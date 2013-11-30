package net.dhleong.acl.util;

import java.util.Arrays;

import net.dhleong.acl.net.EnemyUpdatePacket;

/**
 * Provides easy reading and writing of bits in a bit field. The bit places are
 * identified by an enum. The bytes are little-endian, so in the event that the
 * final byte is not completely utilized, it will be the most significant bits
 * that are left unused.
 * @author rjwut
 */
public class BitField {
	public static void main(String[] args) {
		BitField bitField = new BitField(
				EnemyUpdatePacket.Bit.values(),
				TextUtil.hexToByteArray(args[0]),
				0
		);
		System.out.println(bitField.listActiveBits(EnemyUpdatePacket.Bit.values()));
	}

	private byte[] bytes;

	/**
	 * Creates a BitField large enough to accomodate the enumerated bits. All
	 * bits start at 0.
	 */
	public BitField(Enum<?>[] bits) {
		int byteCount = (bits.length + 7) / 8;
		this.bytes = new byte[byteCount];
	}

	/**
	 * Creates a BitField large enough to accomodate the enumerated bits, and
	 * stores the indicated bytes in it.
	 */
	public BitField(Enum<?>[] bits, byte[] bytes, int offset) {
		int byteCount = (bits.length + 7) / 8;
		this.bytes = Arrays.copyOfRange(bytes, offset, offset + byteCount);
	}

	/**
	 * Returns the number of bytes in this BitField.
	 */
	public int getByteCount() {
		return bytes.length;
	}

	/**
	 * Returns true if the indicated bit is 1, false if it's 0.
	 */
	public boolean get(Enum<?> bit) {
		int bitIndex = bit.ordinal();
		int byteIndex =  bitIndex / 8;
		int mask = 0x1 << (bitIndex % 8);
		return (bytes[byteIndex] & mask) != 0;
	}

	/**
	 * If value is true, the indicated bit is set to 1; otherwise, it's set to
	 * 0.
	 */
	public void set(Enum<?> bit, boolean value) {
		int ordinal = bit.ordinal();
		int byteIndex = ordinal / 8;
		int bitIndex = ordinal % 8;
		int mask = (0x1 << bitIndex) ^ 0xff;
		int shiftedValue = (value ? 1 : 0) << bitIndex;
		bytes[byteIndex] = (byte) ((bytes[byteIndex] & mask) | shiftedValue);
	}

	/**
	 * Returns a hex encoded String of the bytes composing this BitField.
	 */
	@Override
	public String toString() {
		return TextUtil.byteArrayToHexString(bytes);
	}

	/**
	 * Returns an array of the bytes composing this BitField.
	 */
	public byte[] toByteArray() {
		return Arrays.copyOf(bytes, bytes.length);
	}

	/**
	 * Returns a space-delimited list of the names of the enum values that
	 * correspond to active bits in this BitField. This can be useful for
	 * debugging purposes.
	 */
	public String listActiveBits(Enum<?>[] bits) {
		StringBuilder list = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];

			for (int j = 0; j < 8; j++) {
				int bitIndex = i * 8 + j;

				if (bitIndex < bits.length) {
					if ((b & (0x01 << j)) != 0) {
						if (list.length() != 0) {
							list.append(' ');
						}

						list.append(bits[bitIndex].name());
					}
				}
			}
		}

		return list.toString();
	}
}