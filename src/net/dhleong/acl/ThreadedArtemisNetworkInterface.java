package net.dhleong.acl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import net.dhleong.acl.net.PacketParser;

public class ThreadedArtemisNetworkInterface implements ArtemisNetworkInterface {
    
    private static class SenderThread extends Thread implements OnPacketListener {

        private final Socket mSkt;
        private final Queue<ArtemisPacket> mQueue = new ArrayDeque<ArtemisPacket>(128);
        private boolean mRunning = true;
        
        private ArtemisPacket mCurrentPacket = null;
        private final OutputStream mOutput;
        private final ThreadedArtemisNetworkInterface mInterface;
        
        private boolean mConnected = false;
        private OnConnectedListener mOnConnectedListener;

        public SenderThread(ThreadedArtemisNetworkInterface net, Socket skt) throws IOException {
            mInterface = net;
            mSkt = skt;
            
            mOutput = new BufferedOutputStream(mSkt.getOutputStream());
        }
        
        public boolean offer(ArtemisPacket pkt) {
            return mQueue.offer(pkt);
        }

        @Override
        public void run() {
            while (mRunning) {
                
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {}
                
                if (!mConnected )
                    continue;
                
                if (mCurrentPacket == null && mQueue.size() > 0) {
                    mCurrentPacket = mQueue.remove();
                } else if (mCurrentPacket == null) {
                    // empty queue; loop back to wait
                    continue;
                }
            
                try {
//                    System.out.print(">> ");
////                    mCurrentPacket.write(System.out);
//                    System.out.println();
                    if (DEBUG) System.out.println(">> " + mCurrentPacket);
                    
                    if (mCurrentPacket.write(mOutput)) {
                        mCurrentPacket = null;
                        mOutput.flush();
                    }
                } catch (IOException e) {
                    // TODO stop everything?
                    e.printStackTrace();
                    break;
                }
            }
            
            mInterface.stop();
        }

        public void end() {
            mRunning = false;
        }

        @Override
        public void onPacket(ArtemisPacket pkt) {
            if (pkt.getType() == 0x19c6e2d4) { // onConnect
                mConnected = true;
                if (mOnConnectedListener != null)
                    mOnConnectedListener.onConnected();
            }
        }
    }

    private static class ReceiverThread extends Thread {

        private boolean mRunning = true;
        ArrayList<OnPacketListener> mListeners = new ArrayList<OnPacketListener>();
        private final BufferedInputStream mInput;
        private final PacketParser mParser;
        private final ThreadedArtemisNetworkInterface mInterface;
        private final Socket mSocket;
        
        public ReceiverThread(ThreadedArtemisNetworkInterface net, Socket skt) throws IOException {
            mInterface = net;
            mInput = new BufferedInputStream(skt.getInputStream());
            mParser = new PacketParser();
            mSocket = skt;
        }

        @Override
        public void run() {
            while (mRunning) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {}
                
                try {
                    if (mInput.available() < 8) {
                        // nothing, or not enough to at least 
                        //  figure out what we need
                        continue;
                    }
                    
                    try {
                        // read packet
                        ArtemisPacket pkt = mParser.readPacket(mInput);
                        
                        // notify listeners
                        for (OnPacketListener listener : mListeners) {
                            listener.onPacket(pkt);
                        }
                    } catch (ArtemisPacketException e) {
                        // TODO ?
                        e.printStackTrace();
                        break;
                    }
                } catch (IOException e) {
                    // TODO stop everything?
                    e.printStackTrace();
                    break;
                }
            }
            
            // close the socket here;
            try {
                mSocket.close();
            } catch (IOException e) {}
            
            mInterface.stop();
        }
        
        public void end() {
            mRunning = false;
        }

        public void addOnPacketListener(OnPacketListener listener) {
            mListeners.add(listener);
        }
    }
    
    private static final boolean DEBUG = false;

    private final ReceiverThread mReceiveThread;
    private final SenderThread mSendThread;
    
    public ThreadedArtemisNetworkInterface(final String tgtIp, final int tgtPort) 
            throws UnknownHostException, IOException {
        Socket skt = new Socket(tgtIp, tgtPort);
        skt.setKeepAlive(true);
        
        mSendThread = new SenderThread(this, skt);
        mReceiveThread = new ReceiverThread(this, skt);
        mReceiveThread.addOnPacketListener(mSendThread);
    }

    @Override
    public void addOnPacketListener(OnPacketListener listener) {
        mReceiveThread.addOnPacketListener(listener);
    }
    
    public boolean isConnected() {
        return mSendThread.mConnected;
    }

    @Override
    public void send(ArtemisPacket pkt) {
        mSendThread.offer(pkt);
    }
    
    @Override
    public void stop() {
        mReceiveThread.end();
        mSendThread.end();
    }
    
    @Override
    public void start() {
        if (!mReceiveThread.isAlive())
            mReceiveThread.start();
        if (!mSendThread.isAlive())
            mSendThread.start();
    }

    public void setOnConnectedListener(OnConnectedListener listener) {
        mSendThread.mOnConnectedListener = listener;
    }

}
