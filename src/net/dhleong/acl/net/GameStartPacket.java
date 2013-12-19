package net.dhleong.acl.net;

import net.dhleong.acl.enums.ConnectionType;

public class GameStartPacket extends BaseArtemisPacket {
    public static final int TYPE = 0xf754c8fe;
    public static final int MSG_TYPE = 0x0;
    
    private final int mNumber, mOffset;
    
    public GameStartPacket(byte[] bucket) {
        super(ConnectionType.SERVER, TYPE, bucket); // TODO don't save the byte[]?
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