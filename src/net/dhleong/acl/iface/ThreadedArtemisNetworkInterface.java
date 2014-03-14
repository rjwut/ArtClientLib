package net.dhleong.acl.iface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Queue;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.Protocol;
import net.dhleong.acl.protocol.UnparsedPacket;
import net.dhleong.acl.protocol.core.GameOverPacket;
import net.dhleong.acl.protocol.core.setup.ReadyPacket;
import net.dhleong.acl.protocol.core.setup.ReadyPacket2;
import net.dhleong.acl.protocol.core.setup.VersionPacket;
import net.dhleong.acl.protocol.core.setup.WelcomePacket;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.util.Util;

/**
 * Default implementation of ArtemisNetworkInterface. Kicks off a thread for
 * each stream.
 */
public class ThreadedArtemisNetworkInterface implements ArtemisNetworkInterface {
    private static final boolean DEBUG = false;

    private final ConnectionType recvType;
    private final ConnectionType sendType;
    private final PacketFactoryRegistry factoryRegistry = new PacketFactoryRegistry();
    private final ReceiverThread mReceiveThread;
    private final SenderThread mSendThread;

    /** Error code, for when we disconnect */
    private int errorCode = OnConnectedListener.ERROR_NONE;

    /**
     * Prepares an outgoing client connection to an Artemis server. The
     * connection won't actually be opened until start() is called.
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
        mReceiveThread.addPacketListener(mSendThread);
    }

    private ThreadedArtemisNetworkInterface pair;
    private PairingPolicy pairingPolicy;

    public static void pair(
    		ThreadedArtemisNetworkInterface iface1,
    		ThreadedArtemisNetworkInterface iface2,
    		PairingPolicy pairingPolicy
    ) {
    	if (iface1 == null || iface2 == null || pairingPolicy == null) {
    		throw new IllegalArgumentException("All arguments are required");
    	}

    	iface1.pair = iface2;
    	iface1.pairingPolicy = pairingPolicy;
    	iface2.pair = iface1;
    	iface2.pairingPolicy = pairingPolicy;
    }

    public ThreadedArtemisNetworkInterface getPair() {
    	return pair;
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
    public void addPacketListener(final Object listener) {
        mReceiveThread.addPacketListener(listener);
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
     * Registers an object that will be notified when a connection to the remote
     * machine is established or terminated.
     */
    public void setOnConnectedListener(final OnConnectedListener listener) {
        mSendThread.mOnConnectedListener = listener;
    }


    /**
	 * Manages sending packets to the OutputStream.
	 */
	private static class SenderThread extends Thread {
        private final Socket mSkt;
        private final Queue<ArtemisPacket> mQueue = new ArrayDeque<ArtemisPacket>(128);
        private boolean mRunning = true;
        
        private ArtemisPacket mCurrentPacket = null;
        private final PacketWriter mWriter;
        private final ThreadedArtemisNetworkInterface mInterface;
        
        private boolean mConnected = false;
        private OnConnectedListener mOnConnectedListener;
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
                
                if (!mConnected) {
                    continue;
                }
                
                if (mCurrentPacket == null && (mCurrentPacket = mQueue.poll()) == null) {
                    // empty queue; loop back to wait
                    continue;
                }
            
                try {
                    if (DEBUG) {
                    	System.out.println("< " + mCurrentPacket);
                    }

                    mCurrentPacket.write(mWriter);
                    mCurrentPacket = null;
                } catch (final IOException e) {
                    if (mRunning) {
                        e.printStackTrace();
                        mInterface.errorCode = OnConnectedListener.ERROR_IO;
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
            
            if (mOnConnectedListener != null) {
                mOnConnectedListener.onDisconnected(mInterface.errorCode);
    
                // not interested in listening anymore
                mOnConnectedListener = null;
            }
        }

        public void end() {
            mRunning = false;
        }

        @PacketListener
        public void onPacket(final WelcomePacket pkt) {
            final boolean wasConnected = mConnected;
            mConnected = true;

            // send a couple of these to prime the server
            // TODO Is this really required?
            //offer(new ReadyPacket2());
            //offer(new ReadyPacket2());
            
            if (!wasConnected && mOnConnectedListener != null) {
                mOnConnectedListener.onConnected();
            }
        }

        @PacketListener
        public void onPacket(final VersionPacket pkt) {
            final float version = pkt.getVersion();

            if (Util.findInArray(ArtemisNetworkInterface.SUPPORTED_VERSIONS, version) == -1) {
                System.err.println(String
                        .format("Unsupported Artemis server version (%f)", version));

                if (mOnConnectedListener != null) {
                    mOnConnectedListener.onDisconnected(
                            OnConnectedListener.ERROR_VERSION);
                }
                
                // go ahead and end the receive thread NOW
                mInterface.errorCode = OnConnectedListener.ERROR_VERSION;
                mInterface.mReceiveThread.end();
                end();
            }
        }

        @PacketListener
        public void onPacket(final GameOverPacket pkt) {
        	offer(new ReadyPacket());
        }
    }

	/**
	 * Manages receiving packets from the InputStream.
	 */
    private class ReceiverThread extends Thread {
        private ListenerRegistry mListeners = new ListenerRegistry();
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
                    	boolean unparsed = pkt instanceof UnparsedPacket;

                    	if (pair != null && (unparsed || pairingPolicy == PairingPolicy.ALL)) {
                    		pair.send(pkt);
                    	}

                    	if (!unparsed) {
                    		mListeners.fire(pkt);
                    	}
                    }
                } catch (final ArtemisPacketException e) {
                    if (mRunning) {
                    	ConnectionType connType = e.getConnectionType();
                    	System.err.println("### PACKET PARSE EXCEPTION! ###");

                    	if (connType != null) {
                    		System.err.println("Connection type: " + connType);
                        	int packetType = e.getPacketType();

                        	if (packetType != 0) {
                        		System.err.println(
                        			"    Packet type: " + packetType
                        		);
                        		byte[] payload = e.getPayload();

                        		if (payload != null) {
                        			System.err.println(
                        				"        Payload: " +
               							TextUtil.byteArrayToHexString(payload)
                        			);
                        		}
                        	}
                    	}

                        e.printStackTrace();
                        mInterface.errorCode = OnConnectedListener.ERROR_PARSE;
                        end();
                    }

                    break;
                }
            }
            
            mInterface.stop();
        }
        
        public void end() {
            mRunning = false;
            mListeners.clear();
        }

    	private void addPacketListener(Object object) {
    		mListeners.register(object);
    	}
    }
}