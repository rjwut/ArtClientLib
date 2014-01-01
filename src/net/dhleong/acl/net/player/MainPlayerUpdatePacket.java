package net.dhleong.acl.net.player;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.BeamFrequency;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.MainScreenView;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.ObjectUpdatingPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Provides principal updates for a player's ship. Some engineering- or
 * weapons-oriented updates are provided by the EngPlayerUpdatePacket or
 * WeapPlayerUpdatePacket.
 */
public class MainPlayerUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
    private enum Bit {
    	UNK_1_1,
    	IMPULSE,
    	RUDDER,
    	TOP_SPEED,
    	TURN_RATE,
    	AUTO_BEAMS,
    	WARP,
    	ENERGY,

    	SHIELD_STATE,
    	SHIP_NUMBER,
    	SHIP_TYPE,
    	X,
    	Y,
    	Z,
    	UNK_2_7,
    	UNK_2_8,

    	HEADING,
    	VELOCITY,
    	UNK_3_3,
    	NAME,
    	FORE_SHIELDS,
    	FORE_SHIELDS_MAX,
    	AFT_SHIELDS,
    	AFT_SHIELDS_MAX,

    	DOCKING_STATION,
    	RED_ALERT,
    	UNK_4_3,
    	MAIN_SCREEN,
    	BEAM_FREQUENCY,
    	AVAILABLE_COOLANT,
    	SCIENCE_TARGET,
    	CAPTAIN_TARGET,

    	DRIVE_TYPE,
    	SCAN_OBJECT_ID,
    	SCAN_PROGRESS,
    	REVERSE_STATE,
    	UNK_5_5,
    	UNK_5_6,
    	UNK_5_7
    }

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    public MainPlayerUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
    	while (reader.hasMore()) {
    		reader.startObject(Bit.values());
    		reader.readObjectUnknown(Bit.UNK_1_1, 4);

    		float impulseSlider = reader.readFloat(Bit.IMPULSE, -1); 
            float steeringSlider = reader.readFloat(Bit.RUDDER, -1);
            float topSpeed = reader.readFloat(Bit.TOP_SPEED, -1);
            float turnRate = reader.readFloat(Bit.TURN_RATE, -1);
            BoolState mAutoBeams = reader.readBool(Bit.AUTO_BEAMS, 1);
            byte warp = reader.readByte(Bit.WARP, (byte) -1);
            float energy = reader.readFloat(Bit.ENERGY, -1);
            BoolState shields = reader.readBool(Bit.SHIELD_STATE, 2);
            int shipNumber = reader.readInt(Bit.SHIP_NUMBER);
            int hullId = reader.readInt(Bit.SHIP_TYPE);
            float x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            float y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            float z = reader.readFloat(Bit.Z, Float.MIN_VALUE);

            reader.readObjectUnknown(Bit.UNK_2_7, 4);
            reader.readObjectUnknown(Bit.UNK_2_8, 4);

            float heading = reader.readFloat(Bit.HEADING, Float.MIN_VALUE);
            float velocity = reader.readFloat(Bit.VELOCITY, -1);

            reader.readObjectUnknown(Bit.UNK_3_3, 2);

            String name = reader.readString(Bit.NAME);
            float shieldsFront = reader.readFloat(Bit.FORE_SHIELDS, Float.MIN_VALUE);
            float shieldsFrontMax = reader.readFloat(Bit.FORE_SHIELDS_MAX, -1);
            float shieldsRear = reader.readFloat(Bit.AFT_SHIELDS, Float.MIN_VALUE);
            float shieldsRearMax = reader.readFloat(Bit.AFT_SHIELDS_MAX, -1);
            int dockingStation = reader.readInt(Bit.DOCKING_STATION, -1);
            BoolState redAlert = reader.readBool(Bit.RED_ALERT, 1);

            reader.readObjectUnknown(Bit.UNK_4_3, 4);

            MainScreenView mainScreen;

            if (reader.has(Bit.MAIN_SCREEN)) {
                mainScreen = MainScreenView.values()[reader.readByte()];
            } else {
                mainScreen = null;
            }

            BeamFrequency beamFreq = null;

            if (reader.has(Bit.BEAM_FREQUENCY)) {
            	beamFreq = BeamFrequency.values()[reader.readByte()];
            }

            // total available coolant?
            byte availableCoolant = reader.readByte(Bit.AVAILABLE_COOLANT, (byte) -1); // MUST
            int scanTarget = reader.readInt(Bit.SCIENCE_TARGET, -1); // 1 means no target
            int captainTarget = reader.readInt(Bit.CAPTAIN_TARGET, -1);
            byte driveType = reader.readByte(Bit.DRIVE_TYPE, (byte) -1);
            int scanningId = reader.readInt(Bit.SCAN_OBJECT_ID, -1);
            float scanProgress = reader.readFloat(Bit.SCAN_PROGRESS, -1);
            BoolState mReverse = reader.readBool(Bit.REVERSE_STATE, 1);

            reader.readObjectUnknown(Bit.UNK_5_5, 4);
            reader.readObjectUnknown(Bit.UNK_5_6, 4);
            reader.readObjectUnknown(Bit.UNK_5_7, 4);

            ArtemisPlayer player = new ArtemisPlayer(
            		reader.getObjectId(), name, hullId, shipNumber,
            		redAlert, shields
            );
            player.setTopSpeed(topSpeed);
            player.setTurnRate(turnRate);
            player.setAutoBeams(mAutoBeams);
            player.setWarp(warp);
            player.setImpulse(impulseSlider);
            player.setSteering(steeringSlider);
            player.setX(x);
            player.setY(y);
            player.setZ(z);
            player.setBearing(heading);
            player.setVelocity(velocity);
            player.setEnergy(energy);
            player.setDockingStation(dockingStation);
            player.setMainScreen(mainScreen);
            player.setBeamFrequency(beamFreq);
            player.setAvailableCoolant(availableCoolant);
            player.setScienceTarget(scanTarget);
            player.setCaptainTarget(captainTarget);
            player.setScanObjectId(scanningId);
            player.setScanProgress(scanProgress);
            player.setShieldsFront(shieldsFront);
            player.setShieldsFrontMax(shieldsFrontMax);
            player.setShieldsRear(shieldsRear);
            player.setShieldsRearMax(shieldsRearMax);
            player.setDriveType(driveType == -1
                    ? null
                    : DriveType.values()[driveType]);
            player.setReverse(mReverse);
            player.setUnknownProps(reader.getUnknownObjectProps());
            mObjects.add(player);
    	}
    }

    @Override
    public List<ArtemisObject> getObjects() {
        return mObjects;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisObject obj : mObjects) {
			b.append("\nObject #").append(obj.getId()).append(obj);
		}
	}
}