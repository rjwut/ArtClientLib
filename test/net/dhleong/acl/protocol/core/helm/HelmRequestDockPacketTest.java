package net.dhleong.acl.protocol.core.helm;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class HelmRequestDockPacketTest extends AbstractPacketTester<HelmRequestDockPacket> {
	@Test
	public void test() {
		execute("core/helm/HelmRequestDockPacket.txt", ConnectionType.CLIENT, 1);
	}

	@Override
	protected void testPackets(List<HelmRequestDockPacket> packets) {
		Assert.assertNotNull(packets.get(0));
	}
}