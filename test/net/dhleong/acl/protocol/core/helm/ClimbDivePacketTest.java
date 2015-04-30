package net.dhleong.acl.protocol.core.helm;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class ClimbDivePacketTest extends AbstractPacketTester<ClimbDivePacket> {
	@Test
	public void test() {
		execute("core/helm/ClimbDivePacket.txt", ConnectionType.CLIENT, 2);
	}

	@Override
	protected void testPackets(List<ClimbDivePacket> packets) {
		Assert.assertTrue(packets.get(0).isUp());
		Assert.assertFalse(packets.get(1).isUp());
	}
}