package net.dhleong.acl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Contains all the information needed to invoke a packet listener Method
 * (annotated with @PacketListener).
 * @author rjwut
 */
public class ListenerMethod {
	private Object object;
	private Method method;
	private Class<?> paramType;

	/**
	 * @param object The packet listener object
	 * @param method The annotated method
	 */
	ListenerMethod (Object object, Method method) {
		validate(method);
		this.object = object;
		this.method = method;
		paramType = method.getParameterTypes()[0];
	}

	/**
	 * Throws an IllegalArgumentException if the given method is not a valid
	 * packet listener method.
	 */
	private static void validate(Method method) {
		if (!Modifier.isPublic(method.getModifiers())) {
			throw new IllegalArgumentException(
					"Method " + method.getName() +
					" must be public to be a @PacketListener"
			);
		}

		if (!Void.TYPE.equals(method.getReturnType())) {
			throw new IllegalArgumentException(
					"Method " + method.getName() +
					" must return void to be a @PacketListener"
			);
		}

		Class<?>[] paramTypes = method.getParameterTypes();

		if (paramTypes.length != 1) {
			throw new IllegalArgumentException(
					"Method " + method.getName() +
					" must have exactly one argument"
			);
		}

		Class<?> paramType = paramTypes[0];

		if (!ArtemisPacket.class.isAssignableFrom(paramType)) {
			throw new IllegalArgumentException(
					"Method " + method.getName() +
					" argument must be an ArtemisPacket or a subtype of it"
			);
		}
	}

	/**
	 * Returns true if this ListenerMethod accepts packets of the given class;
	 * false otherwise.
	 */
	boolean accepts(Class<? extends ArtemisPacket> clazz) {
		return paramType.isAssignableFrom(clazz);
	}

	/**
	 * Invokes the wrapped listener Method, passing in the indicated
	 * ArtemisPacket, if it is type-compatible with the Method's argument;
	 * otherwise, nothing happens. Since the listeners have been pre-validated,
	 * no exception should occur, so we wrap the ones thrown by Method.invoke()
	 * in a RuntimeException.
	 */
	void offer(ArtemisPacket packet) {
		Class<?> clazz = packet.getClass();

		if (paramType.isAssignableFrom(clazz)) {
    		try {
				method.invoke(object, packet);
			} catch (IllegalAccessException ex) {
				throw new RuntimeException(ex);
			} catch (IllegalArgumentException ex) {
				throw new RuntimeException(ex);
			} catch (InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
}