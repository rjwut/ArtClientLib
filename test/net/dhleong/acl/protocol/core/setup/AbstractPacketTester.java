package net.dhleong.acl.protocol.core.setup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.TestPacketFile;
import net.dhleong.acl.util.TextUtil;

public abstract class AbstractPacketTester<T extends ArtemisPacket> {
	private static final boolean DEBUG = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("jdwp") >= 0;

	protected abstract void testPackets(List<T> packets);

	protected void execute(String resourcePath, ConnectionType type, int packetCount) {
		try {
			List<T> list = new ArrayList<T>(packetCount);
			TestPacketFile file = new TestPacketFile(resourcePath);
	
			if (DEBUG) {
				System.out.println("### " + resourcePath + " ###");
				System.out.println(getClass().getSimpleName() + " [R]: " + file);
			}
	
			PacketReader reader = file.toPacketReader(type);
	
			for (int i = 0; i < packetCount; i++) {
				T pkt = (T) reader.readPacket();
				Assert.assertNotNull(pkt);
	
				if (DEBUG) {
					System.out.println(pkt);
				}
	
				list.add(pkt);
			}

			Assert.assertFalse(reader.hasMore());
			testPackets(list);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PacketWriter writer = new PacketWriter(baos);
	
			for (T pkt : list) {
				pkt.writeTo(writer);
			}
	
			if (DEBUG) {
				System.out.println(TextUtil.byteArrayToHexString(baos.toByteArray()));
			}
	
			Assert.assertTrue(file.matches(baos));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (ArtemisPacketException ex) {
			throw new RuntimeException(ex);
		}
	}
}