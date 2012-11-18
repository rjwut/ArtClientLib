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
        private boolean mStarted;

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
            mStarted = true;
            
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
                    e.printStackTrace();
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
            } catch (IOException e) {}
        }

        public void end() {
            mRunning = false;
            
            // also, not interested in listening anymore
            mOnConnectedListener = null;
        }

        @Override
        public void onPacket(ArtemisPacket pkt) {
            if (pkt.getType() == 0x19c6e2d4) { // onConnect
                
                final boolean wasConnected = mConnected;
                
                mConnected = true;
                
                if (!wasConnected && mOnConnectedListener != null) 
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
        private boolean mStarted;
        
        public ReceiverThread(ThreadedArtemisNetworkInterface net, Socket skt) throws IOException {
            mInterface = net;
            mInput = new BufferedInputStream(skt.getInputStream());
            mParser = new PacketParser();
        }

        @Override
        public void run() {
            mStarted = true;
            
            while (mRunning) {
//                try {
//                    Thread.sleep(5);
//                } catch (InterruptedException e) {}
                
//                try {
                    // not sure this is actually useful
//                    if (mInput.available() < 8) {
//                        // nothing, or not enough to at least 
//                        //  figure out what we need
//                        continue;
//                    }
                    
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
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            
            mInterface.stop();
        }
        
        public void end() {
            mRunning = false;
            
            // also, not interested in listening anymore
            mListeners.clear();
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
        if (!mReceiveThread.mStarted)
            mReceiveThread.start();
        if (!mSendThread.mStarted)
            mSendThread.start();
    }

    public void setOnConnectedListener(OnConnectedListener listener) {
        mSendThread.mOnConnectedListener = listener;
    }

}
