package net.dhleong.acl.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.net.comms.CommsIncomingPacket;
import net.dhleong.acl.net.comms.IncomingAudioPacket;
import net.dhleong.acl.net.eng.EngGridUpdatePacket;
import net.dhleong.acl.net.helm.JumpStatusPacket;
import net.dhleong.acl.net.player.EngPlayerUpdatePacket;
import net.dhleong.acl.net.player.MainPlayerUpdatePacket;
import net.dhleong.acl.net.player.WeapPlayerUpdatePacket;
import net.dhleong.acl.net.setup.AllShipSettingsPacket;
import net.dhleong.acl.net.setup.StationStatusPacket;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Reads packets from a stream, and provides 
 *  utilities for parsing various data
 *  types from the raw bytes
 *   
 * @author dhleong
 *
 */
public class PacketParser {
    
    private static boolean mNoParse;
    private final byte[] mIntBuffer = new byte[4];

    /**
     * Read a Packet from the input stream
     * @param is
     * @return The packet, or NULL if it's not a handle-able packet;
     *  this does not mean that we CAN'T handle it (we would throw
     *  an ArtemisPacketException in that case) but rather that it
     *  has no data; it's probably the old "empty SystemInfoPacket".
     *  If it is null, you can safely discard it
     *  
     * @throws IOException
     * @throws ArtemisPacketException
     */
    public ArtemisPacket readPacket(InputStream is) throws IOException, ArtemisPacketException {
        int header = readInt(is);
        if (header != 0xdeadbeef) {
            throw new ArtemisPacketException("Illegal packet header: " + Integer.toHexString(header));
        }
        
        final int len = readInt(is);
        if (len <= 8) {
            return new BaseArtemisPacket();
        }
        
        final int mode = readInt(is);
        if (mode != 1 && mode != 2) {
            throw new ArtemisPacketException("Unknown packet mode: " + mode);
        }
        
        final int modeIsh = readInt(is);
        if (modeIsh != 0) {
            throw new ArtemisPacketException("No empty padding after 4-byte mode?");
        }
        
        final int flags = readInt(is);
        final int packetType = readInt(is);
        
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
        
        try {
        
            return buildPacket(packetType, mode, flags, bucket);
        
        } catch (RuntimeException e) {
            System.err.println("Unable to parse packet of type " 
                    + Integer.toHexString(packetType));
            System.err.println("--> " + TextUtil
                    .byteArrayToHexString(bucket));
            throw e;
        }
    }
    
    /**
     * For debugging; disable actually parsing packets,
     *  and just return {@link BaseArtemisPacket} instances
     *  with the raw data
     * @param noParse
     */
    public void setNoParseMode(boolean noParse) {
        mNoParse = noParse;
    }
    
    /**
     * Make sure to read a full int 
     * 
     * @param is
     * @return
     * @throws IOException
     */
    private int readInt(InputStream is) throws IOException {
        int readTotal = 0, read;
        while (readTotal < 4) {
            read = is.read(mIntBuffer, readTotal, 4-readTotal);
            if (read == -1)
                throw new EOFException(); // disconnected! 
                
            readTotal += read;
        }
        return getLendInt(mIntBuffer);
    }

