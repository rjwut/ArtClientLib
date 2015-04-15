package net.dhleong.acl.protocol;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.BaseDebugger;
import net.dhleong.acl.iface.Listener;
import net.dhleong.acl.iface.ListenerRegistry;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.util.TextUtil;

/**
 * Class that can slurp in a file containing packet data and store it as a byte
 * array, then perform various operations useful for testing.
 *
 * The test files don't contain the raw binary data. Instead, they contain hex-
 * encoded bytes for easier reading by humans. You can add white space for
 * greater legibility. You can also add comments with double slashes (//); the
 * slashes and all characters after them until the next newline character will
 * be ignored.
 * 
 * Example:
 * // ToggleRedAlertPacket
 * efbeadde          // header
 * 20000000          // packet length = 32
 * 02000000          // origin = client
 * 00000000          // padding
 * 0c000000          // remaining length = 12
 * 3c1d824c 0a000000 // ToggleRedAlertPacket
 * 00000000          // unused
 *
 * @author rjwut
 */
public class TestPacketFile {
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Reads a file name and a ConnectionType (SERVER or CLIENT) from the
	 * command line, then reads in the file, parses it, re-writes the packets,
	 * and confirms that the resulting output bytes exactly match the bytes that
	 * were originally read.
	 */
	public static void main(String[] args) {
		String fileName = args[0];
		ConnectionType connType = ConnectionType.valueOf(args[1]);

		try {
			new TestPacketFile(new File(fileName)).test(connType);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private byte[] bytes;

	/**
	 * Reads test packet data from the given File.
	 */
	public TestPacketFile(File file) throws IOException {
		this(new FileInputStream(file));
	}

	/**
	 * Reads test packet data from the given URL.
	 */
	public TestPacketFile(URL url) throws IOException {
		this(url.openStream());
	}

	/**
	 * Reads test packet data from the given InputStream.
	 */
	public TestPacketFile(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String line;

		while ((line = reader.readLine()) != null) {
			// strip out comments and whitespace
			int commentIndex = line.indexOf("//");

			if (commentIndex != -1) {
				line = line.substring(0, commentIndex);
			}

			line = line.replaceAll("\\s+", "");

			if (line.isEmpty()) {
				continue;	// no actual data on this line
			}

			baos.write(TextUtil.hexToByteArray(line));
		}

		reader.close();
		bytes = baos.toByteArray();
	}

	/**
	 * Writes an int to the given byte array at the indicated offset.
	 */
	private static int writeInt(int v, byte[] bytes, int offset) {
		bytes[offset++] = (byte) (v & 0xff);
		bytes[offset++] = (byte) ((v >> 8) & 0xff);
		bytes[offset++] = (byte) ((v >> 16) & 0xff);
		bytes[offset++] = (byte) ((v >> 24) & 0xff);
		return offset;
	}

	/**
	 * Given a ConnectionType from which the data in this file originated,
	 * attempts to parse and re-write the data. If an error occurs, a stack
	 * trace will be written out to System.err.
	 */
	public void test(ConnectionType connType) {
		// Set up PacketReader
		ListenerRegistry listeners = new ListenerRegistry();
		listeners.register(this);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		PacketReader reader = new PacketReader(
				connType,
				bais,
				new PacketFactoryRegistry(),
				listeners
		);
		PacketTestDebugger debugger = new PacketTestDebugger();

		// Read until there's no more data
		while (bais.available() > 0) {
			ArtemisPacket pkt;

			try {
				// Parse a packet, then get the bytes from the Debugger
				pkt = reader.readPacket(debugger);
				System.out.println(pkt.getClass().getSimpleName());
				byte[] in = debugger.in;

				// Write the bytes out to a ByteArrayOutputStream
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PacketWriter writer = new PacketWriter(baos);
				pkt.writeTo(writer, debugger);
				byte[] out = baos.toByteArray();

				// Compare against original bytes
				if (!diff(pkt, in, out)) {
					break; // diff already wrote out an error
				}
			} catch (ArtemisPacketException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Returns true if the given byte arrays match. Otherwise, it prints out a
	 * description of the packet and the input and output byte arrays and
	 * returns false.
	 */
	private static boolean diff(ArtemisPacket pkt, byte[] in, byte[] out) {
		boolean ok = in.length == out.length;

		for (int i = 0; ok && i < in.length; i++) {
			ok = in[i] == out[i];
		}

		if (!ok) {
			System.out.println(pkt);
			System.out.println("   IN: " + TextUtil.byteArrayToHexString(in));
			System.out.println("  OUT: " + TextUtil.byteArrayToHexString(out));
		}

		return ok;
	}

	/**
	 * Returns a PacketReader of the given type that will consume the bytes from
	 * this file.
	 */
	public PacketReader toPacketReader(ConnectionType type) {
		ListenerRegistry listeners = new ListenerRegistry();
		listeners.register(this);
		return new PacketReader(
				type,
				new ByteArrayInputStream(bytes),
				new PacketFactoryRegistry(),
				listeners
		);
	}

	/**
	 * Do nothing; this simply forces all packets to be read; the actual test
	 * work is handled by the PacketTestDebugger class.
	 */
	@Listener
	public void onPacket(ArtemisPacket pkt) {
		// do nothing
	}

	/**
	 * Returns true if the bytes in the given ByteArrayOutputStream match those
	 * from this file; false otherwise.
	 */
	public boolean matches(ByteArrayOutputStream baos) {
		byte[] bytes2 = baos.toByteArray();

		if (bytes.length != bytes2.length) {
			return false;
		}

		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != bytes2[i]) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return TextUtil.byteArrayToHexString(bytes);
	}


	/**
	 * Used by TestPacketFile to capture the raw bytes from the file. This is
	 * used by test() method. It also ensures that any parse warnings get output
	 * to the console.
	 */
	private class PacketTestDebugger extends BaseDebugger {
		private byte[] in;

		@Override
		public void onRecvPacketBytes(ConnectionType connType, int pktType,
				byte[] payload) {
			int packetLength = payload.length + 24;
			in = new byte[packetLength];
			int offset = 0;
			offset = writeInt(ArtemisPacket.HEADER, in, offset);
			offset = writeInt(packetLength, in, offset);
			offset = writeInt(connType.toInt(), in, offset);
			offset = writeInt(0, in, offset);
			offset = writeInt(packetLength - 20, in, offset);
			offset = writeInt(pktType, in, offset);
			System.arraycopy(payload, 0, in, 24, payload.length);
		}

		@Override
		public void warn(String msg) {
			System.out.println(msg);
		}
	}
}