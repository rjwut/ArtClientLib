package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.world.ArtemisObject;

public class ObjUpdatePacket implements ArtemisPacket {

    public static class ObjUpdate {

        public final float x, y, z;

        /** in radians, where 0 is straight down? */
        public final float bearing;

        public final int targetId;
        public final byte targetType;

        public final boolean scanned;

        private ObjUpdate(byte targetType, int targetId,
                float x, float y, float z, float bearing, boolean scanned) {
            this.targetType = targetType;
            this.targetId = targetId;
            this.x = x;
            this.y = y;
            this.z = z;
            this.bearing = bearing;
            this.scanned = scanned;
        }

        public void debugPrint() {
            System.out.println(String.format("\nUpdate: [%d]%s", 
                    targetType, Integer.toHexString(targetId)));
            System.out.println(String.format("* Position: %.2f, %.2f, %.2f", x, y, z));
            System.out.println(String.format("*  Bearing: %.2f", bearing));
            System.out.println(String.format("*  Scanned: %b", scanned));
        }
    }

    private static final byte ACTION_UPDATE_BYTE  = (byte) 0x80;
    
    private static final byte ACTION_SKIP_BYTES_1 = (byte) 0x02;
    private static final byte ACTION_SKIP_BYTES_2 = (byte) 0x04;

    private static final int POS_Z       = 0x00000002; // huh?
    private static final int POS_Y       = 0x00000001; // wtf?
    private static final int DUNNO_SKIP  = 0x00000008; 
    private static final int BEARING     = 0x00000010; 
    private static final int DUNNO_SKIP_2= 0x00000020; // wtf?
    private static final int DUNNO_SKIP_3= 0x00000080; // wtf?
    private static final int ELITE       = 0x00008000; // just a guess
    
    private static final int SCANNED     = 0x00020000; // I think?


    private final byte[] mData;

    public final List<ObjUpdate> mUpdates = new ArrayList<ObjUpdate>();


    public ObjUpdatePacket(final SystemInfoPacket pkt) {

        mData = pkt.mData;

        float x, y, z, bearing;
        byte targetType;
        int targetId;

        int base = 0;
        while (base+10 < mData.length) {
            try {
                targetType = mData[base];
                targetId = PacketParser.getLendInt(mData, base+1);

                byte action = mData[base+5];
                int args = PacketParser.getLendInt(mData, base+6);
                boolean scanned = false;

                int offset = base+10;
                if ((action & ACTION_SKIP_BYTES_1) != 0)
                    offset += 4; // I have no idea what this is...
                if ((action & ACTION_SKIP_BYTES_2) != 0)
                    offset += 4; // or this

                if ((action & ACTION_UPDATE_BYTE) != 0) {
//                if ((args & POS_X_AND_Z) != 0) {
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

                if ((args & DUNNO_SKIP) != 0)
                    offset += 4;

                if ((args & BEARING) != 0) {
                    bearing = PacketParser.getLendFloat(mData, offset);
                    offset += 4;
                } else {
                    bearing = Float.MIN_VALUE;
                }

                if ((args & DUNNO_SKIP_2) != 0)
                    offset += 4;
                
                if ((args & DUNNO_SKIP_3) != 0)
                    offset += 2; // hurray, a short. wtf.
                
                // don't care right now
                if ((args & ELITE) != 0)
                    offset += 4;
                
                if ((args & SCANNED) != 0) {
                    scanned = mData[offset] != 0;
                    offset++;
                }

                mUpdates.add(new ObjUpdate(targetType, targetId, x, y, z, bearing, scanned));
                base = offset;
            } catch (ArrayIndexOutOfBoundsException e) {
                debugPrint();
                System.out.println("!! DEBUG this = " + 
                        BaseArtemisPacket.byteArrayToHexString(mData));
                throw e;
            }
        }
    }

    public void debugPrint() {
        for (ObjUpdate u : mUpdates) {
            u.debugPrint();
        }
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
                ((pkt.getAction() & SystemInfoPacket.ACTION_MASK) == ACTION_UPDATE_BYTE);
    }

}
