package net.dhleong.acl.iface;

/**
 * An event that gets thrown when an attempt to connect to a remote machine
 * fails. (Losing an existing connection is {@link DisconnectEvent}.) The
 * {@link exception} may provide additional information about why the failure
 * occurred.
 * @author rjwut
 */
public class ConnectionFailureEvent extends ConnectionEvent {
	private Exception exception;

	public Exception getException() {
		return exception;
	}
}