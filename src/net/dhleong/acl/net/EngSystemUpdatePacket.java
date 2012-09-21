package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.world.ArtemisObject;

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
    
    public enum BoolState {
        TRUE, FALSE,
        /** Not specified in packet */
        UNKNOWN;

        public boolean getBooleanValue() {
            // hacks? meh
            return ordinal() == 0;
        }

        public static BoolState from(boolean isTrue) {
            return isTrue ? TRUE : FALSE;
        }
    }
    private static final long RED_ALERT     = 0x0000000000020000L;
    private static final long NO_ENERGY     = 0x0000000000000010L;
    
    private static final long STATION_BEAMS = 0x0000000002000000L;
    private static final long STATION_TORPS = 0x0000000004000000L;
    private static final long STATION_SENSR = 0x0000000008000000L;
    private static final long STATION_MANEU = 0x0000000010000000L;
    private static final long STATION_IMPLS = 0x0000000020000000L;
    private static final long STATION_JUMPS = 0x0000000040000000L;
    private static final long STATION_SREAR = 0x0000000080000000L;
    private static final long STATION_SFRNT = 0x0000000100000000L;

    private static final long[] SYSTEMS = {
        STATION_BEAMS, STATION_TORPS, STATION_SENSR,
        STATION_MANEU, STATION_IMPLS, STATION_JUMPS,
        STATION_SREAR, STATION_SFRNT
    };
    
    
    private final byte[] mData;
    
    private final float mShipEnergy;
    private final BoolState mRedAlert;
    public final List<HeatInfo> mHeatInfo = new ArrayList<HeatInfo>();

    public EngSystemUpdatePacket(final SystemInfoPacket pkt) {
//        mData = new byte[32];
//        
//        // copy data for now
//        System.arraycopy(pkt.mData, 0, mData, 0, 32);
        mData = pkt.mData;
        
        long args = PacketParser.getLendLong(pkt.mData, 6);
//        System.out.println("!! Args: " + Long.toHexString(args));
        
        int offset;
        if ((args & NO_ENERGY) == 0) {
            mShipEnergy = PacketParser.getLendFloat(pkt.mData, 16);
            offset = 20;
        } else {
            mShipEnergy = -1; // not provided
            offset = 16;
        }

        
        final int end = mData.length-4;
        int systemIndex = 0;
        while (offset < end) {
//            System.out.println("Systems: " + Integer.toHexString(stations));
//            System.out.println("  Check: " + Integer.toHexString(SYSTEMS[systemIndex]));
            while (systemIndex < SYSTEMS.length && 
                    (args & SYSTEMS[systemIndex]) == 0)
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
        
        if ((args & RED_ALERT) != 0) {
            mRedAlert = (pkt.mData[offset] != 0) ? BoolState.TRUE : BoolState.FALSE;
        } else {
            mRedAlert = BoolState.UNKNOWN;
        }
    }

    @Override
    public long getMode() {
        return 0x01;
    }
    
    /**
     * 
     * @return The new energy amount, or -1 if unspecified in packet
     */
    public float getShipEnergy() {
        return mShipEnergy;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return true;
    }

    public BoolState getRedAlert() {
        return mRedAlert;
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
        System.out.println("**   Energy:" + mShipEnergy);
        System.out.println("** RedAlert:" + mRedAlert);
        for (HeatInfo info : mHeatInfo)
            System.out.println(info.station + " = " + info.heat);
        System.out.println("** --> " + toString());
    }
    
    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        return pkt.getTargetType() == ArtemisObject.TYPE_PLAYER && 
                (pkt.getAction() == SystemInfoPacket.ACTION_UPDATE_SYSTEMS
                    || pkt.getAction() == SystemInfoPacket.ACTION_UPDATE_SYSTEMS_2);
    }
}
