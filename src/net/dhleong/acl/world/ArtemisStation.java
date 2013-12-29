package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

public class ArtemisStation extends BaseArtemisShielded {
	private int mIndex = -1;

	public ArtemisStation(int objId, String name) {
        super(objId, name);
    }

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

	@Override
    public ObjectType getType() {
        return ObjectType.SPACE_STATION;
    }

    @Override
    public void updateFrom(ArtemisPositionable eng) {
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