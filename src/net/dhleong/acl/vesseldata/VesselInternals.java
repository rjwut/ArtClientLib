package net.dhleong.acl.vesseldata;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.dhleong.acl.util.GridCoord;

/**
 * An object which describes the internal system grid of a Vessel. Note that while the .snt file
 * contains an entry for every coordinate in the internal grid, this object will only contain 
 * VesselNodes for coordinates that correspond to a ship system or a hallway.
 * @author rjwut
 */
public class VesselInternals implements Iterable<Map.Entry<GridCoord, VesselNode>> {
	public static final int GRID_SIZE_X = 5;
	public static final int GRID_SIZE_Y = 5;
	public static final int GRID_SIZE_Z = 10;

	private Map<GridCoord, VesselNode> map = new HashMap<GridCoord, VesselNode>();
	private byte[] buffer = new byte[VesselNode.BLOCK_SIZE];

	public VesselInternals(String sntPath) {
		URI uri;

		try {
			uri = VesselData.pathResolver.get(sntPath);
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}

		InputStream in = null;

		try {
			in = uri.toURL().openStream();
			load(new BufferedInputStream(in));
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					// don't care
				}
			}
		}
	}

	/**
	 * Builds a VesselInternals object from the .snt file read from the given InputStream.
	 */
	private void load(InputStream in) throws InterruptedException, IOException {
		for (int x = 0; x < GRID_SIZE_X; x++) {
			for (int y = 0; y < GRID_SIZE_Y; y++) {
				for (int z = 0; z < GRID_SIZE_Z; z++) {
					VesselNode node = new VesselNode(in, buffer);

					if (node.isAccessible()) {
						map.put(GridCoord.getInstance(x, y, z), node);
					}
				}
			}
		}
	}

	/**
	 * Returns the VesselNode located at the given internal grid coordinates, or null if there is
	 * no VesselNode at that location.
	 */
	public VesselNode get(int x, int y, int z) {
		return map.get(GridCoord.getInstance(x, y, z));
	}

	/**
	 * Iterates all VesselNodes.
	 */
	@Override
	public Iterator<Map.Entry<GridCoord, VesselNode>> iterator() {
		return map.entrySet().iterator();
	}
}