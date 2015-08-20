package net.dhleong.acl.protocol.core.comm;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.AudioCommand;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class AudioCommandPacketTest extends AbstractPacketTester<AudioCommandPacket> {
	@Test
	public void test() {
		execute("core/comm/AudioCommandPacket.txt", ConnectionType.CLIENT, 2);
	}

	@Override
	protected void testPackets(List<AudioCommandPacket> packets) {
		AudioCommandPacket pkt = packets.get(0);
		Assert.assertEquals(0, pkt.getAudioId());
		Assert.assertEquals(AudioCommand.PLAY, pkt.getCommand());
		pkt = packets.get(1);
		Assert.assertEquals(1, pkt.getAudioId());
		Assert.assertEquals(AudioCommand.DISMISS, pkt.getCommand());
	}
}