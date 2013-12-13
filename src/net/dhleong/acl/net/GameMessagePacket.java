package net.dhleong.acl.net;

public class GameMessagePacket extends BaseArtemisPacket {
    public static final int TYPE = 0xf754c8fe;
    private static final int TYPE_GAMEOVER = 0x00000006;
    private static final int TYPE_MESSAGE = 0x0000000a;
    
    private final int mMsgType;
    private final String mMessage;
    
    public GameMessagePacket(byte[] bucket) {
        super(0x01, TYPE, bucket); // TODO don't save the byte[]?
        
        mMsgType = PacketParser.getLendInt(bucket);
        if (mMsgType == TYPE_MESSAGE) {
            int msgLen = PacketParser.getNameLengthBytes(bucket, 4);
            mMessage = PacketParser.getNameString(bucket, 8, msgLen);
        } else {
            mMessage = null;
        }
    }
    
    public String getMessage() {
        return mMessage;
    }
    
    public boolean hasMessage() {
        return mMsgType == TYPE_MESSAGE;
    }

    public boolean isGameOver() {
        return mMsgType == TYPE_GAMEOVER;
    }       
}