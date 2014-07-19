package net.dhleong.acl.protocol.core;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.MainScreenView;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;

/**
 * Set what to show on the MainScreen
 * @author dhleong
 */
public class SetMainScreenPacket extends ShipActionPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, TYPE_MAINSCREEN,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SetMainScreenPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SetMainScreenPacket(reader);
			}
		});
	}

	/**
	 * @param screen The enum value representing the desired view
	 */
    public SetMainScreenPacket(MainScreenView screen) {
        super(TYPE_MAINSCREEN, screen != null ? screen.ordinal() : -1);

        if (screen == null) {
        	throw new IllegalArgumentException("You must specify a view");
        }
    }

    private SetMainScreenPacket(PacketReader reader) {
    	super(TYPE_MAINSCREEN, reader);
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(MainScreenView.values()[mArg]);
	}
}