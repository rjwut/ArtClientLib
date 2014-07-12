package net.dhleong.acl.protocol.core;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;

/**
 * Sends a keystroke to the server. This should only be done for the game master
 * console, or if keystroke capture has been enabled via the
 * KeyCaptureTogglePacket.
 * @author rjwut
 */
public class KeystrokePacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, TYPE_REVERSE_ENGINES,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return KeystrokePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new KeystrokePacket(reader);
			}
		});
	}

	/**
	 * @param keycode the key that was pressed
	 * @see {@link java.awt.event.KeyEvent} (for constants)
	 */
	public KeystrokePacket(int keycode) {
		super(TYPE_KEYSTROKE, keycode);
	}

	private KeystrokePacket(PacketReader reader) {
		super(TYPE_KEYSTROKE, reader);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg);
	}
}