    public static ArtemisPacket buildPacket(int packetType, int mode, 
            int flags, byte[] bucket) throws ArtemisPacketException {
        
        if (mNoParse)
            return new BaseArtemisPacket(mode, flags, packetType, bucket);
        
        switch (packetType) {            
        case EngGridUpdatePacket.TYPE:
            return new EngGridUpdatePacket(flags, bucket);
            
        case CommsIncomingPacket.TYPE:
            return new CommsIncomingPacket(flags, bucket);
            
        case IncomingAudioPacket.TYPE:
            return new IncomingAudioPacket(bucket);
            
        case DestroyObjectPacket.TYPE:
            return new DestroyObjectPacket(flags, bucket);
            
        case GameMessagePacket.TYPE:
            // this is another generic type; a few other 
            //  global messages are included
            switch(bucket[0]) {
            case AllShipSettingsPacket.MSG_TYPE:
                return new AllShipSettingsPacket(bucket);
            case JumpStatusPacket.MSG_TYPE_BEGIN:
            case JumpStatusPacket.MSG_TYPE_END:
                return new JumpStatusPacket(bucket);
            case GameStartPacket.MSG_TYPE:
                return new GameStartPacket(flags, bucket);
            default:
                return new GameMessagePacket(flags, bucket);
            }
            
        case StationStatusPacket.TYPE:
            return new StationStatusPacket(bucket);
            
        case ArtemisPacket.WORLD_TYPE:
            // ooh, crazy world type; switch for kid types
            final int type = bucket[0];
            switch (type) {
            case ArtemisObject.TYPE_PLAYER_MAIN:
                return new MainPlayerUpdatePacket(bucket);
            case ArtemisObject.TYPE_PLAYER_WEAP:
                return new WeapPlayerUpdatePacket(bucket);
            case ArtemisObject.TYPE_PLAYER_ENG:
                return new EngPlayerUpdatePacket(bucket);
                
            case ArtemisObject.TYPE_ENEMY:
                return new EnemyUpdatePacket(bucket);
                
            case ArtemisObject.TYPE_OTHER:
                return new OtherShipUpdatePacket(bucket);
                
            case ArtemisObject.TYPE_STATION:
                return new StationPacket(bucket);

            case ArtemisObject.TYPE_MESH:
                return new GenericMeshPacket(bucket);

            case ArtemisObject.TYPE_MINE:
            case ArtemisObject.TYPE_ANOMALY:
            case ArtemisObject.TYPE_NEBULA:
            case ArtemisObject.TYPE_TORPEDO:
            case ArtemisObject.TYPE_BLACK_HOLE:
            case ArtemisObject.TYPE_ASTEROID:
            case ArtemisObject.TYPE_MONSTER:
            case ArtemisObject.TYPE_WHALE:
                return new GenericUpdatePacket(bucket);
                
            case 0: // some sort of empty packet... possibly keepalive?
                return null;
            }
            
            // unhandled? fall through for generic
        
        default:
            return new BaseArtemisPacket(mode, flags, packetType, bucket);
        }       
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
    
    public static long getLendLong(byte[] bytes, int offset) {
        return (
                (0xffL & bytes[offset+7]) << 56  |
                (0xffL & bytes[offset+6]) << 48  |
                (0xffL & bytes[offset+5]) << 40  |
                (0xffL & bytes[offset+4]) << 32  |
                (0xffL & bytes[offset+3]) << 24  |
                (0xffL & bytes[offset+2]) << 16  |
                (0xffL & bytes[offset+1]) << 8   |
                (0xffL & bytes[offset]) << 0
                );
    }
    
    public static int getLendShort(byte[] bytes, int offset) {
        return ( 
                (0xff & bytes[offset+1]) << 8   |
                (0xff & bytes[offset]) << 0
                );
    }

    public static float getLendFloat(byte[] bytes, int offset) {
        int bits = getLendInt(bytes, offset);
        return Float.intBitsToFloat(bits);
    }

    /**
     * How many bytes would it take to insert 
     *  the given "name" into a packet?
     * @param name
     * @return
     */
    public static int getNameLengthBytes(String name) {
        // 4 bytes for the int length, 2 for 
        //  each char, + 2 for the "null" bytes
        return 4 + 2*name.length() + 2;
    }

    public static int getNameLengthBytes(byte[] mData, int offset) {
        // nameLen includes the "null" bytes, and is 
        //  measured in 2-byte CHARs
        final int nameLen = getLendInt(mData, offset);
        return (nameLen-1) * 2;
    }

    /**
     * Sometimes the nameLen in bytes includes some garbage text 
     *  (probably a pre-allocated buffer on the server) and we don't
     *  want that; this will look for a null byte and ensure that
     *  we don't include anything after it (apparently Java doesn't
     *  stop text at a null byte like C does)
     *  
     * @param bytes
     * @param offset
     * @param nameLenBytes
     * @return
     */
    public static String getNameString(byte[] bytes, int offset, int nameLenBytes) {
        int realNameLen = nameLenBytes;
        for (int i=offset+nameLenBytes-2; i >= offset; i-=2) {
            if (getLendShort(bytes, i) == 0) {
                realNameLen = i-offset;
                break;
            }
        }
        try {
            return new String(bytes, offset, realNameLen, "UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            return new String(bytes, offset, realNameLen);
        }
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

    public static void putNameString(String name, byte[] mData, int offset) {
        putLendInt(name.length()+1, mData, offset);
        offset += 4;
        byte[] bytes = name.getBytes();
        if (bytes.length == name.length()) {
            for (int i=0; i<bytes.length; i++) {
                mData[offset] = bytes[i];
                offset += 2;
            }
        } else {            
            System.arraycopy(bytes, 0, mData, offset, bytes.length);
        }
    }
}
