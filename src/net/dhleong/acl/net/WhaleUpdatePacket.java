package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisCreature;
import net.dhleong.acl.world.ArtemisPositionable;

public class WhaleUpdatePacket implements ObjectUpdatingPacket {
    private enum Bit {
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

    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();
    private byte[] mData;

    public WhaleUpdatePacket(byte[] data) {
    	init(data);
    }

    private void init(byte[] data) {
        mData = data;
        
        try {
            ObjectParser p = new ObjectParser(mData, 0);
            float x, y, z, bearing;
            String name;
            
            while (p.hasMore()) {
                p.start(Bit.values());
                ObjectType type = ObjectType.fromId(p.getTargetType());

                if (type != ObjectType.WHALE) {
                	throw new IllegalArgumentException(
                			"Expected " + ObjectType.WHALE.getId() + ", got " +
                			p.getTargetType()
                	);
                }

                name = p.readName(Bit.NAME);

                p.readUnknown(Bit.UNK_0, 4);
                p.readUnknown(Bit.UNK_1, 4);
                
                x = p.readFloat(Bit.X, -1);
                y = p.readFloat(Bit.Y, -1);
                z = p.readFloat(Bit.Z, -1);
                
                p.readUnknown(Bit.UNK_2, 4);
                p.readUnknown(Bit.UNK_3, 4);

                bearing = p.readFloat(Bit.HEADING, Float.MIN_VALUE);

                p.readUnknown(Bit.UNK_4, 4);
                p.readUnknown(Bit.UNK_5, 4);
                p.readUnknown(Bit.UNK_6, 4);
                p.readUnknown(Bit.UNK_7, 4);
                
                final ArtemisCreature obj = new ArtemisCreature(
                        p.getTargetId(), name, type);
                obj.setX(x);
                obj.setY(y);
                obj.setZ(z);
                obj.setBearing(bearing);
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
    public ConnectionType getConnectionType() {
        return ConnectionType.SERVER;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return false;
    }

    @Override
    public int getType() {
        return ArtemisPacket.WORLD_TYPE;
    }

    @Override
    public List<ArtemisPositionable> getObjects() {
        return mObjects;
    }
    
    @Override
    public void debugPrint() {
        for (ArtemisPositionable u : mObjects) {
            System.out.println("- DEBUG: " + u);
        }
    }
}