package net.dhleong.acl.protocol;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketWriter;

/**
 * Implements common packet functionality.
 */
public abstract class BaseArtemisPacket implements ArtemisPacket {
    protected abstract void writePayload(PacketWriter writer);
    protected abstract void appendPacketDetail(StringBuilder b);

    private final ConnectionType mConnectionType;
    private final int mType;

    /**
     * @param connectionType The packet's ConnectionType
     * @param packetType The packet's type value
     */
    public BaseArtemisPacket(ConnectionType connectionType, int packetType) {
        mConnectionType = connectionType;
        mType = packetType;
    }

    @Override
    public ConnectionType getConnectionType() {
        return mConnectionType;
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public final void write(PacketWriter writer) throws IOException {
    	writer.start(mType);
    	writePayload(writer);
    	writer.flush();
    }

    @Override
    public final String toString() {
    	StringBuilder b = new StringBuilder();
    	b.append('[').append(getClass().getSimpleName()).append("] ");
    	appendPacketDetail(b);
    	return b.toString();
    }
}