package net.dhleong.acl.protocol.core.setup;

import java.nio.charset.Charset;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;

/**
 * Sent by the server immediately on connection. The receipt of this packet
 * indicates a successful connection to the server.
 * @author rjwut
 */
public class WelcomePacket extends BaseArtemisPacket {
	private static final int TYPE = 0x6d04b3da;
	private static final byte[] MSG = "You have connected to Thom Robertson's Artemis Bridge Simulator.  Please connect with an authorized game client.".getBytes(Charset.forName("US-ASCII"));

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return WelcomePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new WelcomePacket(reader);
			}
		});
	}

	private WelcomePacket(PacketReader reader) {
		super(ConnectionType.SERVER, TYPE);
		int length = reader.readInt();
		reader.skip(length);
	}

	public WelcomePacket() {
		super(ConnectionType.SERVER, TYPE);
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG.length).writeBytes(MSG);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}