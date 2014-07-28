package net.dhleong.acl.protocol.core.comm;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class IncomingAudioPacketTest extends AbstractPacketTester<IncomingAudioPacket> {
	@Test
	public void test() {
		execute("core/comm/IncomingAudioPacket.txt", ConnectionType.SERVER, 2);
	}

	@Override
	protected void testPackets(List<IncomingAudioPacket> packets) {
		IncomingAudioPacket pkt = packets.get(0);
		Assert.assertEquals(0, pkt.getAudioId());
		Assert.assertEquals(IncomingAudioPacket.Mode.INCOMING, pkt.getAudioMode());
		Assert.assertEquals("Hello", pkt.getTitle());
		Assert.assertEquals("hello.ogg", pkt.getFileName());
		pkt = packets.get(1);
		Assert.assertEquals(1, pkt.getAudioId());
		Assert.assertEquals(IncomingAudioPacket.Mode.PLAYING, pkt.getAudioMode());
		Assert.assertNull(pkt.getTitle());
		Assert.assertNull(pkt.getFileName());
	}
}