package net.dhleong.acl.enums;

/**
 * Represents the type of the machine found at the opposite end of a connection.
 * @author rjwut
 */
public enum ConnectionType {
	SERVER, CLIENT;

	public static final ConnectionType fromInt(int value) {
		return value == 1 ? SERVER : (value == 2 ? CLIENT : null);
	}

	private int val;

	ConnectionType() {
		val = ordinal() + 1;
	}

	public int toInt() {
		return val;
	}

	public ConnectionType opposite() {
		return this == SERVER ? CLIENT : SERVER;
	}
}