package net.dhleong.acl.net.protocol;

import java.util.LinkedList;
import java.util.List;

/**
 * A registry of PacketFactories. This class handles the job of selecting the
 * appropriate PacketFactory for a given type and subType. PacketFactories are
 * checked in the order in which they were registered. Therefore, registering
 * the most frequently-encountered PacketFactories first will result in better
 * performance.
 * 
 * Every PacketFactoryRegistry is pre-seeded with factories from
 * {@link CoreArtemisProtocol}.
 * 
 * @author rjwut
 */
public class PacketFactoryRegistry {
	/**
	 * An entry in the PacketFactoryRegistry.
	 */
	private class Entry {
		private int type;
		private Byte subType;
		private PacketFactory factory;

		private Entry(int type, PacketFactory factory) {
			this.type = type;
			this.factory = factory;
		}

		private Entry(int type, byte subType, PacketFactory factory) {
			this.type = type;
			this.subType = Byte.valueOf(subType);
			this.factory = factory;
		}

		/**
		 * Returns true if this Entry corresponds to the offered type and
		 * subType; false otherwise.
		 */
		private boolean match(int offeredType, byte offeredSubType) {
			if (type != offeredType) {
				return false;
			}

			return subType == null || subType.byteValue() == offeredSubType;
		}
	}

	private List<Entry> list = new LinkedList<Entry>();

	public PacketFactoryRegistry() {
		new CoreArtemisProtocol().registerPacketFactories(this);
	}

	/**
	 * Registers the given PacketFactory under the indicated type.
	 */
	public void register(int type, PacketFactory factory) {
		list.add(new Entry(type, factory));
	}

	/**
	 * Registers the given PacketFactory under the indicated type and subType.
	 */
	public void register(int type, byte subType, PacketFactory factory) {
		list.add(new Entry(type, subType, factory));
	}

	/**
	 * Returns the PacketFactory registered under the given type and subType, or
	 * null if no such PacketFactory has been registered. If a packetFactory was
	 * not registered under a subType, the subType will be ignored when matching
	 * on it.
	 */
	public PacketFactory get(int type, byte subType) {
		for (Entry entry : list) {
			if (entry.match(type, subType)) {
				return entry.factory;
			}
		}

		return null;
	}
}