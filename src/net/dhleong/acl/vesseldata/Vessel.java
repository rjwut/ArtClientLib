package net.dhleong.acl.vesseldata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.dhleong.acl.enums.OrdnanceType;
import net.dhleong.acl.enums.VesselAttribute;

/**
 * Corresponds to the <vessel> element in vesselData.xml. Note that this
 * represents an entire class of ships, not an individual one.
 * @author rjwut
 */
public class Vessel {
	private int id;
	private int side;
	private String name;
	String description;
	private Set<VesselAttribute> attributes;
	String meshFile;
	String diffuseFile;
	String glowFile;
	String specularFile;
	float scale;
	int pushRadius;
	int foreShields;
	int aftShields;
	float turnRate;
	float topSpeed;
	float efficiency;
	int fleetAiCommonality;
	int fighterCount;
	float production;
	List<BeamPort> beamPorts = new ArrayList<BeamPort>();
	List<VesselPoint> torpedoTubes = new ArrayList<VesselPoint>();
	Map<OrdnanceType, Integer> torpedoStorage = new LinkedHashMap<OrdnanceType, Integer>();
	List<WeaponPort> dronePorts = new ArrayList<WeaponPort>();
	List<VesselPoint> enginePorts = new ArrayList<VesselPoint>();
	List<VesselPoint> impulsePoints = new ArrayList<VesselPoint>();
	List<VesselPoint> maneuverPoints = new ArrayList<VesselPoint>();

	Vessel(int uniqueID, int side, String className, String broadType) {
		id = uniqueID;
		this.side = side;
		name = className;
		attributes = VesselAttribute.build(broadType);

		for (OrdnanceType type : OrdnanceType.values()) {
			torpedoStorage.put(type, Integer.valueOf(0));
		}
	}

	/**
	 * Returns the Vessel's ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the Vessel's Faction ID. 
	 */
	public int getSide() {
		return side;
	}

	/**
	 * Returns the Faction to which this Vessel belongs.
	 */
	public Faction getFaction() {
		return VesselData.get().getFaction(side);
	}

	/**
	 * Returns this Vessel's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a short description of this Vessel.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns an array of this Vessel's VesselAttributes.
	 */
	public VesselAttribute[] getAttributes() {
		return attributes.toArray(new VesselAttribute[attributes.size()]);
	}

	/**
	 * Returns true if this Vessel has the given VesselAttribute; false
	 * otherwise.
	 */
	public boolean is(VesselAttribute attribute) {
		return attributes.contains(attribute);
	}

	/**
	 * Returns the 3D mesh filename.
	 */
	public String getMeshFile() {
		return meshFile;
	}

	/**
	 * Returns the diffuse image filename.
	 */
	public String getDiffuseFile() {
		return diffuseFile;
	}

	/**
	 * Returns the glow image filename.
	 */
	public String getGlowFile() {
		return glowFile;
	}

	/**
	 * Returns the specular image filename.
	 */
	public String getSpecularFile() {
		return specularFile;
	}

	/**
	 * Returns this Vessel's scale value. This presumably controls how large it
	 * is.
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Returns this Vessel's push radius. This presumably how close other
	 * objects can get before they are considered to have collided with this
	 * Vessel.
	 */
	public int getPushRadius() {
		return pushRadius;
	}

	/**
	 * Returns the initial strength of this Vessel's forward shields.
	 */
	public int getForeShields() {
		return foreShields;
	}

	/**
	 * Returns the initial strength of this Vessel's aft shields.
	 */
	public int getAftShields() {
		return aftShields;
	}

	/**
	 * Returns this Vessel's turn rate.
	 */
	public float getTurnRate() {
		return turnRate;
	}

	/**
	 * Returns this Vessel's top (impulse) speed.
	 */
	public float getTopSpeed() {
		return topSpeed;
	}

	/**
	 * Returns this Vessel's efficiency rating.
	 */
	public float getEfficiency() {
		return efficiency;
	}

	/**
	 * Returns this Vessel's fleet AI commonality value. It is unknown what
	 * exactly this value does.
	 */
	public int getFleetAiCommonality() {
		return fleetAiCommonality;
	}

	/**
	 * Returns the number of fighters this Vessel has. Only Vessels that were
	 * declared with the <carrier> element will have fighters, and presumably
	 * only those with VesselAttribute.CARRIER.
	 */
	public int getFighterCount() {
		return fighterCount;
	}

	/**
	 * Returns an array of BeamPort objects describing the beams with which this
	 * Vessel is equipped.
	 */
	public BeamPort[] getBeamPorts() {
		return beamPorts.toArray(new BeamPort[beamPorts.size()]);
	}

	/**
	 * Returns an array of VesselPoint objects describing the locations of the
	 * Vessel's torpedo tubes.
	 */
	public VesselPoint[] getTorepedoTubes() {
		return torpedoTubes.toArray(new BeamPort[torpedoTubes.size()]);
	}

	/**
	 * Returns the number of units of the given OrdnanceType this Vessel can
	 * carry.
	 */
	public int getTorpedoStorage(OrdnanceType type) {
		return torpedoStorage.get(type).intValue();
	}

	/**
	 * Returns true if this Vessel is capable of carrying torpedoes; false
	 * otherwise.
	 */
	public boolean hasTorpedoes() {
		return !torpedoTubes.isEmpty() && !torpedoStorage.isEmpty();
	}

	/**
	 * Returns an array of VesselPoint objects describing the locations of the
	 * Vessel's engine ports.
	 */
	public VesselPoint[] getEnginePorts() {
		return enginePorts.toArray(new VesselPoint[enginePorts.size()]);
	}

	/**
	 * Returns an array of VesselPoint objects describing the locations of the
	 * Vessel's impulse points.
	 */
	public VesselPoint[] getImpulsePoints() {
		return impulsePoints.toArray(new VesselPoint[impulsePoints.size()]);
	}

	/**
	 * Returns an array of VesselPoint objects describing the locations of the
	 * Vessel's maneuver points.
	 */
	public VesselPoint[] getManeuverPoints() {
		return maneuverPoints.toArray(new VesselPoint[maneuverPoints.size()]);
	}
}