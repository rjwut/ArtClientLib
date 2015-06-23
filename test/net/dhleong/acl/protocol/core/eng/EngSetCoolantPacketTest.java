package net.dhleong.acl.protocol.core.eng;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class EngSetCoolantPacketTest extends AbstractPacketTester<EngSetCoolantPacket> {
	@Test
	public void test() {
		execute("core/eng/EngSetCoolantPacket.txt", ConnectionType.CLIENT, 2);
	}

	@Override
	protected void testPackets(List<EngSetCoolantPacket> packets) {
		EngSetCoolantPacket pkt = packets.get(0);
		Assert.assertEquals(ShipSystem.AFT_SHIELDS, pkt.getSystem());
		Assert.assertEquals(3, pkt.getCoolant());
		pkt = packets.get(1);
		Assert.assertEquals(ShipSystem.BEAMS, pkt.getSystem());
		Assert.assertEquals(0, pkt.getCoolant());
	}
}