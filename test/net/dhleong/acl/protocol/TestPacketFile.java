package net.dhleong.acl.protocol;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.Listener;
import net.dhleong.acl.iface.ListenerRegistry;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.util.TextUtil;

public class TestPacketFile {
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private byte[] bytes;

	public TestPacketFile(String resourcePath) throws IOException {
		InputStream is = TestPacketFile.class.getResourceAsStream(resourcePath);
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
}