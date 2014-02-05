package net.dhleong.acl.iface;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.dhleong.acl.protocol.ArtemisPacket;

/**
 * Contains ListenerMethods to be invoked when a compatible ArtemisPacket
 * arrives.
 * @author rjwut
 */
public class ListenerRegistry {
    private List<ListenerMethod> listeners = new CopyOnWriteArrayList<ListenerMethod>();

    /**
     * Registers all methods on the given Object which have the @PacketListener
     * annotation with the registry.
     */
    void register(Object object) {
		Method[] methods = object.getClass().getMethods();

		for (Method method : methods) {
			Annotation anno = method.getAnnotation(PacketListener.class);

			if (anno != null) {
				listeners.add(new ListenerMethod(object, method));
			}
		}
    }

    /**
     * Returns true if any listeners are interested in packets of the given
     * class; false otherwise.
     */
    public boolean listeningFor(Class<? extends ArtemisPacket> clazz) {
		for (ListenerMethod listener : listeners) {
			if (listener.accepts(clazz)) {
				return true;
			}
		}

		return false;
    }

    /**
     * Fires all listeners which are compatible with the given ArtemisPacket.
     */
    void fire(ArtemisPacket packet) {
		for (ListenerMethod listener : listeners) {
			listener.offer(packet);
		}
    }

    /**
     * Removes all registered listeners.
     */
    void clear() {
    	synchronized (listeners) {
    		listeners.clear();
    	}
    }
}