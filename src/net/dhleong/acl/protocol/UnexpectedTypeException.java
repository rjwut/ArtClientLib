package net.dhleong.acl.protocol;

public class UnexpectedTypeException extends IllegalArgumentException {
	private static final long serialVersionUID = -5961855010011595291L;

	public UnexpectedTypeException(int type, int expectedType) {
		super("Expected type " + expectedType + ", got " + type);
	}
}
