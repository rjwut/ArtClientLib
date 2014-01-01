package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.EliteAbility;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.util.BoolState;

/**
 * An NPC ship; they may have special abilities, and can be scanned.
 * @author dhleong
 */
public class ArtemisNpc extends BaseArtemisShip {
    // scan levels... only 2 for now
    public static final byte SCAN_LEVEL_BASIC = 1;
    public static final byte SCAN_LEVEL_FULL  = 2;
    
    private byte mScanLevel = -1;
    private int mElite = -1, mEliteState = -1;
    private BoolState mEnemy = BoolState.UNKNOWN;
    private String mIntel;

    public ArtemisNpc(int objId, String name, int hullId) {
        super(objId, name, hullId);
    }

    @Override
    public ObjectType getType() {
        return ObjectType.NPC_SHIP;
    }

    /**
     * Returns BoolState.TRUE if this ship is an enemy, BoolState.FALSE if it's
     * friendly, and BoolState.UNKNOWN if its status is unspecified. Note that
     * this only works in Solo mode.
     * Unspecified: BoolState.UNKNOWN
     */
    public BoolState isEnemy() {
    	return mEnemy;
    }

    public void setEnemy(BoolState enemy) {
    	mEnemy = enemy;
    }

    /**
     * Returns true if this ship has the specified elite ability and false if it
     * does not or if it is unknown whether it has it.
     */
    public boolean hasEliteAbility(EliteAbility ability) {
        return mElite != -1 && ability.on(mElite);
    }

    /**
     * Returns true if this ship is using the specified elite ability and false
     * if it is not.
     */
    public boolean isUsingEliteAbilty(EliteAbility ability) {
        return mEliteState != -1 && ability.on(mEliteState);
    }

    /**
     * Sets the elite ability bit field.
     * Unspecified: -1
     */
    public void setEliteBits(int elite) {
        mElite = elite;
    }

    /**
     * Sets the elite state bit field (what abilities are being used).
     * Unspecified: -1
     */
    public void setEliteState(int elite) {
        mEliteState = elite;
    }

    /**
     * The scan level for this ship.
     * Unspecified: -1
     */
    public byte getScanLevel() {
        return mScanLevel;
    }

    public void setScanLevel(byte scanLevel) {
        mScanLevel = scanLevel;
    }

    /**
     * Returns true if this ship has been scanned at the given level or higher;
     * false otherwise.
     */
    public boolean isScanned(byte scanLevel) {
        return mScanLevel >= scanLevel;
    }

    /**
     * The intel String for this ship.
     * Unspecified: null
     */
    public String getIntel() {
    	return mIntel;
    }

    public void setIntel(String intel) {
    	mIntel = intel;
    }

    @Override
    public void updateFrom(ArtemisObject eng) {
        super.updateFrom(eng);
        
        // it SHOULD be an ArtemisNpc
        if (eng instanceof ArtemisNpc) {
            ArtemisNpc cast = (ArtemisNpc) eng;
            BoolState enemy = cast.isEnemy();

            if (BoolState.isKnown(enemy)) {
            	mEnemy = enemy;
            }

            if (cast.mScanLevel != -1) {
                setScanLevel(cast.mScanLevel);
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
     * Return whether or not we can show the given scanLevel info for ANY
     * ArtemisObject. The logic is we can ALWAYS show scan info for non-enemies;
     * otherwise, if it IS an enemy, it must pass {@link #isScanned(byte)} (of
     * course).
     */
    public static boolean isScanned(ArtemisObject obj, byte scanLevel) {
    	if (!(obj instanceof ArtemisNpc)) {
    		return true;
    	}

    	ArtemisNpc npc = (ArtemisNpc) obj;

    	return npc.isEnemy() == BoolState.FALSE || npc.isScanned(scanLevel);
    }

    /**
     * Convenience, checks if we've scanned the obj AT ALL. (Do we have BASIC
     * level scan?)
     */
    public static boolean isScanned(ArtemisObject obj) {
        return isScanned(obj, SCAN_LEVEL_BASIC);
    }

    /**
     * Use for static abilities like INVISIBLE_TO_MAIN_SCREEN.
     */
    public static boolean hasEliteAbility(ArtemisObject obj,
    		EliteAbility ability) {
        return (obj instanceof ArtemisNpc)
                && ((ArtemisNpc) obj).hasEliteAbility(ability);
    }

    /**
     * Use for dynamic abilities like CLOAKING.
     */
    public static boolean isUsingEliteAbility(ArtemisObject obj,
    		EliteAbility ability) {
        return (obj instanceof ArtemisNpc) 
                && ((ArtemisNpc) obj).isUsingEliteAbilty(ability);
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props,
			boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Scan level", mScanLevel, -1, includeUnspecified);
    	putProp(props, "Elite", mElite, -1, includeUnspecified);
    	putProp(props, "Elite state", mEliteState, -1, includeUnspecified);
    	putProp(props, "Is enemy", mEnemy, includeUnspecified);
    	putProp(props, "Intel", mIntel, includeUnspecified);
    }
}