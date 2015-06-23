package net.dhleong.acl.protocol.core.helm;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class HelmSetSteeringPacketTest extends AbstractPacketTester<HelmSetSteeringPacket> {
	@Test
	public void test() {
		execute("core/helm/HelmSetSteeringPacket.txt", ConnectionType.CLIENT, 2);
	}

	@Override
	protected void testPackets(List<HelmSetSteeringPacket> packets) {
		HelmSetSteeringPacket pkt = packets.get(0);
		Assert.assertEquals(0.0f, pkt.getSteering(), EPSILON);
		pkt = packets.get(1);
		Assert.assertEquals(0.7f, pkt.getSteering(), EPSILON);
	}
}