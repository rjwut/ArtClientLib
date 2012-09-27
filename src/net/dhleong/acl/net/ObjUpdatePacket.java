package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisEnemy;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisOtherShip;
import net.dhleong.acl.world.ArtemisPositionable;

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
    
    private static final byte ACTION_NAME_BYTE    = (byte) 0x01;
    private static final byte ACTION_SKIP_BYTES_1 = (byte) 0x02;
    private static final byte ACTION_SKIP_BYTES_2 = (byte) 0x04;

    private static final int POS_Z       = 0x00000002; // huh?
    private static final int POS_Y       = 0x00000001; // wtf?
    private static final int DUNNO_SKIP  = 0x00000008; 
    private static final int BEARING     = 0x00000010; 
    private static final int DUNNO_SKIP_2= 0x00000020; // wtf?
    private static final int DUNNO_SKIP_3= 0x00000080; // wtf?
    private static final int DUNNO_NEW_1 = 0x00004000; // ?
    private static final int ELITE       = 0x00008000; // just a guess
    
    private static final int SCANNED     = 0x00020000; // I think?
    
    private static final int DUNNO_NEW_2 = 0x04000000; // ?
    private static final int DUNNO_NEW_3 = 0x08000000; // ?
    private static final int DUNNO_NEW_4 = 0x10000000; // ?
    private static final int DUNNO_NEW_5 = 0x20000000; // ?
    private static final int DUNNO_NEW_6 = 0x40000000; // ?


    private final byte[] mData;

    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();


    public ObjUpdatePacket(final SystemInfoPacket pkt) {

        mData = pkt.mData;

        float x, y, z, bearing;
        boolean scanned = false;
        String name = null;
        int hullId = -1;

//        int base = 0;
        ObjectParser p = new ObjectParser(mData, 0);
        while (p.hasMore()) {
            try {
                p.start();
                
                name = p.readName(ACTION_NAME_BYTE);
                
                // no idea what these are
                p.readInt(ACTION_SKIP_BYTES_1);
                p.readInt(ACTION_SKIP_BYTES_2);

                x = p.readInt(ACTION_UPDATE_BYTE);
                y = p.readFloat(POS_Y, -1);
                z = p.readFloat(POS_Z, -1);
                
                p.readInt(DUNNO_SKIP);

                bearing = p.readFloat(BEARING, Float.MIN_VALUE);

                p.readInt(DUNNO_SKIP_2);
                p.readShort(DUNNO_SKIP_3);
                
                p.readShort(DUNNO_NEW_1);

                // don't care right now
                p.readInt(ELITE);
                
                scanned = p.readByte(SCANNED, (byte) 0) != 0;
                
                p.readInt(DUNNO_NEW_2);
                p.readInt(DUNNO_NEW_3);
                p.readInt(DUNNO_NEW_4);
                p.readInt(DUNNO_NEW_5);
                p.readInt(DUNNO_NEW_6);
                
                final ArtemisPositionable newObj;
                switch (p.getTargetType()) {
                default:
                case ArtemisObject.TYPE_ENEMY:
                    ArtemisEnemy enemy = new ArtemisEnemy(p.getTargetId(), name, hullId);
                    enemy.setBearing(bearing);
                    if (scanned)
                        enemy.setScanned();
                    newObj = enemy;
                    break;
                case ArtemisObject.TYPE_OTHER:
                    ArtemisOtherShip other = new ArtemisOtherShip(
                            p.getTargetId(), name, hullId);
                    other.setBearing(bearing);
                    newObj = other;
                    break;
                }
                
                // shared update
                newObj.setX(x);
                newObj.setY(y);
                newObj.setZ(z);

                mObjects.add(newObj);
//                base = offset;
            } catch (ArrayIndexOutOfBoundsException e) {
                debugPrint();
                System.out.println("!! DEBUG this = " + 
                        BaseArtemisPacket.byteArrayToHexString(mData));
                throw e;
            }
        }
    }

    public void debugPrint() {
        for (ArtemisPositionable u : mObjects) {
            System.out.println("- DEBUG: " + u);
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
                pkt.getTargetType() == ArtemisObject.TYPE_OTHER);
//                && 
//                ((pkt.getAction() & SystemInfoPacket.ACTION_MASK) == ACTION_UPDATE_BYTE);
    }

}
