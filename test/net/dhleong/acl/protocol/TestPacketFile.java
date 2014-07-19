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

public class TestPacketFile {
	private static final Charset UTF_8 = Charset.forName("UTF-8");

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

	public TestPacketFile(File file) throws IOException {
		this(new FileInputStream(file));
	}

	public TestPacketFile(URL url) throws IOException {
		this(url.openStream());
	}

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

	private static int writeInt(int v, byte[] bytes, int offset) {
		bytes[offset++] = (byte) (v & 0xff);
		bytes[offset++] = (byte) ((v >> 8) & 0xff);
		bytes[offset++] = (byte) ((v >> 16) & 0xff);
		bytes[offset++] = (byte) ((v >> 24) & 0xff);
		return offset;
	}

	public void test(ConnectionType connType) {
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

		while (bais.available() > 0) {
			ArtemisPacket pkt;

			try {
				pkt = reader.readPacket(debugger);
				System.out.println(pkt.getClass().getSimpleName());
				byte[] in = debugger.in;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PacketWriter writer = new PacketWriter(baos);
				pkt.writeTo(writer, debugger);
				byte[] out = baos.toByteArray();

				if (!diff(pkt, in, out)) {
					break;
				}
			} catch (ArtemisPacketException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

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

	@Listener
	public void onPacket(ArtemisPacket pkt) {
		// do nothing
	}

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