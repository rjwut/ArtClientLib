package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisCreature;
import net.dhleong.acl.world.ArtemisGenericObject;
import net.dhleong.acl.world.ArtemisGenericObject.Type;
import net.dhleong.acl.world.ArtemisPositionable;

/**
 * Update/create ArtemisGenericObjects
 * @author dhleong
 *
 */
public class GenericUpdatePacket implements ArtemisPacket {
    

    private static final byte GEN_ACTION_X = 0x01;
    private static final byte GEN_ACTION_Y = 0x02;
    private static final byte GEN_ACTION_Z = 0x04;
    private static final byte GEN_ACTION_NAME    = 0x08;
    
    private static final byte GEN_ACTION_DUNNO_2 = 0x10;
    private static final byte GEN_ACTION_DUNNO_3 = 0x20;
    
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
    
    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();
    private byte[] mData;
    
    public GenericUpdatePacket(SystemInfoPacket pkt) {
        ArtemisGenericObject.Type type = ArtemisGenericObject.Type
                .fromInt(pkt.getTargetType());
        if (type == null)
            return; // unhandled type
        
        init(pkt.mData);
    }
    
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
                p.startNoArgs();
                
                Type type = Type.fromInt(p.getTargetType());
                
                final ArtemisGenericObject obj;
                if (type == Type.WHALE) {
                    // why are whales so different?
                    byte whaleArgs = 0;
                    whaleArgs = p.readByte();
                    
                    name = p.readName(WHALE_ACTION_NAME);
                    
                    p.readInt(WHALE_ACTION_DUNNO_1);
                    p.readInt(WHALE_ACTION_DUNNO_2);
                    
                    x = p.readFloat(WHALE_ACTION_X, -1);
                    y = p.readFloat(WHALE_ACTION_Y, -1);
                    z = p.readFloat(WHALE_ACTION_Z, -1);
                    
                    p.readInt(WHALE_ACTION_DUNNO_3);
                    p.readInt(WHALE_ACTION_DUNNO_4);
                    
                    if ((whaleArgs & WHALE_ARG_BEARING) != 0)
                        bearing = p.readFloat();
                    else
                        bearing = Float.MIN_VALUE;
                    
                    // read off extra args that I dunno what they are
                    for (int arg : WHALE_ARGS) {
                        if ((whaleArgs & arg) != 0)
                            p.readInt();
                    }
                    
                    ArtemisCreature c = new ArtemisCreature(
                            p.getTargetId(), name, type);
                    c.setBearing(bearing);
                    obj = c;
                } else {

                    x = p.readFloat(GEN_ACTION_X, -1);
                    y = p.readFloat(GEN_ACTION_Y, -1);
                    z = p.readFloat(GEN_ACTION_Z, -1);
                    bearing = -1;
    
                    if (type.hasName) {
                        name = p.readName(GEN_ACTION_NAME);
                    } else {
                        name = null;
                        p.readInt(GEN_ACTION_NAME);
                    }
                    p.readInt(GEN_ACTION_DUNNO_2);
                    p.readInt(GEN_ACTION_DUNNO_3);
                    
                    obj = new ArtemisGenericObject(
                            p.getTargetId(), name, type);
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
    public boolean write(OutputStream os) throws IOException {
        return false;
    }
    
    @Override
    public String toString() {
        return BaseArtemisPacket.byteArrayToHexString(mData);
    }
    
    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        // any generic object
        return ArtemisGenericObject.Type.fromInt(pkt.getTargetType()) != null;
    }

}
