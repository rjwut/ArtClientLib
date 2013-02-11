package net.dhleong.acl.net.comms;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.world.ArtemisObject;

public class CommsOutgoingPacket extends BaseArtemisPacket {
    public enum Mode {
        TO_PLAYER(ArtemisObject.TYPE_PLAYER_MAIN),
        TO_ENEMY(ArtemisObject.TYPE_ENEMY),
        TO_STATION(ArtemisObject.TYPE_STATION),
        TO_OTHER(ArtemisObject.TYPE_OTHER);
        
        public final byte objType;

        Mode(byte objType) {
            this.objType = objType;
        }
        
        public static Mode fromObjType(int type) {
            for (Mode m : values()) {
                if (m.objType == type)
                    return m;
            }
                
            return null;
        }
    }
    
    public enum Message {
        // for players
        Yes(0x00),
        No(0x01),
        Help(0x02),
        Greetings(0x03),
        Die(0x04),
        Were_leaving_the_sector_Bye(0x05),
        Ready_to_go(0x06),
        Please_follow_us(0x07),
        Well_follow_you(0x08),
        Were_badly_damaged(0x09),
        Were_headed_back_to_the_station(0x0a),
        Sorry_please_disregard(0x0b),
        
        // for enemies
        Will_you_surrender(0x00),
        I_can_smell_you_from_here_space_scum(0x01),
        Your_matriarchal_leader_wears_combat_boots(0x02),
        Im_still_here_waiting_blogrot(0x03),

        // for stations
        Request_Docking(0x00),
        Request_Status(0x01),
        Build_Type_1_Homing_ordnance(0x02),
        Build_Type_4_LR_Nuke_ordnance(0x03),
        Build_Type_6_Mine_ordnance(0x04),
        Build_Type_9_ECM_ordnance(0x05),

        // for other ships
        Please_report_status(0x00),
        Turn_to_heading_0(0x01),
        Turn_to_heading_90(0x02),
        Turn_to_heading_180(0x03),
        Turn_to_heading_270(0x04),
        Turn_left_10_degrees(0x05),
        Turn_right_10_degrees(0x06),
        Turn_left_25_degrees(0x0f),
        Turn_right_25_degrees(0x10),
        Attack_nearest_enemy(0x07),
        Proceed_to_your_destination(0x08),
        
        /** REQUIRES an argument! */
        Go_defend(0x09);

        public final int value;
        Message(int value) {
            this.value = value;
        }
    }

    private static final int FLAGS = 0x18;
    private static final int TYPE = 0x574C4C4B;

    
    public CommsOutgoingPacket(Mode mode, ArtemisObject target, Message msg) {
        this(mode, target, msg, 0x00730078);
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
    public CommsOutgoingPacket(Mode mode, ArtemisObject target, Message msg,
            int arg1) {
        this(mode, target, msg, arg1, 0x004f005e);
    }
    
    /** So far not used...? */
    private CommsOutgoingPacket(Mode mode, ArtemisObject target, Message msg,
            int arg1, int arg2) {
        super(0x2, FLAGS, TYPE, new byte[20]);
        
        PacketParser.putLendInt(mode.ordinal(), mData, 0); // ?
        PacketParser.putLendInt(target.getId(), mData, 4);
        PacketParser.putLendInt(msg.value, mData, 8);
        PacketParser.putLendInt(arg1, mData, 12);
        PacketParser.putLendInt(arg2, mData, 16);
    }
}
