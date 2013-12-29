package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

public class ArtemisDrone extends ArtemisGenericObject implements ArtemisBearable {
	public ArtemisDrone(int objId) {
		super(objId, null, ObjectType.DRONE);
	}

	private float mBearing = -1;
	private float mVelocity = -1;
	private float mSteering = -1;

	@Override
	public float getBearing() {
		return mBearing;
	}

	@Override
	public void setBearing(float bearing) {
		this.mBearing = bearing;
	}

	@Override
	public float getVelocity() {
		return mVelocity;
	}

	@Override
	public void setVelocity(float velocity) {
		this.mVelocity = velocity;
	}

	@Override
	public float getSteering() {
		return mSteering;
	}

	@Override
	public void setSteering(float steering) {
		this.mSteering = steering;
	}

	@Override
	public void updateFrom(ArtemisPositionable other) {
		super.updateFrom(other);

		if (other instanceof ArtemisDrone) {
			ArtemisDrone drone = (ArtemisDrone) other;
			mBearing = drone.mBearing;
			mVelocity = drone.mVelocity;
			mSteering = drone.mSteering;
		}
	}

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Heading", mBearing, -1, includeUnspecified);
    	putProp(props, "Velocity", mVelocity, -1, includeUnspecified);
    	putProp(props, "Rudder", mSteering, -1, includeUnspecified);
    }
}