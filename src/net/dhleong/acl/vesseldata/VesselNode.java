package net.dhleong.acl.vesseldata;

import java.io.IOException;
import java.io.InputStream;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.util.ByteArrayReader;

public class VesselNode {
	static final int BLOCK_SIZE = 32;
	private static final int EMPTY_NODE_VALUE = -2;
	private static final int HALLWAY_NODE_VALUE = -1;

	private float x;
	private float y;
	private float z;
	private boolean accessible;
	private ShipSystem system;

	VesselNode (InputStream in, byte[] buffer) throws InterruptedException, IOException {
		ByteArrayReader.readBytes(in, BLOCK_SIZE, buffer);
		ByteArrayReader reader = new ByteArrayReader(buffer);
		x = reader.readFloat();
		y = reader.readFloat();
		z = reader.readFloat();
		int typeValue = reader.readInt();
		accessible = typeValue != EMPTY_NODE_VALUE;

		if (accessible && typeValue != HALLWAY_NODE_VALUE) {
			system = ShipSystem.values()[typeValue];
		}
	}

	/**
	 * Returns the X-coordinate of this node relative to the origin of the ship's model coordinates.
	 */
	public float getRelativeX() {
		return x;
	}

	/**
	 * Returns the Y-coordinate of this node relative to the origin of the ship's model coordinates.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Returns the Z-coordinate of this node relative to the origin of the ship's model coordinates.
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Returns true if it's possible for DAMCON teams to access this node; false otherwise.
	 */
	public boolean isAccessible() {
		return accessible;
	}

	/**
	 * Returns the ShipSystem found here, or null if there is none.
	 */
	public ShipSystem getSystem() {
		return system;
	}
}
