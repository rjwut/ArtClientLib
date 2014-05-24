package net.dhleong.acl.protocol.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;

public class GameOverReasonPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xf754c8fe;
    private static final byte MSG_TYPE = 0x14;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return GameOverReasonPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new GameOverReasonPacket(reader);
			}
		});
	}

	private List<String> mText;

	private GameOverReasonPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, TYPE);
        int subtype = reader.readInt();

        if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
        }

        mText = new LinkedList<String>();

        while (reader.hasMore()) {
        	mText.add(reader.readString());
        }
    }

    public GameOverReasonPacket(String... text) {
    	super(ConnectionType.SERVER, TYPE);
    	mText = Arrays.asList(text);
    }

    /**
     * The text describing why the game ended. Each String in the List is one
     * line.
     */
    public List<String> getText() {
        return new LinkedList<String>(mText);
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE);

		for (String line : mText) {
			writer.writeString(line);
		}
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (String line : mText) {
			b.append("\n\t").append(line);
		}
	}
}