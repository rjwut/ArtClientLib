package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPositionable;
import net.dhleong.acl.world.ArtemisStation;

public class StationPacket implements ObjectUpdatingPacket {

    private static final byte NAME    = 0x01;
    private static final byte SHIELDS_FRONT = 0x02;
    private static final byte SHIELDS_REAR  = 0x04;
    private static final byte SKIP_1  = 0x08;
    private static final byte SKIP_2  = 0x10;
    private static final byte POS_X   = 0x20;
    private static final byte POS_Y   = 0x40;
    private static final byte POS_Z   = (byte)0x80;
    
    private static final byte[] UNKNOWN_INTS = new byte[] {
        0x01, 0x02, 0x04, 0x08, 0x01
    };
    
    private static final byte UNKNOWN_BYTE  = 0x20;
    

//    private static final byte ACTION_CREATE = (byte) 0xf0;

    private final byte[] mData;

    private final List<ArtemisPositionable> mCreatedObjs = new ArrayList<ArtemisPositionable>();

    public StationPacket(byte[] data) {
        mData = data;
        
        String name;
        float x, y, z;
        float shieldsFront, shieldsRear;

        ObjectParser p = new ObjectParser(data, 0);
        while (p.hasMore()) {
            p.startNoArgs();

            byte args2 = p.readByte();
            
            name = p.readName(NAME);

            shieldsFront = p.readFloat(SHIELDS_FRONT, -1);
            shieldsRear = p.readFloat(SHIELDS_REAR, -1);

            p.readInt(SKIP_1);
            p.readInt(SKIP_2); 

            x = p.readFloat(POS_X, -1);
            y = p.readFloat(POS_Y, -1);
            z = p.readFloat(POS_Z, -1);
            
            // secondary args... unknown purpose
            p.setArgs(args2);
            
            for (byte arg : UNKNOWN_INTS)
                p.readInt(arg);

            p.readByte(UNKNOWN_BYTE, (byte)-1);
            
            // create the obj!
            ArtemisStation station = new ArtemisStation(p.getTargetId(), name);
            station.setX(x);
            station.setY(y);
            station.setZ(z);
            
            station.setShieldsFront(shieldsFront);
            station.setShieldsRear(shieldsRear);
            
            mCreatedObjs.add(station);

//            // for some reason, station packets are sometimes retarded
//            //  and have lots of 0 padding at the end.
//            while (offset+1 < mData.length && 
//                    mData[offset] != ArtemisObject.TYPE_STATION)
//                offset++;
        }

        //            debugPrint();
        //            System.out.println("DEBUG: Packet = " + this);

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
        return ArtemisPacket.WORLD_TYPE;
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

//    public static boolean isExtensionOf(SystemInfoPacket pkt) {
//        //        return pkt.getAction() == SystemInfoPacket.ACTION_CREATE;
//        // new crazy is temporary as we transition to merged packet
//        return (pkt.getAction() & SystemInfoPacket.ACTION_MASK) 
//                == ACTION_CREATE 
//                //                && (pkt.getTargetType() == ArtemisObject.TYPE_PLAYER 
//                && (pkt.getTargetType() == ArtemisObject.TYPE_STATION);
//    }
    public static final boolean handlesType(int type) {
        return type == ArtemisObject.TYPE_STATION;
    }

    @Override
    public List<ArtemisPositionable> getObjects() {
        return mCreatedObjs;
    }
}
