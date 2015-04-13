package net.dhleong.acl.iface;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.UnknownPacket;
import net.dhleong.acl.protocol.UnparsedPacket;
import net.dhleong.acl.protocol.Version;
import net.dhleong.acl.protocol.core.setup.VersionPacket;
import net.dhleong.acl.util.BitField;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.util.TextUtil;

/**
 * Facilitates reading packets from an InputStream. This object may be reused to
 * read as many packets as desired from a single InputStream. Individual packet
 * classes can read their properties by using the read*() methods on this class.
 * @author rjwut
 */
public class PacketReader {
	private ConnectionType connType;
	private InputStream in;
	private byte[] buffer = new byte[4];
	private boolean parse = true;
	private PacketFactoryRegistry factoryRegistry;
	private ListenerRegistry listenerRegistry;
	private Version version;
	private byte[] payload;
	private int offset;
	private SortedMap<String, byte[]> unknownProps;
	private ObjectType objectType;
	private int objectId;
	private BitField bitField;
	private SortedMap<String, byte[]> unknownObjectProps;

	/**
	 * Wraps the given InputStream with this PacketReader.
	 */
	public PacketReader(ConnectionType connType, InputStream in,
			PacketFactoryRegistry factoryRegistry,
			ListenerRegistry listenerRegistry) {
		this.connType = connType;
		this.in = in;
		this.factoryRegistry = factoryRegistry;
		this.listenerRegistry = listenerRegistry;
	}

	/**
	 * If set to false, all packets will be returned as UnknownPackets. This is
	 * useful for testing purposes to easily capture packet payloads in their
	 * raw form without bothering to parse any of them. By default, this
	 * property is true, meaning that all known packets will be parsed.
	 */
	public void setParsePackets(boolean parse) {
		this.parse = parse;
	}

	/**
	 * Returns the server Version, or null if unknown.
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Reads a single packet and returns it. The given Debugger will also be
	 * notified.
	 */
	public ArtemisPacket readPacket(Debugger debugger) throws ArtemisPacketException {
		offset = 0;
		objectType = null;
		objectId = 0;
		bitField = null;
		unknownProps = new TreeMap<String, byte[]>();
		unknownObjectProps = new TreeMap<String, byte[]>();

		// header (0xdeadbeef)
		final int header = readIntFromStream();

		if (header != ArtemisPacket.HEADER) {
			throw new ArtemisPacketException(
					"Illegal packet header: " + Integer.toHexString(header)
			);
		}

		// packet length
		final int len = readIntFromStream();

		if (len <= 8) {
			throw new ArtemisPacketException(
					"Illegal packet length: " + len
			);
		}

		// connection type
		final int connectionTypeValue = readIntFromStream();
		final ConnectionType connectionType = ConnectionType.fromInt(connectionTypeValue);

		if (connectionType == null) {
			throw new ArtemisPacketException(
					"Unknown connection type: " + connectionTypeValue
			);
		}

		if (connectionType != connType) {
			throw new ArtemisPacketException(
					"Connection type mismatch: expected " + connType +
					", got " + connectionType
			);
		}

		// padding
		final int padding = readIntFromStream();

		if (padding != 0) {
			throw new ArtemisPacketException(
					"No empty padding after connection type?",
					connType
			);
		}

		// remaining bytes
		final int remainingBytes = readIntFromStream();
		final int expectedRemainingBytes = len - 20;

		if (remainingBytes != expectedRemainingBytes) {
			throw new ArtemisPacketException(
					"Packet length discrepancy: total length = " + len +
					"; expected " + expectedRemainingBytes +
					" for remaining bytes field, but got " +
					remainingBytes,
					connType
			);
		}

		// packet type
		final int packetType = readIntFromStream();

		// payload
		// The preamble was 24 bytes (6 ints), so the payload size is the size
		// of the whole packet minus 24 bytes.
		final int remaining = len - 24;
		payload = new byte[remaining];

		try {
			int bytesRead = in.read(payload, 0, remaining);

			if (bytesRead < remaining) {
				throw new EOFException("Stream is closed");
			}
		} catch (IOException ex) {
			throw new ArtemisPacketException(ex, connType, packetType);
		}

		debugger.onRecvPacketBytes(connType, packetType, payload);

		// Find the PacketFactory that knows how to handle this packet type
		PacketFactory factory = null;

		if (parse) {
			factory = factoryRegistry.get(connType, packetType,
					hasMore() ? peekByte() : 0x00);
		}

		if (factory == null) {
			// No factory can handle this; return an UnknownPacket
			UnknownPacket packet = new UnknownPacket(connType, packetType, payload);
			offset = payload.length;
			debugger.onRecvUnparsedPacket(packet);
			return packet;
		}

		if (listenerRegistry.listeningFor(factory.getFactoryClass())) {
			// We're interested in this packet; parse and build it
			ArtemisPacket packet;

			try {
				packet = factory.build(this);
			} catch (ArtemisPacketException ex) {
				throw new ArtemisPacketException(ex, connType, packetType, payload);
			} catch (RuntimeException ex) {
				throw new ArtemisPacketException(ex, connType, packetType, payload);
			}

			if (packet instanceof VersionPacket) {
				// We got a VersionPacket; store the version
				version = ((VersionPacket) packet).getVersion();
			}

			int bytesLeft = payload.length - offset;

			if (bytesLeft > 0) {
				debugger.warn(
						"Unread bytes [" +
						packet.getClass().getSimpleName() + "]: " +
						TextUtil.byteArrayToHexString(readBytes(bytesLeft))
				);
			}

			debugger.onRecvParsedPacket(packet);
			return packet;
		}

		// We don't have any listeners for this packet
		UnparsedPacket packet = new UnparsedPacket(connType, packetType, payload);
		debugger.onRecvUnparsedPacket(packet);
		return packet;
	}

