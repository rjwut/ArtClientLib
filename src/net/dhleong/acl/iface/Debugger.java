package net.dhleong.acl.iface;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.RawPacket;

/**
 * Interface for objects which can be attached to an ArtemisNetworkInterface to
 * get notified of debugging-related events.
 * @author rjwut
 */
public interface Debugger {
	/**
	 * Invoked when a packet has been received, but before it is parsed.
	 */
	public void onRecvPacketBytes(ConnectionType connType, int pktType,
			byte[] payload);

	/**
	 * Invoked when a packet is successfully parsed. Packets which are not
	 * parsed (because they are unknown or because no listener is interested in
	 * them) do not trigger this listener.
	 */
	public void onRecvParsedPacket(ArtemisPacket pkt);

	/**
	 * Invoked when a packet is received and not parsed. This may occur because
	 * the packet type is unknown (UnknownPacket), or because no listener is
	 * interested in it (UnparsedPacket).
	 */
	public void onRecvUnparsedPacket(RawPacket pkt);

	/**
	 * Invoked just before a packet is written to the PacketWriter.
	 */
	public void onSendPacket(ArtemisPacket pkt);

	/**
	 * Invoked just after a packet is written to the PacketWriter and just
	 * before it is flushed to the OutputStream.
	 */
	public void onSendPacketBytes(ConnectionType connType, int pktType,
			byte[] payload);

	/**
	 * Invoked when the interface wishes to report a warning.
	 */
	public void warn(String msg);
}