package net.dhleong.acl.util;

import java.lang.String;

import net.dhleong.acl.net.PacketParser;

public class ObjectParser {
    
    private int offset;
    private final byte[] mData;
    
    private byte action;
    private int args;
    private long longArgs;
    
    // per-object stuff
    byte targetType;
    int targetId;

    public ObjectParser(byte[] data, int initialOffset) {
        mData = data;
        offset = initialOffset;
    }
    
    public byte getAction() {
        return action;
    }
    
    public boolean has(byte ifActionByte) {
        return (action & ifActionByte) != 0;
    }

    public boolean has(int ifArgsByte) {
        return (args & ifArgsByte) != 0;
    }

    public boolean has(long ifArgsLong) {
        return (longArgs & ifArgsLong) != 0;
    }

    public boolean hasMore() {
//        return offset+10 < mData.length;
        return offset < mData.length && mData[offset] != 0; // maybe?
    }

    public byte peekByte() {
        return mData[offset];
    }
    
    public int readInt() {
        int value = PacketParser.getLendInt(mData, offset); 
        offset += 4; 
        return value;
    }
    
    public int readInt(byte ifActionByte) {
        if ((action & ifActionByte) != 0) {
            return readInt();
        }
        
        return -1;
    }
    
    public int readInt(int ifArgsByte) {
        if ((args & ifArgsByte) != 0) {
            return readInt();
        }
        
        return -1;
    }

    public int readInt(long ifArgsLong) {
        return readInt(ifArgsLong, -1);
    }

    public int readInt(long ifArgsLong, int defaultValue) {
        if ((longArgs & ifArgsLong) != 0) {
            return readInt();
        }
        
        return defaultValue;
    }
    /**
     * If you do your own checking, then 
     *  read a float manually with this
     * @return
     */
    public float readFloat() {
        float value = PacketParser.getLendFloat(mData, offset); 
        offset += 4;
        return value;
    }

    public float readFloat(byte ifActionByte, float defaultValue) {
        if ((action & ifActionByte) != 0) {
            return readFloat();
        } 

        return defaultValue;
    }
    
    public float readFloat(int ifArgByte, float defaultValue) {
        if ((args & ifArgByte) != 0) {
            return readFloat();
        } 

        return defaultValue;
    }
    
    public float readFloat(long ifArgLong, float defaultValue) {
        if ((longArgs & ifArgLong) != 0) {
            return readFloat();
        } 

        return defaultValue;
    }

    public long readLong() {
        long value = PacketParser.getLendLong(mData, offset); 
        offset += 8; 
        return value;
    }
    
    public long readLong(int ifArgsByte) {
        if ((args & ifArgsByte) != 0) {
            return readLong();
        }
        
        return -1;
    }

    public long readLong(long ifArgLong) {
        if ((longArgs & ifArgLong) != 0) {
            return readLong();
        }

        return -1L;
    }
    
    public int readShort() {
        int value = PacketParser.getLendShort(mData, offset); 
        offset += 2; 
        return value;
    }
    
    public int readShort(byte ifActionByte) {
        if ((action & ifActionByte) != 0) {
            return readShort();
        }
        
        return -1;
    }
    
    public int readShort(int ifArgsByte) {
        if ((args & ifArgsByte) != 0) {
            return readShort();
        }
        
        return -1;
    }

    public int readShort(long ifArgsLong) {
        if ((longArgs & ifArgsLong) != 0) {
            return readShort();
        }
        
        return -1;
    }

    public String readName() {
        int nameLen = PacketParser.getNameLengthBytes(mData, offset);
        String name = PacketParser.getNameString(mData, offset+4, nameLen);
        
        offset += 4 + 2 + nameLen;
        
        return name;
    }

    public String readName(byte ifActionByte) {
        if ((action & ifActionByte) != 0) {
            return readName();
        }
        return null;
    }

    public String readName(long ifArgLong) {
        if ((longArgs & ifArgLong) != 0) {
            return readName();
        }

        return null;
    }

    public byte readByte() {
        return mData[offset++];
    }

    public byte readByte(byte ifActionByte, byte defaultValue) {
        if ((action & ifActionByte) != 0) {
            return readByte();
        }

        return defaultValue;
    }

    public byte readByte(int ifArgsByte, byte defaultValue) {
        if ((args & ifArgsByte) != 0) {
            return readByte();
        }
        
        return defaultValue;
    }
    
    public int readByte(long ifArgsLong, int defaultValue) {
        if ((longArgs & ifArgsLong) != 0) {
            return readByte();
        }
        
        return defaultValue;
    }

    public int getTargetId() {
        return targetId;
    }

    public byte getTargetType() {
        return targetType;
    }
    
    public void skip(int bytes) {
        offset += bytes;
    }

    public void start() {
        start(false);
    }

    public void start(boolean useLong) {
        targetType = mData[offset++];
        targetId = PacketParser.getLendInt(mData, offset);
        
        action = mData[offset+4];
        
        if (useLong) {
            longArgs = PacketParser.getLendLong(mData, offset+5);
            offset += 13;
        } else {
            args = PacketParser.getLendInt(mData, offset+5);
            offset += 9;
        }
    }

    /**
     * If your packet only has action and no args
     */
    public void startNoArgs() {
        targetType = mData[offset++];
        targetId = PacketParser.getLendInt(mData, offset);
        
        action = mData[offset+4];
        offset += 5;
    }
}
