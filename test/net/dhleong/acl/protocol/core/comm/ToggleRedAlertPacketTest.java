package net.dhleong.acl.protocol.core.comm;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class ToggleRedAlertPacketTest extends AbstractPacketTester<ToggleRedAlertPacket> {
	@Test
	public void test() {
		execute("core/comm/ToggleRedAlertPacket.txt", ConnectionType.CLIENT, 1);
	}

	@Override
	protected void testPackets(List<ToggleRedAlertPacket> packets) {
		Assert.assertNotNull(packets.get(0));
	}
}