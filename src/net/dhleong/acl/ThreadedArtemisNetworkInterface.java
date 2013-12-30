package net.dhleong.acl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.GameOverPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.net.PacketWriter;
import net.dhleong.acl.net.setup.ReadyPacket;
import net.dhleong.acl.net.setup.ReadyPacket2;
import net.dhleong.acl.net.setup.VersionPacket;
import net.dhleong.acl.net.setup.WelcomePacket;
import net.dhleong.acl.util.Util;

/**
 * Default implementation of ArtemisNetworkInterface. Kicks off a thread for
 * each stream.
 */
public class ThreadedArtemisNetworkInterface implements ArtemisNetworkInterface {
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
                    	System.out.println(">> " + mCurrentPacket);
                    }

                    mCurrentPacket.write(mWriter);
                    mWriter.flush();
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
            offer(new ReadyPacket2());
            offer(new ReadyPacket2());
            
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
    private static class ReceiverThread extends Thread {
        private List<Listener> mListeners = new CopyOnWriteArrayList<Listener>();
        private boolean mRunning = true;
        private final ThreadedArtemisNetworkInterface mInterface;
        private PacketReader mReader;
        private boolean mStarted;
        
        public ReceiverThread(final ThreadedArtemisNetworkInterface net, final Socket skt) throws IOException {
            mInterface = net;
            InputStream input = new BufferedInputStream(skt.getInputStream());
            mReader = new PacketReader(input);
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

                    // only bother with non-null packets; else,
                    //  they are just keepalive (I guess) for 
                    //  indicating that the server aliveness
                    //  and perhaps calculating latency
                    if (pkt != null && mRunning) {
                    	fireListeners(pkt);
                    }
                } catch (final ArtemisPacketException e) {
                    // TODO ?
                    if (mRunning) {
                        System.err.println("Exitting due to parseException");
                        e.printStackTrace();
                        mInterface.errorCode = OnConnectedListener.ERROR_PARSE;
                    }
                    break;
                }
            }
            
            mInterface.stop();
        }
        
        public void end() {
            mRunning = false;
            
            // also, not interested in listening anymore
            synchronized(mListeners) {
                mListeners.clear();
            }
        }

    	private void addPacketListener(Object object) {
    		Method[] methods = object.getClass().getMethods();

    		for (Method method : methods) {
    			Annotation anno = method.getAnnotation(PacketListener.class);

    			if (anno != null) {
    				mListeners.add(new Listener(object, method));
    			}
    		}
    	}

    	private void fireListeners(ArtemisPacket packet) {
    		for (Listener listener : mListeners) {
    			listener.offer(packet);
    		}
    	}
    }
    
    private static final boolean DEBUG = false;

    private final ReceiverThread mReceiveThread;
    private final SenderThread mSendThread;
    
    /** Error code, for when we disconnect */
    private int errorCode = OnConnectedListener.ERROR_NONE;

    /**
     * @param tgtIp The IP address to connect to
     * @param tgtPort The port to connect to (Artemis's default port is 2010)
     */
    public ThreadedArtemisNetworkInterface(final String tgtIp, final int tgtPort) 
            throws UnknownHostException, IOException {
        final Socket skt = new Socket(tgtIp, tgtPort);
        skt.setKeepAlive(true);
        
        mSendThread = new SenderThread(this, skt);
        mReceiveThread = new ReceiverThread(this, skt);
        mReceiveThread.addPacketListener(mSendThread);
    }

    /**
     * Registers an object as a packet listener. It must have one or more
     * methods annotated with @PacketListener.
     */
    @Override
    public void addPacketListener(final Object listener) {
        mReceiveThread.addPacketListener(listener);
    }

    /**
     * By default, ArtClientLib will attempt to parse all packets it receives.
     * If this is set to false, it will only parse the preambles and emit only
     * UnknownPackets. This basically turns ArtClientLib into a packet sniffer.
     */
    public void setParsePackets(boolean parse) {
    	mReceiveThread.setParsePackets(parse);
    }

    /**
     * Returns true if currently connected to the remote machine; false
     * otherwise.
     */
    public boolean isConnected() {
        return mSendThread.mConnected;
    }

    /**
     * Enqueues a packet to be transmitted to the remote machine.
     */
    @Override
    public void send(final ArtemisPacket pkt) {
    	if (pkt.getConnectionType() != ConnectionType.CLIENT) {
    		throw new IllegalArgumentException("Can only send client packets");
    	}

    	mSendThread.offer(pkt);
    }

    /**
     * Closes the connection to the remote machine.
     */
    @Override
    public void stop() {
        mReceiveThread.end();
        mSendThread.end();
    }

    /**
     * Connects to the remote machine and enables sending and receiving packets.
     */
    @Override
    public void start() {
        if (!mReceiveThread.mStarted) {
            mReceiveThread.start();
        }

        if (!mSendThread.mStarted) {
            mSendThread.start();
        }
    }

    /**
     * Registers an object that will be notified when a connection to the remote
     * machine is established or terminated.
     */
    public void setOnConnectedListener(final OnConnectedListener listener) {
        mSendThread.mOnConnectedListener = listener;
    }


    /**
     * Contains all the information needed to invoke a packet listener method
     * (annotated with @PacketListener).
     * @author rjwut
     */
    private static class Listener {
    	private Object object;
    	private Method method;
    	private Class<?> paramType;

    	/**
    	 * @param object The packet listener object
    	 * @param method The annotated method
    	 */
    	private Listener (Object object, Method method) {
    		validateListenerMethod(method);
    		this.object = object;
    		this.method = method;
    		paramType = method.getParameterTypes()[0];
    	}

    	/**
    	 * Throws an IllegalArgumentException if the given method is not a valid
    	 * packet listener method.
    	 */
    	private static void validateListenerMethod(Method method) {
    		if (!Modifier.isPublic(method.getModifiers())) {
    			throw new IllegalArgumentException(
    					"Method " + method.getName() +
    					" must be public to be a @PacketListener"
    			);
    		}

    		if (!Void.TYPE.equals(method.getReturnType())) {
    			throw new IllegalArgumentException(
    					"Method " + method.getName() +
    					" must return void to be a @PacketListener"
    			);
    		}

    		Class<?>[] paramTypes = method.getParameterTypes();

    		if (paramTypes.length != 1) {
    			throw new IllegalArgumentException(
    					"Method " + method.getName() +
    					" must have exactly one argument"
    			);
    		}

    		Class<?> paramType = paramTypes[0];

    		if (!ArtemisPacket.class.isAssignableFrom(paramType)) {
    			throw new IllegalArgumentException(
    					"Method " + method.getName() +
    					" argument must be an ArtemisPacket or a subtype of it"
    			);
    		}
    	}

    	/**
    	 * Invokes the given listener, passing in the indicated packet. Since
    	 * the listeners have been pre-validated, no exception should occur, so
    	 * we wrap the ones thrown by Method.invoke() in a RuntimeException.
    	 */
    	private void offer(ArtemisPacket packet) {
    		Class<?> clazz = packet.getClass();

    		if (paramType.isAssignableFrom(clazz)) {
        		try {
    				method.invoke(object, packet);
    			} catch (IllegalAccessException ex) {
    				throw new RuntimeException(ex);
    			} catch (IllegalArgumentException ex) {
    				throw new RuntimeException(ex);
    			} catch (InvocationTargetException ex) {
    				throw new RuntimeException(ex);
    			}
    		}
    	}
    }
}