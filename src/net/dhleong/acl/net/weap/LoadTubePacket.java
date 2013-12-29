package net.dhleong.acl.net.weap;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.OrdnanceType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;
import net.dhleong.acl.world.Artemis;

/**
 * Load a type of torpedo into a tube
 * @author dhleong
 */
public class LoadTubePacket extends BaseArtemisPacket {
    private static final int TYPE = 0x69CC01D9;
    private static final int SUBTYPE = 0x02;

    private int mTube;
    private OrdnanceType mOrdnanceType;

    /**
     * @param tube Indexed from 0
     * @param torpedoType one of the TORP_* constants
     */
    public LoadTubePacket(int tube, OrdnanceType ordnanceType) {
        super(ConnectionType.CLIENT, TYPE);

        if (tube < 0 || tube >= Artemis.MAX_TUBES) {
        	throw new IndexOutOfBoundsException(
        			"Invalid tube index: " + tube
        	);
        }

        if (ordnanceType == null) {
        	throw new IllegalArgumentException(
        			"You must specify an ordnance type"
        	);
        }

        mTube = tube;
        mOrdnanceType = ordnanceType;
    }

	@Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(TYPE)
    			.writeInt(SUBTYPE)
    			.writeInt(mTube)
    			.writeInt(mOrdnanceType.ordinal());
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("Tube #").append(mTube).append(": ").append(mOrdnanceType);
	}
}