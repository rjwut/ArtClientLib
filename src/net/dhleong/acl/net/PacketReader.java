package net.dhleong.acl.net;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.net.comms.CommsIncomingPacket;
import net.dhleong.acl.net.comms.IncomingAudioPacket;
import net.dhleong.acl.net.eng.EngGridUpdatePacket;
import net.dhleong.acl.net.helm.JumpStatusPacket;
import net.dhleong.acl.net.setup.AllShipSettingsPacket;
import net.dhleong.acl.net.setup.StationStatusPacket;
import net.dhleong.acl.net.setup.VersionPacket;
import net.dhleong.acl.net.setup.WelcomePacket;
import net.dhleong.acl.util.BitField;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.util.Util;

public class PacketReader {
	private InputStream in;
	private byte[] buffer = new byte[4];
	private boolean parse = true;
	private byte[] payload;
	private int offset;
	private SortedMap<String, byte[]> unknownFields;
	private ObjectType objectType;
	private int objectId;
	private BitField bitField;
	private SortedMap<String, byte[]> unknownObjectFields;

	public PacketReader(String hex) {
		this(TextUtil.hexToByteArray(hex));
	}

	public PacketReader(byte[] bytes) {
		this(new ByteArrayInputStream(bytes));
	}

	public PacketReader(InputStream in) {
		this.in = in;

		if (Util.debug) {
			unknownFields = new TreeMap<String, byte[]>();
			unknownObjectFields = new TreeMap<String, byte[]>();
		}
	}

	public void setParsePackets(boolean parse) {
		this.parse = parse;
	}

	public ArtemisPacket readPacket() throws ArtemisPacketException {
		offset = 0;
		objectType = null;
		objectId = 0;
		bitField = null;

		if (unknownFields != null) {
			unknownFields.clear();
			unknownObjectFields.clear();
		}

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
			//return new BaseArtemisPacket(ConnectionType.SERVER);
		}

		// connection type
		final int connectionTypeValue = readIntFromStream();
		final ConnectionType connectionType = ConnectionType.fromInt(connectionTypeValue);

		if (connectionType == null) {
			throw new ArtemisPacketException(
					"Unknown connection type: " + connectionTypeValue
			);
		}

		if (connectionType != ConnectionType.SERVER) {
			throw new ArtemisPacketException(
					"Connection type mismatch: expected SERVER, got " +
					connectionType
			);
		}

		// padding
		final int padding = readIntFromStream();

		if (padding != 0) {
			throw new ArtemisPacketException(
					"No empty padding after connection type?"
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
					remainingBytes
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
			throw new ArtemisPacketException(ex);
		}

		return buildPacket(packetType);
	}

	public boolean hasMore() {
		return offset < payload.length && (bitField == null || payload[offset] != 0);
	}

	private ArtemisPacket buildPacket(int pktType) throws ArtemisPacketException {
		if (!parse) {
			return new UnknownPacket(ConnectionType.SERVER, pktType, payload);
		}

		switch (pktType) {
		case EngGridUpdatePacket.TYPE:
			return new EngGridUpdatePacket(this);

		case CommsIncomingPacket.TYPE:
			return new CommsIncomingPacket(this);

		case IncomingAudioPacket.TYPE:
			return new IncomingAudioPacket(this);

		case DestroyObjectPacket.TYPE:
			return new DestroyObjectPacket(this);

		case GameMessagePacket.TYPE:
			// This is a generic type; a few other global messages are included
			switch(payload[0]) {
			case GameStartPacket.MSG_TYPE:
				return new GameStartPacket(this);
			case GameOverPacket.MSG_TYPE:
				return new GameOverPacket(this);
			case AllShipSettingsPacket.MSG_TYPE:
				return new AllShipSettingsPacket(this);
			case JumpStatusPacket.MSG_TYPE_BEGIN:
			case JumpStatusPacket.MSG_TYPE_END:
				return new JumpStatusPacket(this);
			case GameMessagePacket.MSG_TYPE:
				return new GameMessagePacket(this);
			default:
				return new UnknownPacket(
						ConnectionType.SERVER,
						GameMessagePacket.TYPE,
						payload
				);
			}

		case StationStatusPacket.TYPE:
			return new StationStatusPacket(this);

		case IntelPacket.TYPE:
        	return new IntelPacket(this);

		case WelcomePacket.TYPE:
			return new WelcomePacket(this);

		case VersionPacket.TYPE:
			return new VersionPacket(this);

		case ArtemisPacket.WORLD_TYPE:
			// ooh, crazy world type; switch for kid types
			final int typeVal = payload[0];

			if (typeVal == 0) {
				// some sort of empty packet... possibly keepalive?
				return null;
			}

			ObjectType type = ObjectType.fromId(typeVal);

			if (type != null) {
				return type.buildPacket(this);
			}

			// Unhandled? Fall back on base packet class
			// $FALL-THROUGH$

		default:
			return new UnknownPacket(ConnectionType.SERVER, pktType, payload);
        }       
	}

