package net.dhleong.acl.util;

import net.dhleong.acl.net.PacketParser;

public class ObjectParser {
    private int offset;
    private final byte[] mData;
    private BitField bitField;

    // per-object stuff
    byte targetType;
    int targetId;

    public ObjectParser(byte[] data, int initialOffset) {
        mData = data;
        offset = initialOffset;
    }

    public boolean has(Enum<?> bit) {
    	return bitField.get(bit);
    }

    public boolean hasAny(Enum<?>... bits) {
    	for (Enum<?> bit : bits) {
    		if (bitField.get(bit)) {
    			return true;
    		}
    	}

    	return false;
    }

    public boolean hasAll(Enum<?>... bits) {
    	for (Enum<?> bit : bits) {
    		if (!bitField.get(bit)) {
    			return false;
    		}
    	}

    	return true;
    }

    public boolean hasMore() {
//        System.out.println("hasMore:: " + offset + "< " + mData.length);
//        if (offset < mData.length)
//            System.out.println("hasMore::> " + mData[offset]);
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
    
    public int readInt(Enum<?> bit) {
    	return readInt(bit, -1);
    }
    
    public int readInt(Enum<?> bit, int defaultValue) {
    	return has(bit) ? readInt() : defaultValue;
    }

    /**
     * If you do your own checking, then read a float manually with this.
     */
    public float readFloat() {
        float value = PacketParser.getLendFloat(mData, offset); 
        offset += 4;
        return value;
    }

    public float readFloat(Enum<?> bit, float defaultValue) {
    	return has(bit) ? readFloat() : defaultValue;
    }
    
    public long readLong() {
        long value = PacketParser.getLendLong(mData, offset); 
        offset += 8; 
        return value;
    }
    
    public long readLong(Enum<?> bit) {
    	return has(bit) ? readLong() : -1;
    }

    public int readShort() {
        int value = PacketParser.getLendShort(mData, offset); 
        offset += 2; 
        return value;
    }
    
    public int readShort(Enum<?> bit) {
    	return has(bit) ? readShort() : -1;
    }
    
    public String readName() {
        int nameLen = PacketParser.getNameLengthBytes(mData, offset);
        String name = PacketParser.getNameString(mData, offset+4, nameLen);
        
        offset += 4 + 2 + nameLen;
        
        return name;
    }

    public String readName(Enum<?> bit) {
    	return has(bit) ? readName() : null;
    }
    
    public byte readByte() {
        return mData[offset++];
    }

    public byte readByte(Enum<?> bit, byte defaultValue) {
    	return has(bit) ? readByte() : defaultValue;
    }

    /**
     * Convenience; if we have the arg, parse it
     *  as {@link BoolState#TRUE} if non-zero, else
     *  {@link BoolState#FALSE}; if we don't have
     *  the arg, just return {@link BoolState#UNKNOWN}
     * @param ifArgs
     * @return
     */
    public BoolState readBoolByte(Enum<?> bit) {
        if (has(bit)) {
            return (readByte() != 0) ? BoolState.TRUE : BoolState.FALSE;
        }

        return BoolState.UNKNOWN;
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
        start(null);
    }

    public void start(Enum<?>[] bits) {
    	targetType = mData[offset++];
        targetId = PacketParser.getLendInt(mData, offset);
        offset += 4;

        if (bits != null) {
        	readBitField(bits);
        }
    }

    public void readBitField(Enum<?>[] bits) {
        bitField = new BitField(bits, mData, offset);
        offset += bitField.getByteCount();
    }
}