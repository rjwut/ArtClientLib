package net.dhleong.acl.protocol.core.setup;

import net.dhleong.acl.enums.Console;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.UnexpectedTypeException;
import net.dhleong.acl.protocol.core.ShipActionPacket;

/**
 * "Take" or "untake" a bridge console.
 * @author dhleong
 */
public class SetConsolePacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SetConsolePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SetConsolePacket(reader);
			}
		});
	}

	private Console mConsole;
	private boolean mSelected;

	/**
	 * @param console The Console being updated
	 * @param selected Whether the player is taking this console or not
	 */
	public SetConsolePacket(Console console, boolean selected) {
        super(TYPE_SET_CONSOLE);

        if (console == null) {
        	throw new IllegalArgumentException("You must specify a console");
        }

        mConsole = console;
        mSelected = selected;
    }

	private SetConsolePacket(PacketReader reader) {
        super(TYPE_SET_CONSOLE);
		int subtype = reader.readInt();

		if (subtype != TYPE_SET_CONSOLE) {
        	throw new UnexpectedTypeException(subtype, TYPE_SET_CONSOLE);
		}

		mConsole = Console.values()[reader.readInt()];
		mSelected = reader.readInt() == 1;
	}

	@Override
    public void writePayload(PacketWriter writer) {
    	writer	.writeInt(TYPE_SET_CONSOLE)
    			.writeInt(mConsole.ordinal())
    			.writeInt(mSelected ? 1 : 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mConsole).append(' ').append(mSelected ? "selected" : "deselected");
	}
}