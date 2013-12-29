package net.dhleong.acl;

import java.io.IOException;
import java.nio.charset.Charset;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.PacketWriter;

public interface ArtemisPacket {
	public static final int HEADER = 0xdeadbeef;
	public static final Charset CHARSET = Charset.forName("UTF-16LE");

    /**
     * Special "type" of packet represented by old
     *  SystemInfoPacket; these packets create and/or
     *  update various world objects
     */
	public static final int WORLD_TYPE = 0x80803df9;

    /**
     * Broad "type" of packet used for various ship
     *  actions initiated by the player
     */
    public static final int SHIP_ACTION_TYPE = 0x4C821D3C;

    public ConnectionType getConnectionType();
    public void write(PacketWriter writer) throws IOException;
    int getType();
}