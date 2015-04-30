package net.dhleong.acl.protocol.core.helm;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class HelmSetClimbDivePacketTest extends AbstractPacketTester<HelmSetClimbDivePacket> {
	@Test
	public void test() {
		execute("core/helm/HelmSetClimbDivePacket.txt", ConnectionType.CLIENT, 3);
	}

	@Override
	protected void testPackets(List<HelmSetClimbDivePacket> packets) {
		HelmSetClimbDivePacket pkt = packets.get(0);
		Assert.assertEquals(0.0f, pkt.getPitch(), EPSILON);
		pkt = packets.get(1);
		Assert.assertEquals(-1.0f, pkt.getPitch(), EPSILON);
		pkt = packets.get(2);
		Assert.assertEquals(0.3f, pkt.getPitch(), EPSILON);
	}
}