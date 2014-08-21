package net.dhleong.acl.protocol.core.eng;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class EngSetEnergyPacketTest extends AbstractPacketTester<EngSetEnergyPacket> {
	private static final float DEFAULT_ENERGY_ALLOCATION = 1.0f / 3;

	@Test
	public void test() {
		execute("core/eng/EngSetEnergyPacket.txt", ConnectionType.CLIENT, 3);
	}

	@Override
	protected void testPackets(List<EngSetEnergyPacket> packets) {
		EngSetEnergyPacket pkt = packets.get(0);
		Assert.assertEquals(pkt.getSystem(), ShipSystem.BEAMS);
		Assert.assertEquals(DEFAULT_ENERGY_ALLOCATION, pkt.getAllocation(), EPSILON);
		pkt = packets.get(1);
		Assert.assertEquals(pkt.getSystem(), ShipSystem.AFT_SHIELDS);
		Assert.assertEquals(0.0f, pkt.getAllocation(), EPSILON);
		pkt = packets.get(2);
		Assert.assertEquals(pkt.getSystem(), ShipSystem.WARP_JUMP_DRIVE);
		Assert.assertEquals(1.0f, pkt.getAllocation(), EPSILON);
	}
}