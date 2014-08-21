package net.dhleong.acl.protocol.core.setup;

import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.Version;

import org.junit.Test;

public class VersionPacketTest extends AbstractPacketTester<VersionPacket> {
	private static final Version VERSION_2_0 = new Version(2.0f);
	private static final Version VERSION_2_1_1 = new Version("2.1.1");

	@Test
	public void test() {
		execute("core/setup/VersionPacket.txt", ConnectionType.SERVER, 2);
	}

	@Override
	protected void testPackets(List<VersionPacket> packets) {
		VERSION_2_0.equals(packets.get(0));
		VERSION_2_1_1.equals(packets.get(1));
	}
}