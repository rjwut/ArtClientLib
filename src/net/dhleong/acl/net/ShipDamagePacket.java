package net.dhleong.acl.net;

/** No, this is wrong */
public class ShipDamagePacket extends BaseArtemisPacket {

    public static final int TYPE = 0x0;
//    public static final int TYPE = 0xf5821226;
    
    public ShipDamagePacket(int flags) {
        super(0x01, flags, TYPE, null);
    }

    @Override
    public String toString() {
        return String.format("[1:%-16s:DAMAGE]", Integer.toHexString(mFlags)); 
    }
}
