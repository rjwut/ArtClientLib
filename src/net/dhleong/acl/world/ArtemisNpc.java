package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.util.BoolState;

/**
 * An NPC ship; they may have special abilities, and can be scanned.
 * @author dhleong
 */
public class ArtemisNpc extends BaseArtemisShip {
    
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
    private int mElite = -1, mEliteState = -1;
    private BoolState mEnemy;
    private String mIntel;

    public ArtemisNpc(int objId, String name, int hullId) {
        super(objId, name, hullId);
    }

    public BoolState isEnemy() {
    	return mEnemy;
    }

    public void setEnemy(BoolState enemy) {
    	mEnemy = enemy;
    }

    @Override
    public ObjectType getType() {
        return ObjectType.NPC_SHIP;
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

    public String getIntel() {
    	return mIntel;
    }

    public void setIntel(String intel) {
    	mIntel = intel;
    }

    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        // it SHOULD be an ArtemisNpc
        if (eng instanceof ArtemisNpc) {
            ArtemisNpc cast = (ArtemisNpc) eng;
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

            if (cast.mIntel != null) {
            	setIntel(cast.mIntel);
            }
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
    	if (!(obj instanceof ArtemisNpc)) {
    		return true;
    	}

    	ArtemisNpc npc = (ArtemisNpc) obj;

    	return npc.isEnemy() == BoolState.FALSE || npc.isScanned(scanLevel);
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
        return (obj instanceof ArtemisNpc) 
                && ((ArtemisNpc)obj).hasEliteAbility(ability);
    }

    /**
     * Use for dynamic abilities like ELITE_CLOAKING
     */
    public static boolean isUsingEliteAbility(ArtemisObject obj, int ability) {
        return (obj instanceof ArtemisNpc) 
                && ((ArtemisNpc)obj).isUsingEliteAbiilty(ability);
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Scan level", mScannedLevel, -1, includeUnspecified);
    	putProp(props, "Elite", mElite, -1, includeUnspecified);
    	putProp(props, "Elite state", mEliteState, -1, includeUnspecified);
    	putProp(props, "Is enemy", mEnemy, includeUnspecified);
    	putProp(props, "Intel", mIntel, includeUnspecified);
    }
}