	/**
	 * Returns true if the packet currently being read has more data; false
	 * otherwise.
	 */
	public boolean hasMore() {
		return offset < payload.length && (bitField == null || payload[offset] != 0);
	}

	/**
	 * Returns the next byte in the current packet's payload without moving the
	 * pointer.
	 */
	public byte peekByte() {
		return payload[offset];
	}

	/**
	 * Reads a single byte from the current packet's payload.
	 */
	public byte readByte() {
		return payload[offset++];
	}

	/**
	 * Convenience method for readByte(bit, 0).
	 */
	public byte readByte(Enum<?> bit) {
		return readByte(bit, (byte) 0);
	}

	/**
	 * Reads a single byte from the current packet's payload if the indicated
	 * bit in the current BitField is on. Otherwise, the pointer is not moved,
	 * and the given default value is returned.
	 */
	public byte readByte(Enum<?> bit, byte defaultValue) {
		return bitField.get(bit) ? readByte() : defaultValue;
	}

	/**
	 * Reads the indicated number of bytes from the current packet's payload,
	 * then coerces the zeroeth byte read into a BoolState.
	 */
	public BoolState readBool(int byteCount) {
		BoolState b = readBool(payload, offset);
		offset += byteCount;
		return b;
	}

	/**
	 * Reads the indicated number of bytes from the current packet's payload if
	 * the indicated bit in the current BitField is on, then coerces the zeroeth
	 * byte read into a BoolState. Otherwise, the pointer is not moved, and
	 * BoolState.UNKNOWN is returned.
	 */
	public BoolState readBool(Enum<?> bit, int bytes) {
		return bitField.get(bit) ? readBool(bytes) : BoolState.UNKNOWN;
	}

	/**
	 * Reads a short from the current packet's payload.
	 */
	public int readShort() {
		int val = readShort(payload, offset);
		offset += 2;
		return val;
	}

	/**
	 * Convenience method for readShort(bit, 0).
	 */
	public int readShort(Enum<?> bit) {
		return readShort(bit, 0);
	}

	/**
	 * Reads a short from the current packet's payload if the indicated bit in
	 * the current BitField is on. Otherwise, the pointer is not moved, and the
	 * given default value is returned.
	 */
	public int readShort(Enum<?> bit, int defaultValue) {
		return bitField.get(bit) ? readShort() : defaultValue;
	}

	/**
	 * Reads an int from the current packet's payload.
	 */
	public int readInt() {
		int val = readInt(payload, offset);
		offset += 4;
		return val;
	}

	/**
	 * Convenience method for readInt(bit, -1).
	 */
	public int readInt(Enum<?> bit) {
		return readInt(bit, -1);
	}

	/**
	 * Reads an int from the current packet's payload if the indicated bit in
	 * the current BitField is on. Otherwise, the pointer is not moved, and the
	 * given default value is returned.
	 */
	public int readInt(Enum<?> bit, int defaultValue) {
		return bitField.get(bit) ? readInt() : defaultValue;
	}

	/**
	 * Reads a float from the current packet's payload.
	 */
	public float readFloat() {
		float val = readFloat(payload, offset);
		offset += 4;
		return val;
	}

	/**
	 * Reads a float from the current packet's payload if the indicated bit in
	 * the current BitField is on. Otherwise, the pointer is not moved, and the
	 * given default value is returned.
	 */
	public float readFloat(Enum<?> bit, float defaultValue) {
		return bitField.get(bit) ? readFloat() : defaultValue;
	}

	/**
	 * Reads a String from the current packet's payload.
	 */
	public String readString() {
		int charCount = readInt();
		int byteCount = charCount * 2;
		byte[] bytes = Arrays.copyOfRange(payload, offset, offset + byteCount - 2);
		int i = 0;

		// check for "early" null
		for ( ; i < bytes.length; i += 2) {
			if (bytes[i] == 0 && bytes[i + 1] == 0) {
				break;
			}
		}

		if (i != bytes.length) {
			bytes = Arrays.copyOfRange(bytes, 0, i);
		}

		offset += byteCount;
		return new String(bytes, ArtemisPacket.CHARSET);
	}