	public byte peekByte() {
		return payload[offset];
	}

	public byte readByte() {
		return payload[offset++];
	}

	public byte readByte(Enum<?> bit) {
		return readByte(bit, (byte) 0);
	}

	public byte readByte(Enum<?> bit, byte defaultValue) {
		return bitField.get(bit) ? readByte() : defaultValue;
	}

	public BoolState readBool(int byteCount) {
		BoolState b = readBool(payload, offset);
		offset += byteCount;
		return b;
	}

	public BoolState readBool(Enum<?> bit, int bytes) {
		return bitField.get(bit) ? readBool(bytes) : BoolState.UNKNOWN;
	}

	public int readShort() {
		int val = readShort(payload, offset);
		offset += 2;
		return val;
	}

	public int readShort(Enum<?> bit) {
		return readShort(bit, 0);
	}

	public int readShort(Enum<?> bit, int defaultValue) {
		return bitField.get(bit) ? readShort() : defaultValue;
	}

	public int readInt() {
		int val = readInt(payload, offset);
		offset += 4;
		return val;
	}

	public int readInt(Enum<?> bit) {
		return readInt(bit, -1);
	}

	public int readInt(Enum<?> bit, int defaultValue) {
		return bitField.get(bit) ? readInt() : defaultValue;
	}

	public long readLong() {
		long val = readLong(payload, offset);
		offset += 8;
		return val;
	}

	public long readLong(Enum<?> bit) {
		return readLong(bit, 0);
	}

	public long readLong(Enum<?> bit, long defaultValue) {
		return bitField.get(bit) ? readLong() : defaultValue;
	}

	public float readFloat() {
		float val = readFloat(payload, offset);
		offset += 4;
		return val;
	}

	/*
	public float readFloat(Enum<?> bit) {
		return readFloat(bit, Float.MIN_VALUE);
	}
	*/

	public float readFloat(Enum<?> bit, float defaultValue) {
		return bitField.get(bit) ? readFloat() : defaultValue;
	}

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

	public String readString(Enum<?> bit) {
		return bitField.get(bit) ? readString() : null;
	}

	public byte[] readBytes(int byteCount) {
		byte[] bytes = Arrays.copyOfRange(payload, offset, offset + byteCount);
		offset += byteCount;
		return bytes;
	}

	public byte[] readBytes(Enum<?> bit, int byteCount) {
		return bitField.get(bit) ? readBytes(byteCount) : null;
	}

	public void readUnknown(String name, int byteCount) {
		if (unknownFields != null) {
			unknownFields.put(name, readBytes(byteCount));
		}
	}

	public void readObjectUnknown(String name, int byteCount) {
		if (unknownObjectFields != null) {
			unknownObjectFields.put(name, readBytes(byteCount));
		}
	}

	public void readObjectUnknown(Enum<?> bit, int byteCount) {
		if (unknownObjectFields != null && bitField.get(bit)) {
			unknownObjectFields.put(bit.name(), readBytes(byteCount));
		}
	}

	public void skip(int byteCount) {
		offset += byteCount;
	}

	public SortedMap<String, byte[]> getUnknownFields() {
		return unknownFields;
	}

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

		if (unknownObjectFields != null) {
			unknownObjectFields.clear();
		}
	}

	public boolean has(Enum<?> bit) {
		return bitField.get(bit);
	}

	public ObjectType getObjectType() {
		return objectType;
	}

	public int getObjectId() {
		return objectId;
	}

	public SortedMap<String, byte[]> getUnknownObjectFields() {
		return unknownObjectFields;
	}

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


	public static BoolState readBool(byte[] bytes, int offset) {
		return BoolState.from(bytes[offset] == 1);
	}

	public static int readShort(byte[] bytes, int offset) {
		return (0xff & (bytes[offset + 1] << 8)) | (0xff & bytes[offset]);
	}

	public static int readInt(byte[] bytes, int offset) {
		return	((0xff & bytes[offset + 3]) << 24) |
				((0xff & bytes[offset + 2]) << 16) |
				((0xff & bytes[offset + 1]) << 8) |
				(0xff & bytes[offset]);
	}

	public static long readLong(byte[] bytes, int offset) {
		return	(((long) (0xff & bytes[offset + 7])) << 56) |
				(((long) (0xff & bytes[offset + 6])) << 48) |
				(((long) (0xff & bytes[offset + 5])) << 40) |
				(((long) (0xff & bytes[offset + 4])) << 32) |
				((0xff & bytes[offset + 3]) << 24) |
				((0xff & bytes[offset + 2]) << 16) |
				((0xff & bytes[offset + 1]) << 8) |
				(0xff & bytes[offset]);
	}

	public static float readFloat(byte[] bytes, int offset) {
		return Float.intBitsToFloat(readInt(bytes, offset));
	}
}