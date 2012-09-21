package net.dhleong.acl.net;

import java.io.IOException;
import java.io.InputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;

public class PacketParser {
    
    private final byte[] mIntBuffer = new byte[4];

    public ArtemisPacket readPacket(InputStream is) throws IOException, ArtemisPacketException {
        is.read(mIntBuffer);
        final int header = getLendInt(mIntBuffer);
        if (header != 0xdeadbeef) {
            throw new ArtemisPacketException("Illegal packet header: " + Integer.toHexString(header));
        }
        
        is.read(mIntBuffer);
        final int len = getLendInt(mIntBuffer);
        if (len <= 8) {
            return new BaseArtemisPacket();
        }
        
        is.read(mIntBuffer);
        final int mode = getLendInt(mIntBuffer);
        if (mode != 1 && mode != 2) {
            throw new ArtemisPacketException("Unknown packet mode: " + mode);
        }
        
        is.read(mIntBuffer);
        final int modeIsh = getLendInt(mIntBuffer);
        if (modeIsh != 0) {
            throw new ArtemisPacketException("No empty padding after 4-byte mode?");
        }
        
        is.read(mIntBuffer);
        final int flags = getLendInt(mIntBuffer);
        
        is.read(mIntBuffer);
        final int packetType = getLendInt(mIntBuffer);
        
        // for now, just shove it in a byte[]
        // len - 24 because the length includes the entire packet;
        //  above, we read out the first six ints
        int remaining = len-24, offset = 0, read = 0;
        byte[] bucket = new byte[remaining];
        while (remaining > 0 && read > -1) {
//            System.out.println("<< (of " + len +") left: " + remaining + "; " + offset);
            read = is.read(bucket, offset, remaining);
            offset += read;
            remaining -= read;
        }
        
        if (read == -1) {
            throw new ArtemisPacketException("EOF");
        }
        
        switch (packetType) {
        case SystemInfoPacket.TYPE:
            // TODO we could directly return subtypes
            return new SystemInfoPacket(flags, bucket);
            
//        case ShipDamagePacket.TYPE:
//            return new ShipDamagePacket(flags);
            
        case CommsIncomingPacket.TYPE:
            return new CommsIncomingPacket(flags, bucket);
            
        case DestroyObjectPacket.TYPE:
            return new DestroyObjectPacket(flags, bucket);
        
        default:
            return new BaseArtemisPacket(mode, flags, packetType, bucket);
        }        
    }
    
    protected static int getNameLengthBytes(byte[] mData, int offset) {
        final int nameLen = getLendInt(mData, offset);
        return (nameLen-1) * 2;
    }

    public static int getLendInt(byte[] bytes) {
        return getLendInt(bytes, 0);
    }
    
    public static int getLendInt(byte[] bytes, int offset) {
        return ( 
                (0xff & bytes[offset+3]) << 24  |
                (0xff & bytes[offset+2]) << 16  |
                (0xff & bytes[offset+1]) << 8   |
                (0xff & bytes[offset]) << 0
                );
    }
    
    public static int getLendLong(byte[] bytes, int offset) {
        return (
                (0xff & bytes[offset+7]) << 56  |
                (0xff & bytes[offset+6]) << 48  |
                (0xff & bytes[offset+5]) << 40  |
                (0xff & bytes[offset+4]) << 32  |
                (0xff & bytes[offset+3]) << 24  |
                (0xff & bytes[offset+2]) << 16  |
                (0xff & bytes[offset+1]) << 8   |
                (0xff & bytes[offset]) << 0
                );
    }
    
    public static int getLendShort(byte[] bytes, int offset) {
        return ( 
                (0xff & bytes[1]) << 8   |
                (0xff & bytes[0]) << 0
                );
    }

    public static float getLendFloat(byte[] bytes, int offset) {
        int bits = getLendInt(bytes, offset);
        return Float.intBitsToFloat(bits);
    }

    public static void putLendInt(int value, byte[] bytes) {
        putLendInt(value, bytes, 0);
    }

    public static void putLendInt(int value, byte[] bytes, int offset) {
    
        bytes[offset+3] = (byte) (0xff & (value >> 24));
        bytes[offset+2] = (byte) (0xff & (value >> 16));
        bytes[offset+1] = (byte) (0xff & (value >> 8));
        bytes[offset] = (byte) (0xff & (value >> 0));  
        
    }

    public static void putLendFloat(float value, byte[] bytes, int offset) {
        final int intValue = Float.floatToRawIntBits(value);
        
        bytes[offset+3] = (byte) (0xff & (intValue >> 24));
        bytes[offset+2] = (byte) (0xff & (intValue >> 16));
        bytes[offset+1] = (byte) (0xff & (intValue >> 8));
        bytes[offset] = (byte) (0xff & (intValue >> 0));         
    
    }
}
