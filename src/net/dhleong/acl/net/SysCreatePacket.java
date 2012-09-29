package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPositionable;
import net.dhleong.acl.world.ArtemisStation;

public class SysCreatePacket implements ArtemisPacket {

    private static final byte FLAG_STATION_SHIELDS = 0x02;
    private static final byte FLAG_STATION_SKIP_1  = 0x04;
    private static final byte FLAG_STATION_SKIP_2  = 0x08;
    private static final byte FLAG_STATION_POS     = 0x10;


    private static final byte ACTION_CREATE = (byte) 0xf0;

    private final byte[] mData;

    private final List<ArtemisPositionable> mCreatedObjs = new ArrayList<ArtemisPositionable>();

    public SysCreatePacket(SystemInfoPacket pkt) {
        mData = pkt.mData;

        switch (pkt.getTargetType()) {
        case ArtemisObject.TYPE_STATION: {

            int offset = 0;
            while (mData[offset] != 0x00) {
                //                // this length does NOT include the obj TYPE and ID
                ////                byte action = mData[offset+5];
                //                int lenSubPacket = (0xff & mData[offset+6]);
                ////                if (lenSubPacket == 0x1f)
                //                if (lenSubPacket != 0x3f)
                //                    lenSubPacket = 62; // seems to be the case...
                final byte args = mData[offset+6];

                int objId = PacketParser.getLendInt(mData, offset+1);
                int nameLen = PacketParser.getNameLengthBytes(mData, offset+7);
                final ArtemisStation station;
                try {
                    String name = PacketParser.getNameString(mData, offset+11, nameLen);
                    station = new ArtemisStation(objId, name);

                    mCreatedObjs.add(station);
                } catch (StringIndexOutOfBoundsException e) {
                    debugPrint();
                    System.out.println("DEBUG: subpLen = " + args);
                    System.out.println("DEBUG: objId   = " + objId);
                    System.out.println("DEBUG: nameLen = " + nameLen);
                    System.out.println("DEBUG: offset = " + offset);
                    System.out.println("DEBUG: Packet = " + this);
                    throw e;
                }
                //                offset += lenSubPacket + 5; // +5 for the TYPE and ID

                offset += 11 + 2 + nameLen; // plus 2 for the null bytes

                if ((args & FLAG_STATION_SHIELDS) != 0) {
                    offset += 8; // TODO get these
                }

                if ((args & FLAG_STATION_SKIP_1) != 0)
                    offset += 4; 
                if ((args & FLAG_STATION_SKIP_2) != 0)
                    offset += 4; 

                if ((args & FLAG_STATION_POS) != 0) {
                    station.setX(PacketParser.getLendFloat(mData, offset));
                    offset += 4;
                    station.setY(PacketParser.getLendFloat(mData, offset));
                    offset += 4;
                    station.setZ(PacketParser.getLendFloat(mData, offset));
                    offset += 4;
                }


                // for some reason, station packets are sometimes retarded
                //  and have lots of 0 padding at the end.
                while (offset+1 < mData.length && 
                        mData[offset] != ArtemisObject.TYPE_STATION)
                    offset++;
            }

            //            debugPrint();
            //            System.out.println("DEBUG: Packet = " + this);
            break; }
            
        }
    }

    public List<ArtemisPositionable> getCreatedObjects() {
        return mCreatedObjs;
    }

    @Override
    public long getMode() {
        return 0x01;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return true;
    }

    @Override
    public int getType() {
        return SystemInfoPacket.TYPE;
    }

    @Override
    public String toString() {
        return BaseArtemisPacket.byteArrayToHexString(mData); 
    }

    public void debugPrint() {
        System.out.println("** CREATE:");
        for (ArtemisObject obj : mCreatedObjs)
            System.out.println("**  + " + obj);
    }

    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        //        return pkt.getAction() == SystemInfoPacket.ACTION_CREATE;
        // new crazy is temporary as we transition to merged packet
        return (pkt.getAction() & SystemInfoPacket.ACTION_MASK) 
                == ACTION_CREATE 
                //                && (pkt.getTargetType() == ArtemisObject.TYPE_PLAYER 
                && (pkt.getTargetType() == ArtemisObject.TYPE_STATION);
    }
}
