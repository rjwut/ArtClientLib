package net.dhleong.acl.protocol.core.eng;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class EngAutoDamconUpdatePacketTest extends AbstractPacketTester<EngAutoDamconUpdatePacket> {
	@Test
	public void test() {
		execute("core/eng/EngAutoDamconUpdatePacket.txt", ConnectionType.SERVER, 2);
	}

	@Override
	protected void testPackets(List<EngAutoDamconUpdatePacket> packets) {
		EngAutoDamconUpdatePacket pkt = packets.get(0);
		Assert.assertFalse(pkt.isOn());
		pkt = packets.get(1);
		Assert.assertTrue(pkt.isOn());
	}
}
