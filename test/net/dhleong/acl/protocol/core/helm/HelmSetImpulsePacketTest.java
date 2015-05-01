package net.dhleong.acl.protocol.core.helm;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class HelmSetImpulsePacketTest extends AbstractPacketTester<HelmSetImpulsePacket> {
	@Test
	public void test() {
		execute("core/helm/HelmSetImpulsePacket.txt", ConnectionType.CLIENT, 2);
	}

	@Override
	protected void testPackets(List<HelmSetImpulsePacket> packets) {
		HelmSetImpulsePacket pkt = packets.get(0);
		Assert.assertEquals(0.0f, pkt.getPower(), EPSILON);
		pkt = packets.get(1);
		Assert.assertEquals(0.7f, pkt.getPower(), EPSILON);
	}
}