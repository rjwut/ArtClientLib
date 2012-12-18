package net.dhleong.acl.net;

public class GameStartPacket extends BaseArtemisPacket {
    
    public static final int MSG_TYPE = 0x0;

    public static final int TYPE = 0xf754c8fe;
    
    private final int mNumber, mOffset;
    
    public GameStartPacket(int flags, byte[] bucket) {
        super(0x01, flags, TYPE, bucket); // TODO don't save the byte[]?
     
        mNumber = PacketParser.getLendInt(bucket, 4);
        mOffset = PacketParser.getLendInt(bucket, 8);
    }
    
    /** No idea what this is */
    public int getNumber() {
        return mNumber;
    }

    /**
     * IDs starting offset...?
     * @return
     */
    public int getOffset() {
        return mOffset;
    }
    
    public void debugPrint() {
        System.out.println("\n\n\n\n***\n*** GAME START: " + mNumber + 
                " / " + mOffset + //Integer.toHexString(mOffset) +
                "\n***\n\n\n\n");
    }
}
