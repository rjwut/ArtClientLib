package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;

public class EngSystemUpdatePacket implements ArtemisPacket {
    
    public static final class HeatInfo implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 4484891520400874942L;
        public final SystemType station;
        public final float heat;
        
        private HeatInfo(SystemType systemType, float h) {
            station = systemType;
            heat = h;
        }
    }
    
    private static final int TYPE_0 = 0x00008000;
    private static final int TYPE_1 = 0x00108000;
    
    private static final int STATION_BEAMS = 0x00000200;
    private static final int STATION_TORPS = 0x00000400;
    private static final int STATION_SENSR = 0x00000800;
    private static final int STATION_MANEU = 0x00001000;
    private static final int STATION_IMPLS = 0x00002000;
    private static final int STATION_JUMPS = 0x00004000;
    private static final int STATION_SREAR = 0x00008000;
    private static final int STATION_SFRNT = 0x00010000;
    private static final int[] SYSTEMS = {
        STATION_BEAMS, STATION_TORPS, STATION_SENSR,
        STATION_MANEU, STATION_IMPLS, STATION_JUMPS,
        STATION_SREAR, STATION_SFRNT
    };
    
    
    private final byte[] mData;
    
    public final float mShipEnergy;
    public final List<HeatInfo> mHeatInfo = new ArrayList<HeatInfo>();

    public EngSystemUpdatePacket(final SystemInfoPacket pkt) {
//        mData = new byte[32];
//        
//        // copy data for now
//        System.arraycopy(pkt.mData, 0, mData, 0, 32);
        mData = pkt.mData;
        
        int stations = PacketParser.getLendInt(pkt.mData, 8);
        
        mShipEnergy = PacketParser.getLendFloat(pkt.mData, 16);
//        mSystemDamage = PacketParser.getLendFloat(pkt.mData, 20);
        
        int offset = 20;
        if (pkt.getAction() == TYPE_1) {
            // read whatever the heck that value is, eventually
            offset += 4;
        }
        
        final int end = mData.length-4;
        int systemIndex = 0;
        while (offset < end) {
//            System.out.println("Systems: " + Integer.toHexString(stations));
//            System.out.println("  Check: " + Integer.toHexString(SYSTEMS[systemIndex]));
            while (systemIndex < SYSTEMS.length && 
                    (stations & SYSTEMS[systemIndex]) == 0)
                systemIndex++;
            if (systemIndex >= SYSTEMS.length) {
//                System.err.println("Couldn't get system...@"+offset);
//                debugPrint();
                break;
            }
            
            float heat = PacketParser.getLendFloat(pkt.mData, offset);
            mHeatInfo.add(new HeatInfo(SystemType.values()[systemIndex], heat));
            offset += 4;
            systemIndex++;
        }
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
        System.out.println("** Energy:" + mShipEnergy);
        for (HeatInfo info : mHeatInfo)
            System.out.println(info.station + " = " + info.heat);
        System.out.println("** --> " + toString());
    }
    
    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        return pkt.getAction() == TYPE_0 || pkt.getAction() == TYPE_1;
    }
}
