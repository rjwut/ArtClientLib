package net.dhleong.acl.protocol.core;

import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.protocol.Protocol;
import net.dhleong.acl.protocol.core.comm.*;
import net.dhleong.acl.protocol.core.eng.*;
import net.dhleong.acl.protocol.core.helm.*;
import net.dhleong.acl.protocol.core.sci.*;
import net.dhleong.acl.protocol.core.setup.*;
import net.dhleong.acl.protocol.core.weap.*;
import net.dhleong.acl.protocol.core.world.*;

/**
 * Implements the core Artemis protocol.
 * @author rjwut
 */
public class CoreArtemisProtocol implements Protocol {
	@Override
	public void registerPacketFactories(PacketFactoryRegistry registry) {
		// server
		// --- prioritized
		NpcUpdatePacket.register(registry);
		MainPlayerUpdatePacket.register(registry);
		DroneUpdatePacket.register(registry);
		BeamFiredPacket.register(registry);
		GenericUpdatePacket.register(registry);
		BasePacket.register(registry);
		EngGridUpdatePacket.register(registry);
		EngPlayerUpdatePacket.register(registry);
		WeapPlayerUpdatePacket.register(registry);
		WhaleUpdatePacket.register(registry);
		IntelPacket.register(registry);
		SoundEffectPacket.register(registry);
		// --- rest
		AllShipSettingsPacket.register(registry);
		EngAutoDamconUpdatePacket.register(registry);
		CommsIncomingPacket.register(registry);
		ConsoleStatusPacket.register(registry);
		DmxMessagePacket.register(registry);
		DestroyObjectPacket.register(registry);
		DifficultyPacket.register(registry);
		GameMessagePacket.register(registry);
		GameOverPacket.register(registry);
		GameOverReasonPacket.register(registry);
		GameOverStatsPacket.register(registry);
		GameStartPacket.register(registry);
		GenericMeshPacket.register(registry);
		IncomingAudioPacket.register(registry);
		JumpStatusPacket.register(registry);
		KeyCaptureTogglePacket.register(registry);
		NebulaUpdatePacket.register(registry);
		PausePacket.register(registry);
		PerspectivePacket.register(registry);
		PlayerShipDamagePacket.register(registry);
		VersionPacket.register(registry);
		WelcomePacket.register(registry);

		// client
		// -- prioritized
		ToggleShieldsPacket.register(registry);
		FireBeamPacket.register(registry);
		FireTubePacket.register(registry);
		ToggleAutoBeamsPacket.register(registry);
		SetWeaponsTargetPacket.register(registry);
		LoadTubePacket.register(registry);
		HelmSetSteeringPacket.register(registry);
		HelmSetWarpPacket.register(registry);
		HelmJumpPacket.register(registry);
		EngSetCoolantPacket.register(registry);
		EngSetEnergyPacket.register(registry);
		HelmSetImpulsePacket.register(registry);
		HelmRequestDockPacket.register(registry);
		// --- rest
		AudioCommandPacket.register(registry);
		CaptainSelectPacket.register(registry);
		ClimbDivePacket.register(registry);
		CommsOutgoingPacket.register(registry);
		ConvertTorpedoPacket.register(registry);
		EngSendDamconPacket.register(registry);
		EngSetAutoDamconPacket.register(registry);
		GameMasterMessagePacket.register(registry);
		GameMasterSelectPacket.register(registry);
		HelmSetClimbDivePacket.register(registry);
		HelmToggleReversePacket.register(registry);
		KeystrokePacket.register(registry);
		ReadyPacket.register(registry);
		ReadyPacket2.register(registry);
		SciScanPacket.register(registry);
		SciSelectPacket.register(registry);
		SetBeamFreqPacket.register(registry);
		SetMainScreenPacket.register(registry);
		SetShipPacket.register(registry);
		SetShipSettingsPacket.register(registry);
		SetConsolePacket.register(registry);
		TogglePerspectivePacket.register(registry);
		ToggleRedAlertPacket.register(registry);
		UnloadTubePacket.register(registry);
	}
}