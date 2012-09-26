package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.world.ArtemisObject;

public class ObjUpdatePacket implements ArtemisPacket {
    
    private static final byte ACTION_UPDATE_SYSTEMS_3 = (byte) 0x84;
    
    private static final int POS_X = 0x00000002;
    private static final int POS_Y = 0x00000001;
    private static final int POS_Z = 0x00000010; // wtf?
    
    private final byte[] mData;
    
    private final float x, y, z;

    /** in radians, where 0 is straight down? */
    private final float bearing;

    public ObjUpdatePacket(final SystemInfoPacket pkt) {
        mData = pkt.mData;
        
        int args = PacketParser.getLendInt(pkt.mData, 6);
        
        int offset = 10;
        if (pkt.getAction() == ACTION_UPDATE_SYSTEMS_3)
            offset += 4; // I have no idea what this is... 
        
        if ((args & POS_X) != 0) {
            x = PacketParser.getLendFloat(mData, offset);
            offset += 4;
        } else {
            x = -1;
        }
        if ((args & POS_Y) != 0) {
            y = PacketParser.getLendFloat(mData, offset);
            offset += 4;
        } else {
            y = -1;
        }
        if ((args & POS_Z) != 0) {
            z = PacketParser.getLendFloat(mData, offset);
            offset += 4;
        } else {
            z = -1;
        }
        
        bearing = PacketParser.getLendFloat(mData, mData.length-8);
    }
    
    public void debugPrint() {
        System.out.println(String.format("* Position: %.2f, %.2f, %.2f", x, y, z));
        System.out.println(String.format("*  Bearing: %.2f", bearing));
    }

    @Override
    public long getMode() {
        return 0x01;
    }

    @Override
    public int getType() {
        return SystemInfoPacket.TYPE;
    }

    @Override
    public String toString() {
        return BaseArtemisPacket.byteArrayToHexString(mData); 
    }
    
    @Override
    public boolean write(OutputStream os) throws IOException {
        return true;
    }
    
    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        // this may be a wrong assumption, but I'd think they're the same
        return (pkt.getTargetType() == ArtemisObject.TYPE_ENEMY ||
                pkt.getTargetType() == ArtemisObject.TYPE_OTHER)
                && 
                (pkt.getAction() == SystemInfoPacket.ACTION_UPDATE_SYSTEMS
                    || pkt.getAction() == ACTION_UPDATE_SYSTEMS_3);
    }

}
