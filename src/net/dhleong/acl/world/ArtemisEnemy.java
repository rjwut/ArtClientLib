package net.dhleong.acl.world;

public class ArtemisEnemy extends BaseArtemisShip {
    
    // elite enemy bits
    public static final int ELITE_INVIS_TO_MAIN_SCREEN  = 1;
    public static final int ELITE_INVIS_TO_SCIENCE      = 2;
    public static final int ELITE_INVIS_TO_TACTICAL     = 4;
    public static final int ELITE_CLOAKING              = 8;
    public static final int ELITE_HET                   = 16;
    public static final int ELITE_WARP                  = 32;
    public static final int ELITE_TELEPORT              = 64;

    // scan levels... only 2 for now
    public static final byte SCAN_LEVEL_BASIC = 1;
    public static final byte SCAN_LEVEL_FULL  = 2;
    
    private byte mScannedLevel = -1;
    private int mElite;
    
    public ArtemisEnemy(int objId, String name, int hullId) {
        super(objId, name, hullId);
        
    }
    
    @Override
    public int getType() {
        return TYPE_ENEMY;
    }
    
    public boolean hasEliteAbility(int ability) {
        return mElite != -1 && (mElite & ability) != 0;
    }
    
    public boolean isScanned(byte scanLevel) {
        return mScannedLevel >= scanLevel;
    }
    
    public void setEliteBits(int elite) {
        mElite = elite;
    }

    public void setScanned(byte scanned) {
        mScannedLevel = scanned;
    }
    
    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        // it SHOULD be an ArtemisEnemy
        if (eng instanceof ArtemisEnemy) {
            ArtemisEnemy cast = (ArtemisEnemy)eng;
            if (cast.mScannedLevel != -1)
                setScanned(cast.mScannedLevel);
            
            if (cast.mElite != -1)
                setEliteBits(cast.mElite);
        }
        
    }

    @Override
    public String toString() {
        return String.format("[ENEMY:%s:%d:%c]%s", 
                mName, 
                mHullId,
                (mScannedLevel > 0) ? mScannedLevel : '_',
                super.toString());
    }

    /**
     * Return whether or not we can show the given scanLevel info
     *  for ANY ArtemisObject. The logic is we can ALWAYS show
     *  scan info for non-enemies; otherwise, if it IS an enemy,
     *  it must pass {@link #isScanned(byte)} (of course)
     *  
     * @param obj
     * @param scanLevel
     * @return
     */
    public static boolean isScanned(ArtemisObject obj, byte scanLevel) {
        return !(obj instanceof ArtemisEnemy) 
                || ((ArtemisEnemy)obj).isScanned(scanLevel);
    }

    /**
     * Convenience, checks if we've scanned the obj AT ALL 
     *  (IE: do we have BASIC level scan?) 
     * @param obj
     * @return
     */
    public static boolean isScanned(ArtemisObject obj) {
        return isScanned(obj, SCAN_LEVEL_BASIC);
    }

    public byte getScanLevel() {
        return mScannedLevel;
    }

    public static boolean hasEliteAbility(ArtemisObject obj, int ability) {
        return (obj instanceof ArtemisEnemy) 
                && ((ArtemisEnemy)obj).hasEliteAbility(ability);
    }
}
