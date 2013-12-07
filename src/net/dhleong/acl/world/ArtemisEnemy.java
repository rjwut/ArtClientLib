package net.dhleong.acl.world;

import net.dhleong.acl.util.BoolState;

/**
 * An enemy ship; they may have special
 *  abilities, and can be scanned.
 *  
 * Other ships objects may be scan-able,
 *  but scanning for these is understood 
 *  
 * @author dhleong
 *
 */
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
    private int mElite, mEliteState;
    private BoolState mEnemy;

    public ArtemisEnemy(int objId, String name, int hullId) {
        super(objId, name, hullId);
    }

    public BoolState isEnemy() {
    	return mEnemy;
    }

    public void setEnemy(BoolState enemy) {
    	mEnemy = enemy;
    }

    @Override
    public int getType() {
        return TYPE_OTHER_SHIP;
    }
    
    public boolean hasEliteAbility(int ability) {
        return mElite != -1 && (mElite & ability) != 0;
    }

    public boolean isUsingEliteAbiilty(int ability) {
        return mEliteState != -1 && (mEliteState & ability) != 0;
    }
    
    public boolean isScanned(byte scanLevel) {
        return mScannedLevel >= scanLevel;
    }
    
    public void setEliteBits(int elite) {
        mElite = elite;
    }

    public void setEliteState(int elite) {
        mEliteState = elite;
    }
    
    public void setScanned(byte scanned) {
        mScannedLevel = scanned;
    }
    
    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        // it SHOULD be an ArtemisEnemy
        if (eng instanceof ArtemisEnemy) {
            ArtemisEnemy cast = (ArtemisEnemy) eng;
            BoolState enemy = cast.isEnemy();

            if (BoolState.isKnown(enemy)) {
            	mEnemy = enemy;
            }

            if (cast.mScannedLevel != -1) {
                setScanned(cast.mScannedLevel);
            }
            
            if (cast.mElite != -1) {
                setEliteBits(cast.mElite);
            }

            if (cast.mEliteState != -1) {
                setEliteState(cast.mEliteState);
            }
        }
    }

    @Override
    public String toString() {
        final String base = String.format("[ENEMY:%s:%d:%c]%s", 
                mName, 
                mHullId,
                (mScannedLevel > 0) ? mScannedLevel : '_',
                super.toString());
        if (mElite == -1 && mEliteState == -1)
            return base;
        else {
            return String.format("%s[ELITE|%d][STATE:%d]", base, mElite, mEliteState);
        }
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

    /**
     * Use for static abilities like ELITE_INVIS_TO_MAIN_SCREEN
     */
    public static boolean hasEliteAbility(ArtemisObject obj, int ability) {
        return (obj instanceof ArtemisEnemy) 
                && ((ArtemisEnemy)obj).hasEliteAbility(ability);
    }

    /**
     * Use for dynamic abilities like ELITE_CLOAKING
     */
    public static boolean isUsingEliteAbility(ArtemisObject obj, int ability) {
        return (obj instanceof ArtemisEnemy) 
                && ((ArtemisEnemy)obj).isUsingEliteAbiilty(ability);
    }
}