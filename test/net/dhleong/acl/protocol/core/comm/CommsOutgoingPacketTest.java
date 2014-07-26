package net.dhleong.acl.protocol.core.comm;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.dhleong.acl.enums.BaseMessage;
import net.dhleong.acl.enums.CommsRecipientType;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.EnemyMessage;
import net.dhleong.acl.enums.OtherMessage;
import net.dhleong.acl.enums.PlayerMessage;
import net.dhleong.acl.protocol.core.setup.AbstractPacketTester;

public class CommsOutgoingPacketTest extends AbstractPacketTester<CommsOutgoingPacket> {
	@Test
	public void test() {
		execute("core/comm/CommsOutgoingPacket.txt", ConnectionType.CLIENT, 4);
	}

	@Override
	protected void testPackets(List<CommsOutgoingPacket> packets) {
		CommsOutgoingPacket pkt = packets.get(0);
		Assert.assertEquals(CommsRecipientType.PLAYER, pkt.getRecipientType());
		Assert.assertEquals(0, pkt.getRecipientId());
		Assert.assertEquals(PlayerMessage.DIE, pkt.getMessage());
		Assert.assertEquals(CommsOutgoingPacket.NO_ARG, pkt.getArgument());

		pkt = packets.get(1);
		Assert.assertEquals(CommsRecipientType.ENEMY, pkt.getRecipientType());
		Assert.assertEquals(1, pkt.getRecipientId());
		Assert.assertEquals(EnemyMessage.WILL_YOU_SURRENDER, pkt.getMessage());
		Assert.assertEquals(CommsOutgoingPacket.NO_ARG, pkt.getArgument());

		pkt = packets.get(2);
		Assert.assertEquals(CommsRecipientType.BASE, pkt.getRecipientType());
		Assert.assertEquals(2, pkt.getRecipientId());
		Assert.assertEquals(BaseMessage.BUILD_NUKES, pkt.getMessage());
		Assert.assertEquals(CommsOutgoingPacket.NO_ARG, pkt.getArgument());

		pkt = packets.get(3);
		Assert.assertEquals(CommsRecipientType.OTHER, pkt.getRecipientType());
		Assert.assertEquals(3, pkt.getRecipientId());
		Assert.assertEquals(OtherMessage.GO_DEFEND, pkt.getMessage());
		Assert.assertEquals(47, pkt.getArgument());
	}
}