package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.world.ArtemisEnemy;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisOtherShip;
import net.dhleong.acl.world.ArtemisStation;

public class SysCreatePacket implements ArtemisPacket {
    
    private final byte[] mData;
    
    private final List<ArtemisObject> mCreatedObjs = new ArrayList<ArtemisObject>();
    
    public SysCreatePacket(SystemInfoPacket pkt) {
        mData = pkt.mData;
        
        switch (pkt.getTargetType()) {
        case ArtemisObject.TYPE_ENEMY: {
            // the length of the name in 2-byte chars WITH trailing null
            int objId = PacketParser.getLendInt(mData, 1);
            int nameLen = PacketParser.getNameLengthBytes(mData, 10);
            String name = new String(mData, 14, nameLen);
            mCreatedObjs.add(new ArtemisEnemy(objId, name));
            break; }
        case ArtemisObject.TYPE_OTHER: {
            int offset = 0;
            while (mData[offset] != 0x00) {
                int nameLen = 0;
                try {
                    int objId = PacketParser.getLendInt(mData, offset+1);
                    nameLen = PacketParser.getNameLengthBytes(mData, offset+10);
                    String name = new String(mData, offset+14, nameLen);
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
                int lenSubPacket = (0xff & mData[offset+6])+5;
                int objId = PacketParser.getLendInt(mData, offset+1);
                int nameLen = PacketParser.getNameLengthBytes(mData, offset+7);
                try {
                    String name = new String(mData, offset+11, nameLen);
                    mCreatedObjs.add(new ArtemisStation(objId, name));
                } catch (StringIndexOutOfBoundsException e) {
                    debugPrint();
                    System.out.println("DEBUG: subpLen = " + lenSubPacket);
                    System.out.println("DEBUG: objId   = " + objId);
                    System.out.println("DEBUG: nameLen = " + nameLen);
                    System.out.println("DEBUG: offset = " + offset);
                    System.out.println("DEBUG: Packet = " + this);
                }
                offset += lenSubPacket;
            }
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
        return pkt.getAction() == SystemInfoPacket.ACTION_CREATE;
    }
}
