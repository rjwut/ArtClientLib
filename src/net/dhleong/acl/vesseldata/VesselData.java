package net.dhleong.acl.vesseldata;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import net.dhleong.acl.protocol.Version;

/**
 * Contains all the information extracted from the vesselData.xml file.
 * @author rjwut
 */
public class VesselData {
	private static VesselData instance;

	public static void load(File artemisInstallPath) throws VesselDataException {
		File xml = new File(artemisInstallPath, "dat" + File.separatorChar + "vesselData.xml");
		load(xml.toURI());
	}

	public static VesselData get() {
		if (instance == null) {
			load();
		}

		return instance;
	}

	private static void load() {
		try {
			load(VesselData.class.getResource("vesselData.xml").toURI());
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex); // shouldn't happen
		} catch (VesselDataException ex) {
			throw new RuntimeException(ex); // shouldn't happen
		}
	}

	private static void load(URI uri) throws VesselDataException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			SAXVesselDataHandler handler = new SAXVesselDataHandler();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(uri.toString());
			instance = handler.vesselData;
		} catch (SAXException ex) {
			throw new VesselDataException(ex);
		} catch (ParserConfigurationException ex) {
			throw new VesselDataException(ex);
		} catch (IOException ex) {
			throw new VesselDataException(ex);
		}
	}

	Version version;
	List<Faction> factions = new ArrayList<Faction>();
	Map<Integer, Vessel> vessels = new LinkedHashMap<Integer, Vessel>();

	VesselData(String version) {
		this.version = new Version(version);
	}

	public Version getVersion() {
		return version;
	}

	public Faction getFaction(int id) {
		return factions.get(id);
	}

	public Vessel getVessel(int id) {
		return vessels.get(Integer.valueOf(id));
	}
}