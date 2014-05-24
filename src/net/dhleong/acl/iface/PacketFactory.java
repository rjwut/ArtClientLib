package net.dhleong.acl.iface;

import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;


/**
 * Interface for objects which can convert a byte array to a packet.
 * @author rjwut
 */
public interface PacketFactory {
	/**
	 * Returns the subclass of ArtemisPacket that this PacketFactory can
	 * produce.
	 */
	public Class<? extends ArtemisPacket> getFactoryClass();

	/**
	 * Returns a packet constructed with a payload read from the given
	 * PacketReader. (It is assumed that the preamble has already been read.)
	 * This method should throw an ArtemisPacketException if the payload is
	 * malformed.
	 */
	public ArtemisPacket build(PacketReader reader) throws ArtemisPacketException;
}