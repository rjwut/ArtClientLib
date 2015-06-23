package net.dhleong.acl.protocol.core.eng;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class EngSetAutoDamconPacketTest extends AbstractPacketTester<EngSetAutoDamconPacket> {
	@Test
	public void test() {
		execute("core/eng/EngSetAutoDamconPacket.txt", ConnectionType.CLIENT, 2);
	}

	@Override
	protected void testPackets(List<EngSetAutoDamconPacket> packets) {
		EngSetAutoDamconPacket pkt = packets.get(0);
		Assert.assertFalse(pkt.isAutonomous());
		pkt = packets.get(1);
		Assert.assertTrue(pkt.isAutonomous());
	}
}