	/**
	 * Reads a String from the current packet's payload if the indicated bit in
	 * the current BitField is on. Otherwise, the pointer is not moved, and null
	 * is returned.
	 */
	public String readString(Enum<?> bit) {
		return bitField.get(bit) ? readString() : null;
	}

	/**
	 * Reads the given number of bytes from the current packet's payload.
	 */
	public byte[] readBytes(int byteCount) {
		byte[] bytes = Arrays.copyOfRange(payload, offset, offset + byteCount);
		offset += byteCount;
		return bytes;
	}

	/**
	 * Reads the given number of bytes from the current packet's payload if
	 * the indicated bit in the current BitField is on. Otherwise, the pointer
	 * is not moved, and null is returned.
	 */
	public byte[] readBytes(Enum<?> bit, int byteCount) {
		return bitField.get(bit) ? readBytes(byteCount) : null;
	}

	/**
	 * Reads the given number of bytes from the current packet's payload and
	 * puts them in the unknown property map with the indicated name.
	 */
	public void readUnknown(String name, int byteCount) {
		unknownProps.put(name, readBytes(byteCount));
	}

	/**
	 * Reads the given number of bytes from the current packet's payload and
	 * puts them in the unknown object property map with the indicated name.
	 */
	public void readObjectUnknown(String name, int byteCount) {
		unknownObjectProps.put(name, readBytes(byteCount));
	}

	/**
	 * if the indicated bit in the current BitField is off, this method returns
	 * without doing anything. Otherwise, it acts as a convenience method for
	 * readObjectUnknown(bit.name(), byteCount).
	 */
	public void readObjectUnknown(Enum<?> bit, int byteCount) {
		if (bitField.get(bit)) {
			readObjectUnknown(bit.name(), byteCount);
		}
	}

	/**
	 * Skips the given number of bytes in the current packet's payload.
	 */
	public void skip(int byteCount) {
		offset += byteCount;
	}

	/**
	 * Returns the unknown properties previously stored by readUnknown().
	 */
	public SortedMap<String, byte[]> getUnknownProps() {
		return unknownProps;
	}

	/**
	 * Starts reading an object from an ObjectUpdatingPacket. This will read off
	 * an object type value (byte), an object ID (int) and (if a bits enum value
	 * array is given) a BitField from the current packet's payload. This also
	 * clears the unknownObjectProps property.
	 */
	public void startObject(Enum<?>[] bits) {
		byte typeByte = readByte();
		objectType = ObjectType.fromId(typeByte);

		if (objectType == null) {
			throw new IllegalStateException("Unknown object type: " + typeByte);
		}

		objectId = readInt();

		if (bits != null) {
			bitField = new BitField(bits, payload, offset);
	        offset += bitField.getByteCount();
		} else {
			bitField = null;
		}

		unknownObjectProps = new TreeMap<String, byte[]>();
	}

	/**
	 * Returns true if the current BitField has the indicated bit turned on.
	 */
	public boolean has(Enum<?> bit) {
		return bitField.get(bit);
	}

	/**
	 * Returns the type of the current object being read from the payload.
	 */
	public ObjectType getObjectType() {
		return objectType;
	}

	/**
	 * Returns the ID of the current object being read from the payload.
	 */
	public int getObjectId() {
		return objectId;
	}

	/**
	 * Returns the unknown object properties previously stored by
	 * readObjectUnknown().
	 */
	public SortedMap<String, byte[]> getUnknownObjectProps() {
		return unknownObjectProps;
	}

	/**
	 * Reads an int value directly from the InputStream wrapped by this object.
	 * This is used to read values for the preamble.
	 */
	private int readIntFromStream() throws ArtemisPacketException {
		try {
			if (in.read(buffer, 0, 4) < 4) {
				throw new EOFException("Stream is closed");
			}

			return readInt(buffer, 0);
		} catch (IOException ex) {
			throw new ArtemisPacketException(ex);
		}
	}


	/**
	 * Reads a BoolState from the indicated offset in the given byte array.
	 */
	public static BoolState readBool(byte[] bytes, int offset) {
		return BoolState.from(bytes[offset] == 1);
	}

	/**
	 * Reads a short from the indicated offset in the given byte array.
	 */
	public static int readShort(byte[] bytes, int offset) {
		return (0xff & (bytes[offset + 1] << 8)) | (0xff & bytes[offset]);
	}

	/**
	 * Reads an int from the indicated offset in the given byte array.
	 */
	public static int readInt(byte[] bytes, int offset) {
		return	((0xff & bytes[offset + 3]) << 24) |
				((0xff & bytes[offset + 2]) << 16) |
				((0xff & bytes[offset + 1]) << 8) |
				(0xff & bytes[offset]);
	}

	/**
	 * Reads a float from the indicated offset in the given byte array.
	 */
	public static float readFloat(byte[] bytes, int offset) {
		return Float.intBitsToFloat(readInt(bytes, offset));
	}
}