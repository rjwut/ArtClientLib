package net.dhleong.acl.vesseldata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.dhleong.acl.enums.FactionAttribute;

/**
 * Corresponds to the <hullRace> element in vesselData.xml.
 * @author rjwut
 */
public class Faction {
	private int id;
	private String name;
	private Set<FactionAttribute> attributes;
	List<Taunt> taunts = new ArrayList<Taunt>(3);

	Faction(int id, String name, String keys) {
		this.id = id;
		this.name = name;
		attributes = FactionAttribute.build(keys);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public FactionAttribute[] getAttributes() {
		return (FactionAttribute[]) attributes.toArray();
	}

	public boolean is(FactionAttribute attribute) {
		return attributes.contains(attribute);
	}

	public Taunt[] getTaunts() {
		return (Taunt[]) taunts.toArray();
	}
}