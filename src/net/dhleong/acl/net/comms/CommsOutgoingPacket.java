package net.dhleong.acl.net.comms;

import net.dhleong.acl.enums.CommsMessage;
import net.dhleong.acl.enums.CommsTargetType;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.world.ArtemisObject;

public class CommsOutgoingPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x574C4C4b;
    private static final int ARG_1_PLACEHOLDER = 0x00730078;
    private static final int ARG_2_PLACEHOLDER = 0x004f005e;

    public CommsOutgoingPacket(ArtemisObject target, CommsMessage msg) {
        super(ConnectionType.CLIENT, TYPE, new byte[20]);

        if (msg.hasArgument()) {
        	throw new IllegalArgumentException(
        			"Message " + msg + " requires an argument"
        	);
        }

        init(target, msg, ARG_1_PLACEHOLDER);
    }
    
    /**
     * Use this constructor when sending a {@link Message#Go_defend} message;
     *  arg1 should be the objId of the "defend" target
     *  
     * @param mode
     * @param target
     * @param msg
     * @param arg1
     */
    public CommsOutgoingPacket(ArtemisObject target, CommsMessage msg,
            int arg) {
        super(ConnectionType.CLIENT, TYPE, new byte[20]);

        if (!msg.hasArgument()) {
        	throw new IllegalArgumentException(
        			"Message " + msg + " does not accept an argument"
        	);
        }

        init(target, msg, arg);
    }

    private void init(ArtemisObject target, CommsMessage msg, int arg) {
    	CommsTargetType tt = CommsTargetType.fromObject(target);

    	if (tt == null) {
    		throw new IllegalArgumentException("Target cannot receive messages");
    	}

    	if (tt != msg.getTargetType()) {
    		throw new IllegalArgumentException(
    				"Target type is " + tt + ", but message target type is " +
    				msg.getTargetType()
    		);
    	}

    	PacketParser.putLendInt(tt.ordinal(), mData, 0);
        PacketParser.putLendInt(target.getId(), mData, 4);
        PacketParser.putLendInt(msg.getId(), mData, 8);
        PacketParser.putLendInt(arg, mData, 12);
        PacketParser.putLendInt(ARG_2_PLACEHOLDER, mData, 16);
    }
}