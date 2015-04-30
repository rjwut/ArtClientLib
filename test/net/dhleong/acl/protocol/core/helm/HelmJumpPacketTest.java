package net.dhleong.acl.protocol.core.helm;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class HelmJumpPacketTest extends AbstractPacketTester<HelmJumpPacket> {
	@Test
	public void test() {
		execute("core/helm/HelmJumpPacket.txt", ConnectionType.CLIENT, 1);
	}

	@Override
	protected void testPackets(List<HelmJumpPacket> packets) {
		HelmJumpPacket pkt = packets.get(0);
		Assert.assertEquals(0.2f, pkt.getHeading(), EPSILON);
		Assert.assertEquals(0.7f, pkt.getDistance(), EPSILON);
	}
}
