package net.dhleong.acl.protocol.core.eng;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;
import net.dhleong.acl.util.GridCoord;

public class EngSendDamconPacketTest extends AbstractPacketTester<EngSendDamconPacket> {
	@Test
	public void test() {
		execute("core/eng/EngSendDamconPacket.txt", ConnectionType.CLIENT, 2);
	}

	@Override
	protected void testPackets(List<EngSendDamconPacket> packets) {
		EngSendDamconPacket pkt = packets.get(0);
		Assert.assertEquals(0, pkt.getTeamNumber());
		GridCoord coord = pkt.getDestination();
		Assert.assertEquals(0, coord.getX());
		Assert.assertEquals(0, coord.getY());
		Assert.assertEquals(0, coord.getZ());
		pkt = packets.get(1);
		Assert.assertEquals(1, pkt.getTeamNumber());
		coord = pkt.getDestination();
		Assert.assertEquals(2, coord.getX());
		Assert.assertEquals(3, coord.getY());
		Assert.assertEquals(4, coord.getZ());
	}
}