package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.enums.Upgrade;

public class ArtemisAnomaly extends BaseArtemisObject {
	private Upgrade mUpgrade;

	public ArtemisAnomaly(int objId) {
		super(objId);
	}

	@Override
	public ObjectType getType() {
		return ObjectType.ANOMALY;
	}

	public Upgrade getUpgrade() {
		return mUpgrade;
	}

	public void setUpgrade(Upgrade upgrade) {
		mUpgrade = upgrade;
	}

    @Override
    public void updateFrom(ArtemisObject obj) {
        super.updateFrom(obj);
        
        if (obj instanceof ArtemisAnomaly) {
        	ArtemisAnomaly anomaly = (ArtemisAnomaly) obj;

            if (anomaly.mUpgrade != null) {
            	mUpgrade = anomaly.mUpgrade;
            }
        }
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Upgrade", mUpgrade, includeUnspecified);
    }
}