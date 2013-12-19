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
import net.dhleong.acl.world.ArtemisDrone;
import net.dhleong.acl.world.ArtemisPositionable;

public class DroneUpdatePacket implements ObjectUpdatingPacket {
    public enum Bit {
    	UNK_0,
    	X,
    	UNK_2,
    	Z,
    	UNK_4,
    	Y,
    	HEADING,
    	UNK_7,

    	UNK_8,
    	UNK_9,
    	UNK_10,
    	UNK_11,
    	UNK_12,
    	UNK_13,
    	UNK_14,
    	UNK_15
    }

    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();
    private byte[] mData;

    public DroneUpdatePacket(byte[] data) {
        init(data);
    }
   
    private void init(byte[] data) {
        mData = data;
        
        try {
            ObjectParser p = new ObjectParser(mData, 0);
            float x, y, z, bearing;
            
            while (p.hasMore() && p.peekByte() == ObjectType.DRONE.getId()) {
                p.start(Bit.values());
            	p.readUnknown(Bit.UNK_0, 4);
            	x = p.readFloat(Bit.X, -1);
            	p.readUnknown(Bit.UNK_2, 4);
            	z = p.readFloat(Bit.Z, -1);
            	p.readUnknown(Bit.UNK_4, 4);
            	y = p.readFloat(Bit.Y, -1);
            	bearing = p.readFloat(Bit.HEADING, -1);
            	p.readUnknown(Bit.UNK_7, 4);
            	p.readUnknown(Bit.UNK_8, 4);
            	p.readUnknown(Bit.UNK_9, 4);
            	p.readUnknown(Bit.UNK_10, 4);
            	p.readUnknown(Bit.UNK_11, 4);
            	p.readUnknown(Bit.UNK_12, 4);
            	p.readUnknown(Bit.UNK_13, 4);
            	p.readUnknown(Bit.UNK_14, 4);
            	p.readUnknown(Bit.UNK_15, 4);
                final ArtemisDrone obj = new ArtemisDrone(p.getTargetId());
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
    public void debugPrint() {
        for (ArtemisPositionable u : mObjects) {
            System.out.println("- DEBUG: " + u);
        }
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.CLIENT;
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