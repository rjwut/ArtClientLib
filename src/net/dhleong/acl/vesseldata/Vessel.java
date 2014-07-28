package net.dhleong.acl.vesseldata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.dhleong.acl.enums.OrdnanceType;
import net.dhleong.acl.enums.VesselAttribute;


/**
 * Corresponds to the <vessel> element in vesselData.xml.
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

	public int getId() {
		return id;
	}

	public int getSide() {
		return side;
	}

	public Faction getFaction() {
		return VesselData.get().getFaction(side);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public VesselAttribute[] getAttributes() {
		return (VesselAttribute[]) attributes.toArray();
	}

	public boolean is(VesselAttribute attribute) {
		return attributes.contains(attribute);
	}

	public String getMeshFile() {
		return meshFile;
	}

	public String getDiffuseFile() {
		return diffuseFile;
	}

	public String getGlowFile() {
		return glowFile;
	}

	public String getSpecularFile() {
		return specularFile;
	}

	public float getScale() {
		return scale;
	}

	public int getPushRadius() {
		return pushRadius;
	}

	public int getForeShields() {
		return foreShields;
	}

	public int getAftShields() {
		return aftShields;
	}

	public float getTurnRate() {
		return turnRate;
	}

	public float getTopSpeed() {
		return topSpeed;
	}

	public float getEfficiency() {
		return efficiency;
	}

	public int getFleetAiCommonality() {
		return fleetAiCommonality;
	}

	public int getFighterCount() {
		return fighterCount;
	}

	public BeamPort[] getBeamPorts() {
		return (BeamPort[]) beamPorts.toArray();
	}

	public VesselPoint[] getTorepedoTubes() {
		return (BeamPort[]) torpedoTubes.toArray();
	}

	public int getTorpedoStorage(OrdnanceType type) {
		return torpedoStorage.get(type).intValue();
	}

	public boolean hasTorpedoes() {
		return !torpedoTubes.isEmpty() && !torpedoStorage.isEmpty();
	}

	public VesselPoint[] getEnginePorts() {
		return (VesselPoint[]) enginePorts.toArray();
	}

	public VesselPoint[] getImpulsePoints() {
		return (VesselPoint[]) impulsePoints.toArray();
	}

	public VesselPoint[] getManeuverPoints() {
		return (VesselPoint[]) maneuverPoints.toArray();
	}
}