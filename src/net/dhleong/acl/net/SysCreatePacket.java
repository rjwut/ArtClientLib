package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.world.ArtemisEnemy;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisOtherShip;
import net.dhleong.acl.world.ArtemisPlayer;
import net.dhleong.acl.world.ArtemisStation;

public class SysCreatePacket implements ArtemisPacket {
    
    private final byte[] mData;
    
    private final List<ArtemisObject> mCreatedObjs = new ArrayList<ArtemisObject>();
    
    public SysCreatePacket(SystemInfoPacket pkt) {
        mData = pkt.mData;
        
        switch (pkt.getTargetType()) {
        case ArtemisObject.TYPE_PLAYER: {
            int objId = PacketParser.getLendInt(mData, 1);
            final int hullId = PacketParser.getLendInt(mData, 48);
            int offset = 82;
            int nameLen = PacketParser.getNameLengthBytes(mData, offset);
            if (nameLen > 1000) {
                debugPrint();
                System.out.println("DEBUG: Packet = " + this);
                throw new IllegalStateException("Parsed a name of " + nameLen + " bytes");
            }
            String name = PacketParser.getNameString(mData, offset+4, nameLen);
            offset += 6 + nameLen; // extra +2 for the null bytes
            final int nameEnd = offset;
            boolean redAlert = (mData[nameEnd + 20] != 0);
            ArtemisPlayer player = new ArtemisPlayer(objId, name, hullId, redAlert);
            
            // extract more info
            offset = nameEnd + 69; // begins system settings
            for (SystemType sys : SystemType.values()) {
                player.setSystemEnergy(sys, PacketParser.getLendFloat(mData, offset));
                offset += 4;
            }
            for (SystemType sys : SystemType.values()) {
                player.setSystemCoolant(sys, mData[offset++]);
            }
            
            mCreatedObjs.add(player);
            break; }
        case ArtemisObject.TYPE_ENEMY: {
            // the length of the name in 2-byte chars WITH trailing null
            int objId = PacketParser.getLendInt(mData, 1);
            int nameLen = PacketParser.getNameLengthBytes(mData, 10);
            String name = PacketParser.getNameString(mData, 14, nameLen);
            int offset = 14 + nameLen + 2; // (for the null bytes)
            offset += 2; // padding?
            offset += 14; // ?
            offset += 4; // ?
            final int hullId = PacketParser.getLendInt(mData, offset);
            mCreatedObjs.add(new ArtemisEnemy(objId, name, hullId));
            break; }
        case ArtemisObject.TYPE_OTHER: {
            int offset = 0;
            while (mData[offset] != 0x00) {
                int nameLen = 0;
                try {
                    int objId = PacketParser.getLendInt(mData, offset+1);
                    nameLen = PacketParser.getNameLengthBytes(mData, offset+10);
                    String name = PacketParser.getNameString(mData, offset+14, nameLen);
                    mCreatedObjs.add(new ArtemisOtherShip(objId, name));
                    offset += 148 + 5; // fixed length + TYPE and ID
                } catch (StringIndexOutOfBoundsException e) {
                    debugPrint();
                    System.out.println("DEBUG: nameLen = " + nameLen);
                    System.out.println("DEBUG: offset = " + offset);
                    System.out.println("DEBUG: Packet = " + this);
                    throw e;
                }
                
                if (offset >= mData.length) {
                    break; // I guess this is normal and fine?
//                    debugPrint();
//                    System.out.println("DEBUG: nameLen = " + nameLen);
//                    System.out.println("DEBUG: offset = " + offset);
//                    System.out.println("DEBUG: Packet = " + this);
//                    byte exception = mData[offset]; // lazy haha
//                    mData[offset] = exception; // will never get called
                }
            }
            break; }
        case ArtemisObject.TYPE_STATION: {
            
            int offset = 0;
            while (mData[offset] != 0x00) {
                // this length does NOT include the obj TYPE and ID
//                byte action = mData[offset+5];
                int lenSubPacket = (0xff & mData[offset+6]);
//                if (lenSubPacket == 0x1f)
                if (lenSubPacket != 0x3f)
                    lenSubPacket = 62; // seems to be the case...
                
                int objId = PacketParser.getLendInt(mData, offset+1);
                int nameLen = PacketParser.getNameLengthBytes(mData, offset+7);
                try {
                    String name = PacketParser.getNameString(mData, offset+11, nameLen);
                    mCreatedObjs.add(new ArtemisStation(objId, name));
                } catch (StringIndexOutOfBoundsException e) {
                    debugPrint();
                    System.out.println("DEBUG: subpLen = " + lenSubPacket);
                    System.out.println("DEBUG: objId   = " + objId);
                    System.out.println("DEBUG: nameLen = " + nameLen);
                    System.out.println("DEBUG: offset = " + offset);
                    System.out.println("DEBUG: Packet = " + this);
                    throw e;
                }
                offset += lenSubPacket + 5; // +5 for the TYPE and ID
                
                // for some reason, station packets are sometimes retarded
                //  and have lots of 0 padding at the end.
                while (offset+1 < mData.length && 
                        mData[offset] != ArtemisObject.TYPE_STATION)
                    offset++;
            }
            
//            debugPrint();
//            System.out.println("DEBUG: Packet = " + this);
            break; }
        }
    }
    
    public List<ArtemisObject> getCreatedObjects() {
        return mCreatedObjs;
    }

    @Override
    public long getMode() {
        return 0x01;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return true;
    }

    @Override
    public int getType() {
        return SystemInfoPacket.TYPE;
    }
    
    @Override
    public String toString() {
        return BaseArtemisPacket.byteArrayToHexString(mData); 
    }

    public void debugPrint() {
        System.out.println("** CREATE:");
        for (ArtemisObject obj : mCreatedObjs)
            System.out.println("**  + " + obj);
    }
    
    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        return pkt.getAction() == SystemInfoPacket.ACTION_CREATE;// || 
//                (pkt.getTargetType() == ArtemisObject.TYPE_ENEMY &&
//                pkt.getAction() != SystemInfoPacket.ACTION_UPDATE_SYSTEMS);
    }
}
