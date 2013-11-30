package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisCreature;
import net.dhleong.acl.world.ArtemisGenericObject;
import net.dhleong.acl.world.ArtemisGenericObject.Type;
import net.dhleong.acl.world.ArtemisPositionable;

/**
 * Update/create ArtemisGenericObjects
 * @author dhleong
 *
 */
public class GenericUpdatePacket implements ObjectUpdatingPacket {
	private enum Bit {
    	X,
    	Y,
    	Z,
    	NAME,
    	UNK_0,
    	UNK_1,
    	UNK_2,
    	UNK_3
    }

    private enum WhaleBit {
    	NAME,
    	UNK_0,
    	UNK_1,
    	X,
    	Y,
    	Z,
    	UNK_2,
    	UNK_3,
    	HEADING,
    	UNK_4,
    	UNK_5,
    	UNK_6,
    	UNK_7
    }

    /*
    private static final byte GEN_ACTION_X = 0x01;
    private static final byte GEN_ACTION_Y = 0x02;
    private static final byte GEN_ACTION_Z = 0x04;
    private static final byte GEN_ACTION_NAME    = 0x08;
    
    private static final byte GEN_ACTION_DUNNO_2 = 0x10;
    private static final byte GEN_ACTION_DUNNO_3 = 0x20;
    
    private static final byte TORP_ACTION_DUNNO_1 = 0x40;
    private static final byte TORP_ACTION_DUNNO_2 = (byte) 0x80;
    
    private static final byte WHALE_ACTION_NAME     = 0x01;
    private static final byte WHALE_ACTION_DUNNO_1  = 0x02;
    private static final byte WHALE_ACTION_DUNNO_2  = 0x04;
    private static final byte WHALE_ACTION_X        = 0x08;
    private static final byte WHALE_ACTION_Y        = 0x10;
    private static final byte WHALE_ACTION_Z        = 0x20;
    private static final byte WHALE_ACTION_DUNNO_3  = 0x40;
    private static final byte WHALE_ACTION_DUNNO_4  = (byte) 0x80;
    
    private static final byte WHALE_ARG_BEARING     = 0x01;
    
    private static final byte[] WHALE_ARGS = new byte[] {
        0x02, 0x04, 0x08,
        0x10
    };
    */
    
    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();
    private byte[] mData;

    public GenericUpdatePacket(byte[] data) {
        init(data);
    }
    
    private void init(byte[] data) {
        mData = data;
        
        try {
            ObjectParser p = new ObjectParser(mData, 0);
            float x, y, z, bearing;
            String name;
            
            while (p.hasMore()) {
                p.start();
                Type type = Type.fromInt(p.getTargetType());
                final ArtemisGenericObject obj;

                if (type == Type.WHALE) {
                	p.readBitField(WhaleBit.values());
                    // why are whales so different?
                    name = p.readName(WhaleBit.NAME);

                    p.readInt(WhaleBit.UNK_0);
                    p.readInt(WhaleBit.UNK_1);
                    
                    x = p.readFloat(WhaleBit.X, -1);
                    y = p.readFloat(WhaleBit.Y, -1);
                    z = p.readFloat(WhaleBit.Z, -1);
                    
                    p.readInt(WhaleBit.UNK_2);
                    p.readInt(WhaleBit.UNK_3);

                    bearing = p.readFloat(WhaleBit.HEADING, Float.MIN_VALUE);

                    p.readInt(WhaleBit.UNK_4);
                    p.readInt(WhaleBit.UNK_5);
                    p.readInt(WhaleBit.UNK_6);
                    p.readInt(WhaleBit.UNK_7);
                    
                    ArtemisCreature c = new ArtemisCreature(
                            p.getTargetId(), name, type);
                    c.setBearing(bearing);
                    obj = c;
                } else {
                	p.readBitField(Bit.values());

                	if (type == Type.TORPEDO) {
                        p.readByte();
                    }

                    x = p.readFloat(Bit.X, -1);
                    y = p.readFloat(Bit.Y, -1);
                    z = p.readFloat(Bit.Z, -1);
                    bearing = -1;
    
                    if (type.hasName) {
                        name = p.readName(Bit.NAME);
                    } else {
                        name = null;
                        p.readInt(Bit.NAME);
                    }

                    p.readInt(Bit.UNK_0);
                    p.readInt(Bit.UNK_1);
                    p.readInt(Bit.UNK_2);
                    p.readInt(Bit.UNK_3);
                    
                    obj = new ArtemisGenericObject(p.getTargetId(), name, type);
                }

                obj.setX(x);
                obj.setY(y);
                obj.setZ(z);
                mObjects.add(obj);
            }
        } catch (RuntimeException e) {
            debugPrint();
            System.out.println("--> " + this);
            throw e;
        }
    }
    
    @Override
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
        return ArtemisPacket.WORLD_TYPE;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return false;
    }
    
    @Override
    public String toString() {
        return TextUtil.byteArrayToHexString(mData);
    }

    @Override
    public List<ArtemisPositionable> getObjects() {
        return mObjects;
    }
}