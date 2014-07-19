package net.dhleong.acl.vesseldata;

import java.util.HashMap;
import java.util.Map;

import net.dhleong.acl.enums.OrdnanceType;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXVesselDataHandler extends DefaultHandler {
	private interface Parser {
		void parse(Attributes attrs);
	}

	VesselData vesselData;
	private Map<String, Parser> parsers = new HashMap<String, Parser>();
	private Faction faction;
	private Vessel vessel;

	SAXVesselDataHandler() {
		parsers.put("art", new ArtParser());
		parsers.put("beam_port", new BeamPortParser());
		parsers.put("carrier", new CarrierParser());
		parsers.put("drone_port", new DronePortParser());
		parsers.put("engine_port", new EnginePortParser());
		parsers.put("fleet_ai", new FleetAiParser());
		parsers.put("hullRace", new HullRaceParser());
		parsers.put("impulse_point", new ImpulsePointParser());
		parsers.put("long_desc", new LongDescParser());
		parsers.put("maneuver_point", new ManeuverPointParser());
		parsers.put("performance", new PerformanceParser());
		parsers.put("production", new ProductionParser());
		parsers.put("shields", new ShieldsParser());
		parsers.put("taunt", new TauntParser());
		parsers.put("torpedo_storage", new TorpedoStorageParser());
		parsers.put("torpedo_tube", new TorpedoTubeParser());
		parsers.put("vessel", new VesselParser());
		parsers.put("vessel_data", new VesselDataParser());
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attrs) throws SAXException {
		Parser parser = parsers.get(qName);

		if (parser != null) {
			parser.parse(attrs);
		}
	}

	private static float parseFloat(Attributes attrs, String name) {
		String value = attrs.getValue(name);
		return value != null ? Float.parseFloat(value) : 0.0f;
	}

	private static int parseInt(Attributes attrs, String name) {
		String value = attrs.getValue(name);
		return value != null ? Integer.parseInt(value) : 0;
	}

	private static void parseVesselPoint(VesselPoint point, Attributes attrs) {
		point.x = Float.parseFloat(attrs.getValue("x"));
		point.y = Float.parseFloat(attrs.getValue("y"));
		point.z = Float.parseFloat(attrs.getValue("z"));
	}

	public static void parseWeaponPort(WeaponPort port, Attributes attrs) {
		parseVesselPoint(port, attrs);
		port.damage = parseFloat(attrs, "damage");
		port.cycleTime = parseFloat(attrs, "cycletime");
		port.range = parseInt(attrs, "range");
	}

	private class ArtParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			vessel.meshFile = attrs.getValue("meshfile");
			vessel.diffuseFile = attrs.getValue("diffuseFile");
			vessel.glowFile = attrs.getValue("glowFile");
			vessel.specularFile = attrs.getValue("specularFile");
			vessel.scale = parseFloat(attrs, "scale");
		}
	}

	private class BeamPortParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			BeamPort port = new BeamPort();
			parseWeaponPort(port, attrs);
			port.arcWidth = parseFloat(attrs, "arcwidth");
			vessel.beamPorts.add(port);
		}
	}

	private class CarrierParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			vessel.fighterCount = parseInt(attrs, "complement");
		}
	}

	private class DronePortParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			WeaponPort port = new WeaponPort();
			parseWeaponPort(port, attrs);
			vessel.dronePorts.add(port);
		}
	}

	private class EnginePortParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			VesselPoint point = new VesselPoint();
			parseVesselPoint(point, attrs);
			vessel.enginePorts.add(point);
		}
	}

	private class FleetAiParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			vessel.fleetAiCommonality = parseInt(attrs, "commonality");
		}
	}

	private class HullRaceParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			int id = parseInt(attrs, "ID");
			faction = new Faction(
					id,
					attrs.getValue("name"),
					attrs.getValue("keys")
			);

			while (vesselData.factions.size() <= id) {
				vesselData.factions.add(null);
			}

			vesselData.factions.set(id, faction);
		}
	}

	private class ImpulsePointParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			VesselPoint point = new VesselPoint();
			parseVesselPoint(point, attrs);
			vessel.impulsePoints.add(point);
		}
	}

	private class LongDescParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			vessel.description = attrs.getValue("text").replaceAll("\\^", "\n");
		}
	}

	private class ManeuverPointParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			VesselPoint point = new VesselPoint();
			parseVesselPoint(point, attrs);
			vessel.maneuverPoints.add(point);
		}
	}

	private class PerformanceParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			vessel.turnRate = parseFloat(attrs, "turnrate");
			vessel.topSpeed = parseFloat(attrs, "topspeed");
			vessel.efficiency = parseFloat(attrs, "efficiency");
		}
	}

	private class ProductionParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			vessel.production = parseFloat(attrs, "production");
		}
	}

	private class ShieldsParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			vessel.foreShields = parseInt(attrs, "front");
			vessel.aftShields = parseInt(attrs, "back");
		}
	}

	private class TauntParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			faction.taunts.add(new Taunt(
					attrs.getValue("immunity"),
					attrs.getValue("text")
			));
		}
	}

	private class TorpedoStorageParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			OrdnanceType type = OrdnanceType.values()[parseInt(attrs, "type")];
			Integer amount = Integer.valueOf(attrs.getValue("amount"));
			vessel.torpedoStorage.put(type, amount);
		}
	}

	private class TorpedoTubeParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			VesselPoint point = new VesselPoint();
			parseVesselPoint(point, attrs);
			vessel.torpedoTubes.add(point);
		}
	}

	private class VesselParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			Integer id = Integer.valueOf(attrs.getValue("uniqueID"));
			vessel = new Vessel(
					id.intValue(),
					parseInt(attrs, "side"),
					attrs.getValue("classname"),
					attrs.getValue("broadType")
			);
			vesselData.vessels.put(id, vessel);
		}
	}

	private class VesselDataParser implements Parser {
		@Override
		public void parse(Attributes attrs) {
			vesselData = new VesselData(attrs.getValue("version"));
		}
	}
}