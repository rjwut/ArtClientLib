package net.dhleong.acl.enums;

/**
 * Messages that can be sent to bases.
 * @author rjwut
 */
public enum BaseMessage implements CommsMessage {
	STAND_BY_FOR_DOCKING("Stand by for docking."),
	PLEASE_REPORT_STATUS("Please report status."),
	BUILD_HOMING_MISSILES("Please build type 1 homing ordnance for us."),
	BUILD_NUKES("Please build type 4 nuke ordnance for us."),
	BUILD_MINES("Please build type 6 mine ordnance for us."),
	BUILD_EMPS("Please build type 9 EMP ordnance for us.");

	private String label;

	BaseMessage(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public boolean hasArgument() {
		return false;
	}

	@Override
	public int getId() {
		return ordinal();
	}

	@Override
	public CommsRecipientType getRecipientType() {
		return CommsRecipientType.BASE;
	}
}