package net.dhleong.acl.world;

import net.dhleong.acl.enums.ObjectType;

public class ArtemisDrone extends ArtemisGenericObject implements ArtemisBearable {
	public ArtemisDrone(int objId) {
		super(objId, null, ObjectType.DRONE);
	}

	private float bearing;
	private float velocity;
	private float steering;

	@Override
	public float getBearing() {
		return bearing;
	}

	@Override
	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	@Override
	public float getVelocity() {
		return velocity;
	}

	@Override
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	@Override
	public float getSteering() {
		return steering;
	}

	@Override
	public void setSteering(float steering) {
		this.steering = steering;
	}

	@Override
	public void updateFrom(ArtemisPositionable other) {
		super.updateFrom(other);

		if (other instanceof ArtemisDrone) {
			ArtemisDrone drone = (ArtemisDrone) other;
			this.bearing = drone.bearing;
			this.velocity = drone.velocity;
			this.steering = drone.steering;
		}
	}
}