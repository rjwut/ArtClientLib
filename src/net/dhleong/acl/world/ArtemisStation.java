package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

/**
 * Space stations
 */
public class ArtemisStation extends BaseArtemisShielded {
	private int mIndex = -1;

	public ArtemisStation(int objId, String name) {
        super(objId, name);
    }

	@Override
    public ObjectType getType() {
        return ObjectType.SPACE_STATION;
    }

	/**
	 * This station's index value. In non-scripted scenarios, DS1's index is 0,
	 * DS2's index is 1, etc. This value is unique even if the names aren't.
	 * Unspecified: -1
	 */
	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

    @Override
    public void updateFrom(ArtemisObject eng) {
        super.updateFrom(eng);
        
        if (eng instanceof ArtemisStation) {
            ArtemisStation station = (ArtemisStation) eng;

            if (station.mIndex != -1) {
            	mIndex = station.mIndex;
            }
        }
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Station index", mIndex, -1, includeUnspecified);
    }
}