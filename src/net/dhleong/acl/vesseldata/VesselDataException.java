package net.dhleong.acl.vesseldata;

/**
 * Thrown when ArtClientLib fails to parse the vesselData.xml file.
 * @author rjwut
 */
public class VesselDataException extends Exception {
	private static final long serialVersionUID = -495427263065919450L;

	public VesselDataException(Exception ex) {
		super(ex);
	}
}