package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisGenericObject;
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
            float x, y, z;
            String name;
            
            while (p.hasMore()) {
                p.start(Bit.values());
                ObjectType type = ObjectType.fromId(p.getTargetType());

            	if (type == ObjectType.TORPEDO) {
                    p.readUnknown("UNK_TORPEDO", 1);
                }

                x = p.readFloat(Bit.X, -1);
                y = p.readFloat(Bit.Y, -1);
                z = p.readFloat(Bit.Z, -1);

                if (type.isNamed()) {
                    name = p.readName(Bit.NAME);
                } else {
                    name = null;
                    p.readInt(Bit.NAME);
                }

                p.readUnknown(Bit.UNK_0, 4);
                p.readUnknown(Bit.UNK_1, 4);
                p.readUnknown(Bit.UNK_2, 4);
                p.readUnknown(Bit.UNK_3, 4);
                
                final ArtemisGenericObject obj = new ArtemisGenericObject(
                		p.getTargetId(), name, type);
                obj.setX(x);
                obj.setY(y);
                obj.setZ(z);
                obj.setUnknownFields(p.getUnknownFields());
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
    public ConnectionType getConnectionType() {
        return ConnectionType.SERVER;
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