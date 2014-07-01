package net.dhleong.acl.iface;

/**
 * An event that gets thrown when an existing connection to a remote machine is
 * lost. (A failure to connect in the first place is a
 * {@link ConnectionFailureEvent}.) The {@link #cause} property indicates why
 * the connection was lost. If there is an exception that further explains the
 * event, it is provided in the {@link #exception} property; otherwise, it will
 * be null.
 * @author rjwut
 */
public class DisconnectEvent extends ConnectionEvent {
	public enum Cause {
		/**
		 * The connection was closed from this side; in other words, the user
		 * terminated the connection intentionally.
		 */
		LOCAL_DISCONNECT,

		/**
		 * The connection was closed from the remote side. This could be because
		 * the remote machine terminated the connection intentionally, or a
		 * network problem caused the connection to drop.
		 */
		REMOTE_DISCONNECT,

		/**
		 * ArtClientLib received a packet with an unexpected format. This would
		 * typically be caused by a bug in ArtClientLib or in a protocol
		 * extension being used by a custom proxy.
		 */
		PACKET_PARSE_EXCEPTION,

		/**
		 * An I/O exception occurred. The {@link #exception} property may have
		 * more information, but this is generally an external problem that
		 * ArtClientLib can't do anything about.
		 */
		IO_EXCEPTION,

		/**
		 * The server version in use is not supported by ArtClientLib. This
		 * fork of ArtClientLib doesn't support Artemis versions earlier than
		 * 2.0, and there are no plans to do so. Future Artemis releases will
		 * require updates to ArtClientLib to support them.
		 */
		UNSUPPORTED_SERVER_VERSION
	}

	private Cause cause;
	private Exception exception;

	DisconnectEvent(Cause cause, Exception exception) {
		this.cause = cause;
		this.exception = exception;
	}

	public Cause getCause() {
		return cause;
	}

	public Exception getException() {
		return exception;
	}
}