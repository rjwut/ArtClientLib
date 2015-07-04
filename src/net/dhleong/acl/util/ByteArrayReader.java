package net.dhleong.acl.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Handles reading various data types from a byte array and tracking the offset
 * pointer.
 */
public class ByteArrayReader {
	/**
	 * Reads the indicated number of bytes from the given InputStream wrapped by this object and
	 * stores them in the provided buffer. This method blocks until the desired number of bytes has
	 * been read or the stream closes.
	 */
	public static void readBytes(InputStream in, int byteCount, byte[] buffer) throws InterruptedException, IOException {
		if (byteCount > buffer.length) {
			throw new IllegalArgumentException("Requested " + byteCount +
					" byte(s) but buffer is only " + buffer.length + " byte(s)");
		}

		int totalBytesRead = 0;

		while (true) {
			int bytesRead = in.read(buffer, totalBytesRead, byteCount - totalBytesRead);

			if (bytesRead == -1) {
				throw new EOFException("Stream is closed");
			}

			totalBytesRead += bytesRead;

			if (totalBytesRead < byteCount) {
				Thread.sleep(1);
			} else {
				break;
			}
		}
	}

	/**
	 * Reads an int from the indicated location in the given byte array.
	 */
	public static int readInt(byte[] bytes, int offset) {
		return	((0xff & bytes[offset + 3]) << 24) |
				((0xff & bytes[offset + 2]) << 16) |
				((0xff & bytes[offset + 1]) << 8) |
				(0xff & bytes[offset]);
	}

	private byte[] bytes;
	private int offset;

	public ByteArrayReader(byte[] bytes) {
		this.bytes = bytes;
	}

	public int getBytesLeft() {
		return bytes.length - offset;
	}

	public byte peek() {
		return bytes[offset];
	}

	public void skip(int byteCount) {
		offset += byteCount;
	}

	public byte readByte() {
		return bytes[offset++];
	}

	public byte[] readBytes(int byteCount) {
		byte[] readBytes = Arrays.copyOfRange(bytes, offset, offset + byteCount);
		offset += byteCount;
		return readBytes;
	}

	public boolean readBoolean(int byteCount) {
		return readBytes(byteCount)[0] == 1;
	}

	public BoolState readBoolState(int byteCount) {
		return BoolState.from(readBoolean(byteCount));
	}

	public int readShort() {
		int value = (0xff & (bytes[offset + 1] << 8)) | (0xff & bytes[offset]);
		offset += 2;
		return value;
	}

	public int readInt() {
		int value =	readInt(bytes, offset);
		offset += 4;
		return value;
	}

	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	public BitField readBitField(Enum<?>[] bits) {
		return new BitField(bits, bytes, offset);
	}

	public String readUSASCIIString() {
		return readString(Util.US_ASCII, 1);
	}

	public String readUTF16LEString() {
		return readString(Util.UTF16LE, 2);
	}

	private String readString(Charset charset, int bytesPerChar) {
		int charCount = readInt();
		int byteCount = charCount * bytesPerChar;
		byte[] readBytes = Arrays.copyOfRange(bytes, offset, offset + byteCount - bytesPerChar);
		offset += byteCount;
		int i = 0;

		// check for "early" null
		for ( ; i < readBytes.length; i += bytesPerChar) {
			boolean isNull = true;

			for (int j = 0; isNull && j < bytesPerChar; j++) {
				isNull = readBytes[i + j] == 0;
			}

			if (isNull) {
				break;
			}
		}

		if (i != readBytes.length) {
			readBytes = Arrays.copyOfRange(readBytes, 0, i);
		}

		return new String(readBytes, charset);
	}
}