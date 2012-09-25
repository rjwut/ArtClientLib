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
    
    public static final class DamageInfo implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 4484891520400874942L;
        public final SystemType station;
        public final float damage;
        
        private DamageInfo(SystemType systemType, float d) {
            station = systemType;
            damage = d;
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
    private static final long NO_ENERGY     = 0x0000000000000001L;
    private static final long DUNNO_SKIP    = 0x0000000000000010L;
    
    private static final long SHIELD_STATE_FRONT= 0x0000000000001000;
    private static final long SHIELD_MAX_FRONT  = 0x0000000000002000;
    private static final long SHIELD_STATE_REAR = 0x0000000000004000;
    private static final long SHIELD_MAX_REAR   = 0x0000000000008000;

    
    private static final long HEAT_BEAMS = 0x0000000002000000L;
    private static final long HEAT_TORPS = 0x0000000004000000L;
    private static final long HEAT_SENSR = 0x0000000008000000L;
    private static final long HEAT_MANEU = 0x0000000010000000L;
    private static final long HEAT_IMPLS = 0x0000000020000000L;
    private static final long HEAT_JUMPS = 0x0000000040000000L;
    private static final long HEAT_SFRNT = 0x0000000080000000L;
    private static final long HEAT_SREAR = 0x0000000100000000L;
    
    private static final long DAMG_BEAMS = 0x0000000200000000L;
    private static final long DAMG_TORPS = 0x0000000400000000L;
    private static final long DAMG_SENSR = 0x0000000800000000L;
    private static final long DAMG_MANEU = 0x0000001000000000L;
    private static final long DAMG_IMPLS = 0x0000002000000000L;
    private static final long DAMG_JUMPS = 0x0000004000000000L;
    private static final long DAMG_SFRNT = 0x0000008000000000L;
    private static final long DAMG_SREAR = 0x0000010000000000L;

    private static final long[] SYSTEMS_HEAT = {
        HEAT_BEAMS, HEAT_TORPS, HEAT_SENSR,
        HEAT_MANEU, HEAT_IMPLS, HEAT_JUMPS,
        HEAT_SFRNT, HEAT_SREAR
    };
    
    private static final long[] SYSTEMS_DAMG = {
        DAMG_BEAMS, DAMG_TORPS, DAMG_SENSR,
        DAMG_MANEU, DAMG_IMPLS, DAMG_JUMPS,
        DAMG_SFRNT, DAMG_SREAR
    };
    
    private final byte[] mData;
    
    private final float mShipEnergy;
    private final BoolState mRedAlert;
    public final List<HeatInfo> mHeatInfo = new ArrayList<HeatInfo>();
    public final List<DamageInfo> mDamageInfo = new ArrayList<DamageInfo>();
    
    public final float mShieldsFront, mShieldsMaxFront;
    public final float mShieldsRear, mShieldsMaxRear;

    public EngSystemUpdatePacket(final SystemInfoPacket pkt) {
//        mData = new byte[32];
//        
//        // copy data for now
//        System.arraycopy(pkt.mData, 0, mData, 0, 32);
        mData = pkt.mData;
        
        long args = PacketParser.getLendLong(pkt.mData, 6);
//        System.out.println("!! Args: " + Long.toHexString(args));
//        System.out.println("!! Args: " + Long.toHexString(args & NO_ENERGY));
        int offset;
        if (pkt.getAction() != 0x0 && (args & NO_ENERGY) == 0) {
            mShipEnergy = PacketParser.getLendFloat(pkt.mData, 16);
            offset = 20;
        } else {
            mShipEnergy = -1; // not provided
            offset = 16;
        }
        
        // dunno...
        if ((args & DUNNO_SKIP) != 0)
            offset += 4;

        // shields
        if ((args & SHIELD_STATE_FRONT) != 0) {
            mShieldsFront = PacketParser.getLendFloat(pkt.mData, offset);
            offset += 4;
        } else {
            mShieldsFront = -1;
        }
        if ((args & SHIELD_MAX_FRONT) != 0) {
            mShieldsMaxFront = PacketParser.getLendFloat(pkt.mData, offset);
            offset += 4;
        } else {
            mShieldsMaxFront = -1;
        }
        if ((args & SHIELD_STATE_REAR) != 0) {
            mShieldsRear = PacketParser.getLendFloat(pkt.mData, offset);
            offset += 4;
        } else {
            mShieldsRear = -1;
        }
        if ((args & SHIELD_MAX_REAR) != 0) {
            mShieldsMaxRear = PacketParser.getLendFloat(pkt.mData, offset);
            offset += 4;
        } else {
            mShieldsMaxRear = -1;
        }
        
        final int end = mData.length-4;
        int systemIndex = 0;
        while (offset < end) {
//            System.out.println("Systems: " + Integer.toHexString(stations));
//            System.out.println("  Check: " + Integer.toHexString(SYSTEMS[systemIndex]));
            while (systemIndex < SYSTEMS_HEAT.length && 
                    (args & SYSTEMS_HEAT[systemIndex]) == 0)
                systemIndex++;
            if (systemIndex >= SYSTEMS_HEAT.length) {
//                System.err.println("Couldn't get system...@"+offset);
//                debugPrint();
                break;
            }
            
            float heat = PacketParser.getLendFloat(pkt.mData, offset);
            mHeatInfo.add(new HeatInfo(SystemType.values()[systemIndex], heat));
            offset += 4;
            systemIndex++;
        }
        
        // look for system damage
        systemIndex = 0;
        while (offset < end) {
//            System.out.println("Systems: " + Integer.toHexString(stations));
//            System.out.println("  Check: " + Integer.toHexString(SYSTEMS[systemIndex]));
            while (systemIndex < SYSTEMS_DAMG.length && 
                    (args & SYSTEMS_DAMG[systemIndex]) == 0)
                systemIndex++;
            if (systemIndex >= SYSTEMS_DAMG.length) {
//                System.err.println("Couldn't get system...@"+offset);
//                debugPrint();
                break;
            }
            
            float heat = PacketParser.getLendFloat(pkt.mData, offset);
            mDamageInfo.add(new DamageInfo(SystemType.values()[systemIndex], heat));
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
        if (mShieldsFront > -1)
            System.out.println("** ShieldsFront:" + mShieldsFront);
        if (mShieldsMaxFront > -1)
            System.out.println("** ShieldsMaxFront:" + mShieldsMaxFront);
        if (mShieldsRear > -1)
            System.out.println("** ShieldsRear:" + mShieldsRear);
        if (mShieldsMaxRear > -1)
            System.out.println("** ShieldsMaxRear:" + mShieldsMaxRear);
        
        for (HeatInfo info : mHeatInfo)
            System.out.println("HEAT) " + info.station + " = " + info.heat);
        for (DamageInfo info : mDamageInfo)
            System.out.println("DAMG) " + info.station + " = " + info.damage);
        System.out.println("** --> " + toString());
    }
    
    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        return pkt.getTargetType() == ArtemisObject.TYPE_PLAYER && 
                (pkt.getAction() == SystemInfoPacket.ACTION_UPDATE_SYSTEMS
                    || pkt.getAction() == SystemInfoPacket.ACTION_UPDATE_SYSTEMS_2);
    }
}
