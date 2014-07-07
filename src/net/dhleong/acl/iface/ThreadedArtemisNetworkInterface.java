package net.dhleong.acl.iface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.Protocol;
import net.dhleong.acl.protocol.Version;
import net.dhleong.acl.protocol.core.setup.VersionPacket;
import net.dhleong.acl.protocol.core.setup.WelcomePacket;

/**
 * Default implementation of ArtemisNetworkInterface. Kicks off a thread for
 * each stream.
 */
public class ThreadedArtemisNetworkInterface implements ArtemisNetworkInterface {
    private static final boolean DEBUG = false;

    private final ConnectionType recvType;
    private final ConnectionType sendType;
    private final PacketFactoryRegistry factoryRegistry = new PacketFactoryRegistry();
    private final ListenerRegistry mListeners = new ListenerRegistry();
    private final ReceiverThread mReceiveThread;
    private final SenderThread mSendThread;

    private DisconnectEvent.Cause disconnectCause = DisconnectEvent.Cause.LOCAL_DISCONNECT;
    private Exception exception;

    /**
     * Prepares an outgoing client connection to an Artemis server. The send and
     * receive streams won't actually be opened until start() is called.
     * @param tgtIp The IP address to connect to
     * @param tgtPort The port to connect to (Artemis's default port is 2010)
     */
    public ThreadedArtemisNetworkInterface(final String host, final int port) 
            throws UnknownHostException, IOException {
   		this(new Socket(host, port), ConnectionType.SERVER);
    }

    /**
     * Creates a ThreadedArtemisNetworkInterface instance which will communicate
     * over the given Socket.
     * 
     * The ConnectionType indicates the expected type of the remote machine; in
     * other words, it corresponds to the type of packets that are expected to
     * be received. If ArtClientLib is connecting as a client to a remote
     * server, connType should be ConnectionType.SERVER. (The constructor which
     * accepts a host and port number simplifies this setup.) If ArtClientLib is
     * acting as a proxy server and has accepted a socket connection from a
     * remote client, connType should be ConnectionType.CLIENT.
     * 
     * The send/receive streams won't actually be opened until start() is
     * called.
     * 
     * @param socket The ServerSocket that has received a connection
     */
    public ThreadedArtemisNetworkInterface(final Socket skt,
    		final ConnectionType connType) throws IOException {
    	recvType = connType;
    	sendType = connType.opposite();
    	skt.setKeepAlive(true);
        mSendThread = new SenderThread(this, skt);
        mReceiveThread = new ReceiverThread(this, skt);
        addListener(mSendThread);
    }

    @Override
    public ConnectionType getRecvType() {
    	return recvType;
    }

    @Override
    public ConnectionType getSendType() {
    	return sendType;
    }

    @Override
	public void registerProtocol(Protocol protocol) {
		protocol.registerPacketFactories(factoryRegistry);
	}

    @Override
    public void addListener(final Object listener) {
    	mListeners.register(listener);
    }

    /**
     * By default, ArtClientLib will attempt to parse any packet it receives for
     * which there is a registered interested listener. Known packet types that
     * have no listeners will be discarded without being parsed, and unknown
     * packet types will emit UnknownPackets.
     * 
     * If this is set to false, ArtClientLib will treat all incoming packets as
     * UnknownPackets. This is useful to simply capture the raw bytes for all
     * packets, without attempting to parse them.
     */
    public void setParsePackets(boolean parse) {
    	mReceiveThread.setParsePackets(parse);
    }

    @Override
    public void start() {
        if (!mReceiveThread.mStarted) {
            mReceiveThread.start();
        }

        if (!mSendThread.mStarted) {
            mSendThread.start();
        }
    }

    @Override
    public boolean isConnected() {
        return mSendThread.mConnected;
    }

    @Override
    public void send(final ArtemisPacket pkt) {
    	if (pkt.getConnectionType() != sendType) {
    		throw new IllegalArgumentException(
    				"Can only send " + sendType + " packets"
    		);
    	}

    	mSendThread.offer(pkt);
    }

    @Override
    public void stop() {
        mReceiveThread.end();
        mSendThread.end();
    }


    /**
	 * Manages sending packets to the OutputStream.
	 */
	private static class SenderThread extends Thread {
        private final Socket mSkt;
        private final Queue<ArtemisPacket> mQueue = new ConcurrentLinkedQueue<ArtemisPacket>();
        private boolean mRunning = true;
        
