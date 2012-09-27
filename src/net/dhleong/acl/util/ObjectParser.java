package net.dhleong.acl.util;

import net.dhleong.acl.net.PacketParser;

public class ObjectParser {
    
    private int offset;
    private final byte[] mData;
    
    private byte action;
    private int args;
    
    // per-object stuff
    byte targetType;
    int targetId;

    public ObjectParser(byte[] data, int initialOffset) {
        mData = data;
        offset = initialOffset;
    }
    
    public void start() {
        targetType = mData[offset++];
        targetId = PacketParser.getLendInt(mData, offset);
        
        action = mData[offset+4];
        args = PacketParser.getLendInt(mData, offset+5);
        offset += 9;
    }

    public boolean hasMore() {
//        return offset+10 < mData.length;
        return mData[offset] != 0; // maybe?
    }
    
    public int readInt(byte ifActionByte) {
        if ((action & ifActionByte) != 0) {
            int value = PacketParser.getLendInt(mData, offset); 
            offset += 4; 
            return value;
        }
        
        return -1;
    }
    
    public int readInt(int ifArgsByte) {
        if ((args & ifArgsByte) != 0) {
            int value = PacketParser.getLendInt(mData, offset); 
            offset += 4; 
            return value;
        }
        
        return -1;
    }

    public float readFloat(byte ifActionByte, float defaultValue) {
        if ((action & ifActionByte) != 0) {
            float value = PacketParser.getLendFloat(mData, offset); 
            offset += 4;
            return value;
        } 

        return defaultValue;
    }
    
    public float readFloat(int ifArgByte, float defaultValue) {
        if ((args & ifArgByte) != 0) {
            float value = PacketParser.getLendFloat(mData, offset); 
            offset += 4;
            return value;
        } 

        return defaultValue;
    }
    
    public int readShort(int ifArgsByte) {
        if ((args & ifArgsByte) != 0) {
            int value = PacketParser.getLendShort(mData, offset); 
            offset += 2; 
            return value;
        }
        
        return -1;
    }

    public String readName(byte ifActionByte) {
        if ((action & ifActionByte) != 0) {            
            int nameLen = PacketParser.getNameLengthBytes(mData, offset);
            String name = PacketParser.getNameString(mData, offset+4, nameLen);
            
            offset += 4 + 2 + nameLen;
            
            return name;
        }
        return null;
    }

    public byte readByte(int ifArgsByte, byte defaultValue) {
        if ((args & ifArgsByte) != 0) {
            return mData[offset++];
        }
        
        return defaultValue;
    }
    
    public int getTargetId() {
        return targetId;
    }

    public byte getTargetType() {
        return targetType;
    }
    
//    public byte readByte()
}
