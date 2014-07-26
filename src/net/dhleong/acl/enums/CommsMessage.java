package net.dhleong.acl.enums;

/**
 * All messages that can be sent over COMMs implement this interface.
 * @author rjwut
 */
public interface CommsMessage {
	/**
	 * Returns the ID of this CommsMessage. IDs are unique per CommsTargetType.
	 */
	public int getId();

	/**
	 * Returns the CommsTargetType that can recieve this CommsMessage.
	 */
	public CommsRecipientType getRecipientType();

	/**
	 * Returns whether or not this message has an argument.
	 */
	public boolean hasArgument();
}