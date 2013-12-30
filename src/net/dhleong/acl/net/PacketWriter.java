package net.dhleong.acl.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.util.BitField;

/**
 * Facilitates writing packets to an OutputStream. This object may be reused to
 * write as many packets as desired to a single OutputStream. To write a packet,
 * follow these steps:
 * 
 * 1. Invoke start().
 * 2. Write the payload data using the write*() methods. Payload data is
 *    buffered by the PacketWriter, not written immediately to the OutputStream.
 * 3. Invoke flush(). The proper values for the fields in the preamble will be
 *    automatically computed and written, followed by the payload. The entire
 *    packet is then flushed to the OutputStream.
 * 
 * Once flush() has been called, you can start writing another packet by
 * invoking start() again.
 * 
 * @author rjwut
 */
public class PacketWriter {
	private final OutputStream out;
	private int packetType;
	private ByteArrayOutputStream baos;
	private byte[] buffer = new byte[4];

	/**
	 * Creates a PacketWriter that writes packets to the given OutputStream.
	 */
	public PacketWriter(OutputStream out) {
		if (out == null) {
			throw new IllegalArgumentException(
					"The out argument cannot be null"
			);
		}

		this.out = out;
	}

	/**
	 * Starts a packet of the given type.
	 */
	public PacketWriter start(int pktType) {
		packetType = pktType;
		baos = new ByteArrayOutputStream();
		return this;
	}

	/**
	 * Writes a single byte. You must invoke start() before calling this method.
	 */
	public PacketWriter writeByte(byte v) {
		assertStarted();
		baos.write(v);
		return this;
	}

	/**
	 * Writes a short (two bytes). You must invoke start() before calling this
	 * method.
	 */
	public PacketWriter writeShort(int v) {
		assertStarted();
		buffer[0] = (byte) (v & 0xff);
		buffer[1] = (byte) ((v >> 8) & 0xff);
		baos.write(buffer, 0, 2);
		return this;
	}

	/**
	 * Writes an int (four bytes). You must invoke start() before calling this
	 * method.
	 */
	public PacketWriter writeInt(int v) {
		assertStarted();
		buffer[0] = (byte) (v & 0xff);
		buffer[1] = (byte) ((v >> 8) & 0xff);
		buffer[2] = (byte) ((v >> 16) & 0xff);
		buffer[3] = (byte) ((v >> 24) & 0xff);
		baos.write(buffer, 0, 4);
		return this;
	}

	/**
	 * Writes a long (eight bytes). You must invoke start() before calling this
	 * method.
	 */
	public PacketWriter writeLong(int v) {
		assertStarted();
		buffer[0] = (byte) (v & 0xff);
		buffer[1] = (byte) ((v >> 8) & 0xff);
		buffer[2] = (byte) ((v >> 16) & 0xff);
		buffer[3] = (byte) ((v >> 24) & 0xff);
		buffer[4] = (byte) ((v >> 32) & 0xff);
		buffer[5] = (byte) ((v >> 40) & 0xff);
		buffer[6] = (byte) ((v >> 48) & 0xff);
		buffer[7] = (byte) ((v >> 56) & 0xff);
		baos.write(buffer, 0, 8);
		return this;
	}

	/**
	 * Writes a float (four bytes). You must invoke start() before calling this
	 * method.
	 */
	public PacketWriter writeFloat(float v) {
		writeInt(Float.floatToRawIntBits(v));
		return this;
	}

	/**
	 * Writes a String. This handles writing the string length and the
	 * terminating null character automatically. You must invoke start() before
	 * calling this method.
	 */
	public PacketWriter writeString(String str) {
		int charCount = str.length() + 1;
		writeInt(charCount);

		try {
			baos.write(str.getBytes(ArtemisPacket.CHARSET));
		} catch (IOException ex) {
			// ByteArrayOutputStream doesn't actually throw IOException; it
			// just inherits the declaration from the OutputStream class. Since
			// it won't ever actually happen, we wrap it in a RuntimeException.
			throw new RuntimeException(ex);
		}

		writeShort(0);
		return this;
	}

	/**
	 * Writes a bit field. You must invoke start() before calling this method.
	 */
	public PacketWriter writeBitField(BitField bitField) {
		assertStarted();

		try {
			bitField.write(baos);
		} catch (IOException ex) {
			// ByteArrayOutputStream doesn't actually throw IOException; it
			// just inherits the declaration from the OutputStream class. Since
			// it won't ever actually happen, we wrap it in a RuntimeException.
			throw new RuntimeException(ex);
		}

		return this;
	}

	/**
	 * Writes a byte array. You must invoke start() before calling this method.
	 */
	public PacketWriter writeBytes(byte[] bytes) {
		assertStarted();

		try {
			baos.write(bytes);
		} catch (IOException ex) {
			// ByteArrayOutputStream doesn't actually throw IOException; it
			// just inherits the declaration from the OutputStream class. Since
			// it won't ever actually happen, we wrap it in a RuntimeException.
			throw new RuntimeException(ex);
		}

		return this;
	}

	/**
	 * Writes the completed packet to the OutputStream. You must invoke start()
	 * before calling this method. When this method returns, you will have to
	 * call start() again before you can write more data.
	 */
	public void flush() throws IOException {
		assertStarted();
		byte[] payload = baos.toByteArray();
		baos = null;
		writeIntToStream(ArtemisPacket.HEADER);				// header
		writeIntToStream(payload.length + 24);				// packet length
		writeIntToStream(ConnectionType.CLIENT.toInt());	// connection type
		writeIntToStream(0);								// padding
		writeIntToStream(payload.length + 4);				// remaining bytes
		writeIntToStream(packetType);						// packet type
		out.write(payload);									// payload
		out.flush();
	}

	/**
	 * Throws an IllegalStateException if start() has not been called since the
	 * time this object was constructed or since the last call to flush().
	 */
	private void assertStarted() {
		if (baos == null) {
			throw new IllegalStateException("Must invoke start() first");
		}
	}

	/**
	 * Writes an int value directly to the wrapped OutputStream.
	 */
	private void writeIntToStream(int value) throws IOException {
		buffer[0] = (byte) (0xff & value);
		buffer[1] = (byte) (0xff & (value >> 8));
		buffer[2] = (byte) (0xff & (value >> 16));
		buffer[3] = (byte) (0xff & (value >> 24));
		out.write(buffer);
	}
}