package net.dhleong.acl.iface;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Contains ListenerMethods to be invoked when a corresponding event occurs.
 * @author rjwut
 */
public class ListenerRegistry {
    private List<ListenerMethod> listeners = new CopyOnWriteArrayList<ListenerMethod>();

    /**
     * Registers all methods on the given Object which have the @Listener
     * annotation with the registry.
     */
    public void register(Object object) {
		Method[] methods = object.getClass().getMethods();

		for (Method method : methods) {
			if (method.getAnnotation(Listener.class) != null) {
				listeners.add(new ListenerMethod(object, method));
			}
		}
    }

    /**
     * Returns true if any listeners are interested in events or packets of the
     * given class; false otherwise.
     */
    public boolean listeningFor(Class<?> clazz) {
		for (ListenerMethod listener : listeners) {
			if (listener.accepts(clazz)) {
				return true;
			}
		}

		return false;
    }

    /**
     * Fires all listeners which are compatible with the given event or packet.
     */
    void fire(Object obj) {
		for (ListenerMethod listener : listeners) {
			listener.offer(obj);
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