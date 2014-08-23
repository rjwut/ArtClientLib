package net.dhleong.acl.protocol.core.setup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.BaseDebugger;
import net.dhleong.acl.iface.Debugger;
import net.dhleong.acl.iface.OutputStreamDebugger;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.TestPacketFile;

public abstract class AbstractPacketTester<T extends ArtemisPacket> {
	private static final boolean DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("jdwp") >= 0;

	protected static final float EPSILON = 0.00000001f;

	protected abstract void testPackets(List<T> packets);

	private Debugger debugger = DEBUG ? new OutputStreamDebugger() : new BaseDebugger();

	protected void execute(String resourcePath, ConnectionType type, int packetCount) {
		try {
			List<T> list = new ArrayList<T>(packetCount);
			URL url = TestPacketFile.class.getResource(resourcePath);
			TestPacketFile file = new TestPacketFile(url);

			if (DEBUG) {
				System.out.println("### " + resourcePath + " ###");
			}

			PacketReader reader = file.toPacketReader(type);
	
			for (int i = 0; i < packetCount; i++) {
				T pkt = (T) reader.readPacket(debugger);
				Assert.assertNotNull(pkt);
				list.add(pkt);
			}

			Assert.assertFalse(reader.hasMore());
			testPackets(list);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PacketWriter writer = new PacketWriter(baos);
	
			for (T pkt : list) {
				pkt.writeTo(writer, debugger);
			}

			Assert.assertTrue(file.matches(baos));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (ArtemisPacketException ex) {
			throw new RuntimeException(ex);
		}
	}
}