        private final PacketWriter mWriter;
        private final ThreadedArtemisNetworkInterface mInterface;
        
        private boolean mConnected;
        private boolean mStarted;

        public SenderThread(final ThreadedArtemisNetworkInterface net, final Socket skt) throws IOException {
            mInterface = net;
            mSkt = skt;
            OutputStream output = new BufferedOutputStream(mSkt.getOutputStream());
            mWriter = new PacketWriter(output);
        }

        /**
         * Enqueues a packet to be sent.
         */
        public boolean offer(final ArtemisPacket pkt) {
        	return mQueue.offer(pkt);
        }

        @Override
        public void run() {
            mStarted = true;

            while (mRunning) {
                try {
                    Thread.sleep(5);
                } catch (final InterruptedException e) {
                	// TODO Supposed to bail if an InterruptedException is received
                }

                ArtemisPacket pkt = mQueue.poll();

            	if (pkt == null) {
                    // empty queue; loop back to wait
                    continue;
                }

                try {
                    if (DEBUG) {
                    	System.out.println("< " + pkt);
                    }

                    pkt.write(mWriter);
                } catch (final IOException e) {
                    if (mRunning) {
                    	mInterface.disconnectCause = DisconnectEvent.Cause.IO_EXCEPTION;
                    	mInterface.exception = e;
                    }

                    break;
                }
            }

            mConnected = false;
            mInterface.stop();
            
            // close the socket here; this will
            //  allow us to send any closing
            //  packets needed before shutting
            //  down the pipes
            try {
                mSkt.close();
            } catch (final IOException e) {
            	// DON'T CARE
            }

            mInterface.mListeners.fire(new DisconnectEvent(
            		mInterface.disconnectCause,
            		mInterface.exception
            ));
        }

        public void end() {
            mRunning = false;
        }

        @Listener
        public void onPacket(final WelcomePacket pkt) {
            final boolean wasConnected = mConnected;
            mConnected = true;

            if (!wasConnected) {
            	mInterface.mListeners.fire(new ConnectionSuccessEvent());
            }
        }

        @Listener
        public void onPacket(final VersionPacket pkt) {
            final Version version = pkt.getVersion();

            if (version.lt(ArtemisNetworkInterface.MIN_VERSION)) {
            	mInterface.mListeners.fire(new DisconnectEvent(
            			DisconnectEvent.Cause.UNSUPPORTED_SERVER_VERSION,
            			null
            	));
                
                // go ahead and end the receive thread NOW
                mInterface.mReceiveThread.end();
                end();
            }
        }
    }

	/**
	 * Manages receiving packets from the InputStream.
	 */
    private class ReceiverThread extends Thread {
        private boolean mRunning = true;
        private final ThreadedArtemisNetworkInterface mInterface;
        private PacketReader mReader;
        private boolean mStarted;
        
        public ReceiverThread(final ThreadedArtemisNetworkInterface net, final Socket skt) throws IOException {
            mInterface = net;
            InputStream input = new BufferedInputStream(skt.getInputStream());
            mReader = new PacketReader(net.getRecvType(), input,
            		factoryRegistry, mListeners);
        }

        private void setParsePackets(boolean parse) {
        	mReader.setParsePackets(parse);
        }

        @Override
        public void run() {
            mStarted = true;
            
            while (mRunning) {
                try {
                    // read packet
                    final ArtemisPacket pkt = mReader.readPacket();

                    if (DEBUG) {
                    	System.out.println("> " + pkt);
                    }

                    if (mRunning) {
                		mListeners.fire(pkt);
                    }
                } catch (final ArtemisPacketException e) {
                    if (mRunning) {
                    	Throwable cause = e.getCause();

                    	if (cause instanceof SocketException) {
                    		// Parse failed because the connection was lost
                    		mInterface.disconnectCause = DisconnectEvent.Cause.REMOTE_DISCONNECT;
                        	mInterface.exception = (SocketException) cause;
                    	} else {
                        	mInterface.disconnectCause = DisconnectEvent.Cause.PACKET_PARSE_EXCEPTION;
                        	mInterface.exception = e;
                    	}

                        end();
                    }

                    break;
                }
            }
            
            mInterface.stop();
        }

        public void end() {
            mRunning = false;
        }
    }
}