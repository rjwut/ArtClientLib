package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPositionable;
import net.dhleong.acl.world.ArtemisStation;

public class StationPacket implements ObjectUpdatingPacket {
	private enum Bit {
		NAME,
		FORE_SHIELDS,
		AFT_SHIELDS,
		UNK_0,
		UNK_1,
		X,
		Y,
		Z,

		UNK_2,
		UNK_3,
		UNK_4,
		UNK_5,
		UNK_6,
		UNK_7
	}

	/*
    private static final byte NAME    = 0x01;
    private static final byte SHIELDS_FRONT = 0x02;
    private static final byte SHIELDS_REAR  = 0x04;
    private static final byte SKIP_1  = 0x08;
    private static final byte SKIP_2  = 0x10;
    private static final byte POS_X   = 0x20;
    private static final byte POS_Y   = 0x40;
    private static final byte POS_Z   = (byte)0x80;
    
    private static final byte[] UNKNOWN_INTS = new byte[] {
        0x01, 0x02, 0x04, 0x08
    };
    
    private static final byte[] UNKNOWN_BYTES  = new byte[] {
        0x10, 0x20
    };
    */

    private final byte[] mData;
    private final List<ArtemisPositionable> mCreatedObjs = new ArrayList<ArtemisPositionable>();

    public StationPacket(byte[] data) {
        mData = data;
        String name;
        float x, y, z;
        float shieldsFront, shieldsRear;
        ObjectParser p = new ObjectParser(data, 0);

        while (p.hasMore()) {
            p.start(Bit.values());
            
            try {
                name = p.readName(Bit.NAME);
            } catch (StringIndexOutOfBoundsException e) {
                debugPrint();
                System.out.println("DEBUG: Packet = " + this);
                throw e;
            }

            shieldsFront = p.readFloat(Bit.FORE_SHIELDS, -1);
            shieldsRear = p.readFloat(Bit.AFT_SHIELDS, -1);

            p.readUnknown(Bit.UNK_0, 4);
            p.readUnknown(Bit.UNK_1, 4); // hull ID?

            x = p.readFloat(Bit.X, -1);
            y = p.readFloat(Bit.Y, -1);
            z = p.readFloat(Bit.Z, -1);

            p.readUnknown(Bit.UNK_2, 4);
            p.readUnknown(Bit.UNK_3, 4);
            p.readUnknown(Bit.UNK_4, 4);
            p.readUnknown(Bit.UNK_5, 4);
            p.readUnknown(Bit.UNK_6, 1);
            p.readUnknown(Bit.UNK_7, 1);
            
            ArtemisStation station = new ArtemisStation(p.getTargetId(), name);
            station.setX(x);
            station.setY(y);
            station.setZ(z);
            station.setShieldsFront(shieldsFront);
            station.setShieldsRear(shieldsRear);
            station.setUnknownFields(p.getUnknownFields());
            mCreatedObjs.add(station);
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
        return ArtemisPacket.WORLD_TYPE;
    }

    @Override
    public String toString() {
        return TextUtil.byteArrayToHexString(mData); 
    }

    @Override
    public void debugPrint() {
        System.out.println("** CREATE:");
        for (ArtemisObject obj : mCreatedObjs)
            System.out.println("**  + " + obj);
    }

    public static final boolean handlesType(int type) {
        return type == ArtemisObject.TYPE_STATION;
    }

    @Override
    public List<ArtemisPositionable> getObjects() {
        return mCreatedObjs;
    }
}
