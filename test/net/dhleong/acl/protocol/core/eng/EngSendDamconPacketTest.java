package net.dhleong.acl.protocol.core.eng;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class EngSendDamconPacketTest extends AbstractPacketTester<EngSendDamconPacket> {
	@Test
	public void test() {
		execute("core/eng/EngSendDamconPacket.txt", ConnectionType.CLIENT, 2);
	}

	@Override
	protected void testPackets(List<EngSendDamconPacket> packets) {
		EngSendDamconPacket pkt = packets.get(0);
		Assert.assertEquals(0, pkt.getTeamNumber());
		Assert.assertEquals(0, pkt.getX());
		Assert.assertEquals(0, pkt.getY());
		Assert.assertEquals(0, pkt.getZ());
		pkt = packets.get(1);
		Assert.assertEquals(1, pkt.getTeamNumber());
		Assert.assertEquals(2, pkt.getX());
		Assert.assertEquals(3, pkt.getY());
		Assert.assertEquals(4, pkt.getZ());
	}
}