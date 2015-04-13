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

	/**
	 * Loads the vesselData.xml file from the Artemis installation found at the
	 * given path.
	 */
	public static void load(File artemisInstallPath) throws VesselDataException {
		File xml = new File(artemisInstallPath, "dat" + File.separatorChar + "vesselData.xml");
		load(xml.toURI());
	}

	/**
	 * Returns the VesselData instance. If load(File) has been invoked, the
	 * VesselData object will reflect whatever customizations have been made on
	 * that Artemis installation. Otherwise, it will contain the values for a
	 * stock Artemis install.
	 */
	public static VesselData get() {
		if (instance == null) {
			load();
		}

		return instance;
	}

	/**
	 * Loads the default vesselData.xml packaged with ArtClientLib. This is
	 * invoked when get() is called without calling load(File) first.
	 */
	private static void load() {
		try {
			load(VesselData.class.getResource("vesselData.xml").toURI());
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex); // shouldn't happen
		} catch (VesselDataException ex) {
			throw new RuntimeException(ex); // shouldn't happen
		}
	}

	/**
	 * Parses the vessel data XML file at the given URI and stores the result
	 * in the VesselData.instance static field.
	 */
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

	/**
	 * Returns the version of Artemis reported by vesselData.xml. Note that this
	 * does not neccessarily match the version reported by the protocol; the
	 * version in vesselData.xml is known to lag behind the actual version
	 * number.
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Returns the Faction represented by the given ID.
	 */
	public Faction getFaction(int id) {
		return factions.get(id);
	}

	/**
	 * Returns the Vessel represented by the given ID.
	 */
	public Vessel getVessel(int id) {
		return vessels.get(Integer.valueOf(id));
	}
}