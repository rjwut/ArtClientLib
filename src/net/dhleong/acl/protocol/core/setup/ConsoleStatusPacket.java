package net.dhleong.acl.protocol.core.setup;

import net.dhleong.acl.enums.Console;
import net.dhleong.acl.enums.ConsoleStatus;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.world.Artemis;

/**
 * Indicates which consoles are taken; received in response to a
 * SetConsolePacket or SetShipPacket.
 * @author dhleong
 */
public class ConsoleStatusPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x19c6e2d4;
    
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return ConsoleStatusPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new ConsoleStatusPacket(reader);
			}
		});
	}

    private final int shipNumber;
    private final ConsoleStatus[] statuses;
    
    private ConsoleStatusPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, TYPE);
        shipNumber = reader.readInt();
        final Console[] consoleValues = Console.values();
        final ConsoleStatus[] statusValues = ConsoleStatus.values();
        statuses = new ConsoleStatus[consoleValues.length];

        for (Console console : consoleValues) {
        	statuses[console.ordinal()] = statusValues[reader.readByte()];
        }
    }

    public ConsoleStatusPacket(int shipNumber, ConsoleStatus[] statuses) {
    	super(ConnectionType.SERVER, TYPE);

    	if (shipNumber < 1 || shipNumber > Artemis.SHIP_COUNT) {
    		throw new IllegalArgumentException(
    				"Ship number must be between 1 and " + Artemis.SHIP_COUNT
    		);
    	}

    	if (statuses.length != ConsoleStatus.values().length) {
    		throw new IllegalArgumentException(
    				"Must provide a status for each bridge console"
    		);
    	}

    	for (ConsoleStatus status : statuses) {
    		if (status == null) {
        		throw new IllegalArgumentException(
        				"Must provide a status for each bridge console"
        		);
    		}
    	}

    	this.shipNumber = shipNumber;
    	this.statuses = statuses;
    }

    /**
     * Returns the ship number whose consoles this packet reports.
     */
    public int getShipNumber() {
    	return shipNumber;
    }

    /**
     * Get the status for a specific Console
     * @param console The desired Console
     * @return ConsoleStatus The status of that console
     */
    public ConsoleStatus get(Console console) {
        if (console == null) {
            throw new IllegalArgumentException("You must specify a console");
        }

        return statuses[console.ordinal()];
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(shipNumber);

		for (ConsoleStatus status : statuses) {
			writer.writeByte((byte) status.ordinal());
		}
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Ship #").append(shipNumber);

		for (Console console : Console.values()) {
    		ConsoleStatus status = statuses[console.ordinal()];
			b.append("\n\t").append(console).append(": ").append(status);
    	}
	}
}