package net.dhleong.acl.world;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.util.TextUtil;

public abstract class BaseArtemisObject implements ArtemisPositionable {
	private static final String UNKNOWN = "UNKNOWN";

	public static void putProp(SortedMap<String, Object> props, String label,
			int value, int unspecifiedValue, boolean includeUnspecified) {
		if (!includeUnspecified && value == unspecifiedValue) {
			return;
		}

		props.put(label, value != unspecifiedValue ? Integer.valueOf(value) : UNKNOWN);
	}

	public static void putProp(SortedMap<String, Object> props, String label,
			float value, float unspecifiedValue, boolean includeUnspecified) {
		if (!includeUnspecified && value == unspecifiedValue) {
			return;
		}

		props.put(label, value != unspecifiedValue ? Float.valueOf(value) : UNKNOWN);
	}

	public static void putProp(SortedMap<String, Object> props, String label,
			BoolState value, boolean includeUnspecified) {
		if (!includeUnspecified && value == BoolState.UNKNOWN) {
			return;
		}

		props.put(label, value);
	}

	public static void putProp(SortedMap<String, Object> props, String label,
			Object value, boolean includeUnspecified) {
		if (!includeUnspecified && value == null) {
			return;
		}

		props.put(label, value != null ? value : UNKNOWN);
	}

	protected final int mId;
    public String mName;
    private float mX = -1;
    private float mY = -1;
    private float mZ = -1;
    private SortedMap<String, byte[]> unknownFields;

    public BaseArtemisObject(int objId, String name) {
        mId = objId;
        mName = name;
    }

    @Override
    public int getId() {
        return mId;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ArtemisObject))
            return false;
        
        return getId() == ((ArtemisObject)other).getId();
    }
    
    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public float getX() {
        return mX;
    }

    @Override
    public void setX(float mX) {
        this.mX = mX;
    }

    @Override
    public float getY() {
        return mY;
    }

    @Override
    public void setY(float y) {
        mY = y;
    }

    @Override
    public float getZ() {
        return mZ;
    }

    @Override
    public void setZ(float z) {
        mZ = z;
    }
    
    @Override
    public void updateFrom(ArtemisPositionable eng) {
        // names can actually change!
        if (eng.getName() != null)
            mName = eng.getName();
        
        if (eng.getX() != -1) {
        	setX(eng.getX());
        }

        if (eng.getY() != -1) {
        	setY(eng.getY());
        }

        if (eng.getZ() != -1) {
        	setZ(eng.getZ());
        }

        BaseArtemisObject cast = (BaseArtemisObject) eng;
        SortedMap<String, byte[]> unknown = cast.getUnknownFields();

        if (unknown != null) {
        	if (unknownFields == null) {
        		unknownFields = new TreeMap<String, byte[]>();
        	}

        	unknownFields.putAll(unknown);
        }
    }

    public SortedMap<String, byte[]> getUnknownFields() {
    	return unknownFields;
    }

    public void setUnknownFields(SortedMap<String, byte[]> unknownFields) {
    	this.unknownFields = unknownFields;
    }

    @Override
    public SortedMap<String, Object> getProps(boolean includeUnspecified) {
    	SortedMap<String, Object> props = new TreeMap<String, Object>();
    	appendObjectProps(props, includeUnspecified);
    	return props;
    }

    @Override
    public final String toString() {
    	SortedMap<String, Object> props = getProps(false);
    	StringBuilder b = new StringBuilder();

    	for (Map.Entry<String, Object> entry : props.entrySet()) {
    		b.append("\n\t").append(entry.getKey()).append(": ");
    		Object value = entry.getValue();

    		if (value instanceof byte[]) { 
    			b.append(TextUtil.byteArrayToHexString((byte[]) value));
    		} else {
    			b.append(value);
    		}
    	}

    	return b.toString();
    }

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	props.put("ID", Integer.valueOf(mId));
    	putProp(props, "Name", mName, includeUnspecified);
    	putProp(props, "X", mX, -1, includeUnspecified);
    	putProp(props, "Y", mY, -1, includeUnspecified);
    	putProp(props, "Z", mZ, -1, includeUnspecified);
    	props.putAll(unknownFields);
    }
}