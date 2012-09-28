package net.dhleong.acl.world;

import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.EngSystemUpdatePacket.BoolState;


public class ArtemisPlayer extends BaseArtemisShip {
    
    private static final int SYS_COUNT = SystemType.values().length;

    private BoolState mRedAlert;
    private final float[] mHeat = new float[SYS_COUNT];
    private final float[] mSystems = new float[SYS_COUNT];
    private final int[] mCoolant = new int[SYS_COUNT];

    private float mEnergy;

    public ArtemisPlayer(int objId, String name, int hullId, BoolState redAlert) {
        super(objId, name, hullId);
        
        mRedAlert = redAlert;
        
        // pre-fill
        for (int i=0; i<SYS_COUNT; i++) {
            mHeat[i] = -1;
            mSystems[i] = -1;
            mCoolant[i] = -1;
        }
    }
    
    public float getEnergy() {
        return mEnergy;
    }
    
    /**
     * Get coolant setting for a system, if we have it
     * @param sys
     * @return The setting as an int [0, 8], or -1 if we don't know
     */
    public int getSystemCoolant(SystemType sys) {
//        return mCoolant.containsKey(sys)
//                ? mCoolant.get(sys)
//                : -1;
        return mCoolant[sys.ordinal()];
    }
    
    /**
     * Get the energy setting for a system, if we have it
     * @param sys
     * @return The setting as a float [0, 1] where 1f == 300%,
     *  or -1 if we don't know
     */
    public float getSystemEnergy(SystemType sys) {
//        return mSystems.containsKey(sys) 
//                ? mSystems.get(sys) 
//                : -1f;
        return mSystems[sys.ordinal()];
    }
    
    public float getSystemHeat(SystemType sys) {
//      return mSystems.containsKey(sys) 
//              ? mSystems.get(sys) 
//              : -1f;
      return mHeat[sys.ordinal()];
  }
    
    @Override
    public int getType() {
        return TYPE_PLAYER;
    }
    
    public boolean isRedAlert() {
        return mRedAlert == BoolState.TRUE;
    }

    @Override
    public String toString() {
        return String.format("[PLAYER:%s:%d:%c]", 
                mName,
                mHullId,
                isRedAlert() ? 'R' : '_');
    }

    public void setRedAlert(boolean newState) {
        mRedAlert = BoolState.from(newState);
    }

    public void setSystemCoolant(SystemType sys, int coolant) {
        mCoolant[sys.ordinal()] = coolant;
    }

    public void setSystemEnergy(SystemType sys, float energy) {
        mSystems[sys.ordinal()] = energy;
    }

    public void setSystemHeat(SystemType sys, float heat) {
        mHeat[sys.ordinal()] = heat;
    }


    public void setShipEnergy(float energy) {
        mEnergy = energy;
    }
    
    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        // it should be!
        if (eng instanceof ArtemisPlayer) {
            ArtemisPlayer plr = (ArtemisPlayer) eng;
            
            if (plr.mRedAlert != BoolState.UNKNOWN)
                mRedAlert = plr.mRedAlert;
            
            if (plr.mEnergy != -1)
                mEnergy = plr.mEnergy;
            
            for (int i=0; i<SYS_COUNT; i++) {
                if (plr.mHeat[i] != -1)
                    mHeat[i] = plr.mHeat[i];
                
                if (plr.mSystems[i] != -1)
                    mSystems[i] = plr.mSystems[i];
                
                if (plr.mCoolant[i] != -1)
                    mCoolant[i] = plr.mCoolant[i];
            }
        }
    }
}
