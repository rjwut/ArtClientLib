package net.dhleong.acl.net.protocol;

import net.dhleong.acl.net.BeamFiredPacket;
import net.dhleong.acl.net.DestroyObjectPacket;
import net.dhleong.acl.net.DroneUpdatePacket;
import net.dhleong.acl.net.GameMessagePacket;
import net.dhleong.acl.net.GameOverPacket;
import net.dhleong.acl.net.GameStartPacket;
import net.dhleong.acl.net.GenericMeshPacket;
import net.dhleong.acl.net.GenericUpdatePacket;
import net.dhleong.acl.net.IntelPacket;
import net.dhleong.acl.net.KeyCaptureTogglePacket;
import net.dhleong.acl.net.NpcUpdatePacket;
import net.dhleong.acl.net.SoundEffectPacket;
import net.dhleong.acl.net.StationPacket;
import net.dhleong.acl.net.WhaleUpdatePacket;
import net.dhleong.acl.net.comms.CommsIncomingPacket;
import net.dhleong.acl.net.comms.IncomingAudioPacket;
import net.dhleong.acl.net.eng.EngGridUpdatePacket;
import net.dhleong.acl.net.helm.JumpStatusPacket;
import net.dhleong.acl.net.player.EngPlayerUpdatePacket;
import net.dhleong.acl.net.player.MainPlayerUpdatePacket;
import net.dhleong.acl.net.player.WeapPlayerUpdatePacket;
import net.dhleong.acl.net.setup.AllShipSettingsPacket;
import net.dhleong.acl.net.setup.StationStatusPacket;
import net.dhleong.acl.net.setup.VersionPacket;
import net.dhleong.acl.net.setup.WelcomePacket;

/**
 * Implements the core Artemis protocol.
 * @author rjwut
 */
public class CoreArtemisProtocol implements Protocol {
	@Override
	public void registerPacketFactories(PacketFactoryRegistry registry) {
		NpcUpdatePacket.register(registry);
		WhaleUpdatePacket.register(registry);
		MainPlayerUpdatePacket.register(registry);
		BeamFiredPacket.register(registry);
		EngGridUpdatePacket.register(registry);
		EngPlayerUpdatePacket.register(registry);
		WeapPlayerUpdatePacket.register(registry);
		IntelPacket.register(registry);
		DroneUpdatePacket.register(registry);
		GenericUpdatePacket.register(registry);
		SoundEffectPacket.register(registry);
		StationPacket.register(registry);

		AllShipSettingsPacket.register(registry);
		CommsIncomingPacket.register(registry);
		DestroyObjectPacket.register(registry);
		GameMessagePacket.register(registry);
		GameOverPacket.register(registry);
		GameStartPacket.register(registry);
		GenericMeshPacket.register(registry);
		IncomingAudioPacket.register(registry);
		JumpStatusPacket.register(registry);
		KeyCaptureTogglePacket.register(registry);
		StationStatusPacket.register(registry);
		VersionPacket.register(registry);
		WelcomePacket.register(registry);
	}
}