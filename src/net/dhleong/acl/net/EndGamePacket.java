package net.dhleong.acl.net;

public class EndGamePacket extends BaseArtemisPacket {

    public static final int TYPE = 0xf754c8fe;
    
    public EndGamePacket(int flags, byte[] bucket) {
        super(0x01, flags, TYPE, bucket); // TODO don't save the byte[]?
    }
        
}
