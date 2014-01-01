package net.dhleong.acl.world;

import java.util.SortedMap;

import net.dhleong.acl.enums.ObjectType;

public class ArtemisDrone extends BaseArtemisObject implements ArtemisBearable {
	private float mBearing = Float.MIN_VALUE;
	private float mVelocity = -1;
	private float mSteering = -1;

	public ArtemisDrone(int objId) {
		super(objId, null);
	}

	@Override
	public ObjectType getType() {
		return ObjectType.DRONE;
	}

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
	public void updateFrom(ArtemisObject other) {
		super.updateFrom(other);

		if (other instanceof ArtemisDrone) {
			ArtemisDrone drone = (ArtemisDrone) other;

			if (drone.mBearing != Float.MIN_VALUE) {
				mBearing = drone.mBearing;
			}

			if (drone.mVelocity != -1) {
				mVelocity = drone.mVelocity;
			}

			if (drone.mSteering != -1) {
				mSteering = drone.mSteering;
			}
		}
	}

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Heading", mBearing, Float.MIN_VALUE, includeUnspecified);
    	putProp(props, "Velocity", mVelocity, -1, includeUnspecified);
    	putProp(props, "Rudder", mSteering, -1, includeUnspecified);
    }
}