package net.dhleong.acl.iface;

import java.io.OutputStream;
import java.io.PrintStream;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.RawPacket;
import net.dhleong.acl.util.TextUtil;

public class OutputStreamDebugger implements Debugger {
	private String name;
	private PrintStream out;

	public OutputStreamDebugger() {
		this("", System.out);
	}

	public OutputStreamDebugger(String name) {
		this(name, System.out);
	}

	public OutputStreamDebugger(OutputStream out) {
		this("", out);
	}

	public OutputStreamDebugger(String name, OutputStream out) {
		this.name = name;
		this.out = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
	}

	@Override
	public void onRecvPacketBytes(ConnectionType connType, int pktType,
			byte[] payload) {
		printPacketBytes(false, connType, pktType, payload);
	}

	@Override
	public void onRecvParsedPacket(ArtemisPacket pkt) {
		out.println(name + "> " + pkt);
	}

	@Override
	public void onRecvUnparsedPacket(RawPacket pkt) {
		out.println(name + "< " + pkt);
	}

	@Override
	public void onSendPacket(ArtemisPacket pkt) {
		out.println(name + "< " + pkt);
	}

	@Override
	public void onSendPacketBytes(ConnectionType connType, int pktType,
			byte[] payload) {
		printPacketBytes(true, connType, pktType, payload);
	}

	@Override
	public void warn(String msg) {
		out.println((name != "" ? (name + ": ") : "") + "WARNING: "  + msg);
	}

	private void printPacketBytes(boolean send, ConnectionType connType,
			int pktType, byte[] payload) {
		out.println(
				name + (send ? "< " : "> ") +
				TextUtil.intToHexLE(ArtemisPacket.HEADER) + " " +
				TextUtil.intToHexLE(payload.length + 24) + " " +
				TextUtil.intToHexLE(connType.toInt()) + " " +
				TextUtil.intToHexLE(0) + " " +
				TextUtil.intToHexLE(payload.length + 4) + " " +
				TextUtil.intToHexLE(pktType) + " " +
				TextUtil.byteArrayToHexString(payload)
		);
	}
}