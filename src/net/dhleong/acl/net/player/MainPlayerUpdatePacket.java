package net.dhleong.acl.net.player;

import net.dhleong.acl.enums.BeamFrequency;
import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.MainScreenView;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.world.ArtemisPlayer;

public class MainPlayerUpdatePacket extends PlayerUpdatePacket {
    private enum Bit {
    	UNK_0,
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
    	UNK_3,
    	UNK_4,

    	HEADING,
    	VELOCITY,
    	UNK_5,
    	NAME,
    	FORE_SHIELDS,
    	FORE_SHIELDS_MAX,
    	AFT_SHIELDS,
    	AFT_SHIELDS_MAX,

    	DOCKING_STATION,
    	RED_ALERT,
    	UNK_6,
    	MAIN_SCREEN,
    	BEAM_FREQUENCY,
    	AVAILABLE_COOLANT,
    	SCIENCE_TARGET,
    	CAPTAIN_TARGET,
    	DRIVE_TYPE,
    	SCAN_OBJECT_ID,
    	SCAN_PROGRESS,
    	REVERSE_STATE
    }

    public MainPlayerUpdatePacket(PacketReader reader) {
        try {
    		reader.startObject(Bit.values());
    		reader.readObjectUnknown(Bit.UNK_0, 4);

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
            float x = reader.readFloat(Bit.X, -1);
            float y = reader.readFloat(Bit.Y, -1);
            float z = reader.readFloat(Bit.Z, -1);

            reader.readObjectUnknown(Bit.UNK_3, 4);
            reader.readObjectUnknown(Bit.UNK_4, 4);

            float heading = reader.readFloat(Bit.HEADING, Float.MIN_VALUE);
            float velocity = reader.readFloat(Bit.VELOCITY, -1);

            reader.readObjectUnknown(Bit.UNK_5, 2);

            String name = reader.readString(Bit.NAME);
            float shieldsFront = reader.readFloat(Bit.FORE_SHIELDS, -1);
            float shieldsFrontMax = reader.readFloat(Bit.FORE_SHIELDS_MAX, -1);
            float shieldsRear = reader.readFloat(Bit.AFT_SHIELDS, -1);
            float shieldsRearMax = reader.readFloat(Bit.AFT_SHIELDS_MAX, -1);

            // I don't *think* the server sends us
            //  this value when we undock...
            int dockingStation = reader.readInt(Bit.DOCKING_STATION, 0);
            BoolState redAlert = reader.readBool(Bit.RED_ALERT, 1);
            
            reader.readObjectUnknown(Bit.UNK_6, 4);

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

            mPlayer = new ArtemisPlayer(
            		reader.getObjectId(), name, hullId, shipNumber,
            		redAlert, shields
            );
            mPlayer.setTopSpeed(topSpeed);
            mPlayer.setTurnRate(turnRate);
            mPlayer.setAutoBeams(mAutoBeams);
            mPlayer.setWarp(warp);
            mPlayer.setImpulse(impulseSlider);
            mPlayer.setSteering(steeringSlider);
            mPlayer.setX(x);
            mPlayer.setY(y);
            mPlayer.setZ(z);
            mPlayer.setBearing(heading);
            mPlayer.setVelocity(velocity);
            mPlayer.setShipEnergy(energy);
            mPlayer.setDockingStation(dockingStation);
            mPlayer.setMainScreen(mainScreen);
            mPlayer.setBeamFrequency(beamFreq);
            mPlayer.setAvailableCoolant(availableCoolant);
            mPlayer.setScanTarget(scanTarget);
            mPlayer.setCaptainTarget(captainTarget);
            mPlayer.setScanObjectId(scanningId);
            mPlayer.setScanProgress(scanProgress);
            mPlayer.setShieldsFront(shieldsFront);
            mPlayer.setShieldsFrontMax(shieldsFrontMax);
            mPlayer.setShieldsRear(shieldsRear);
            mPlayer.setShieldsRearMax(shieldsRearMax);
            mPlayer.setDriveType(driveType == -1
                    ? null
                    : DriveType.values()[driveType]);
            mPlayer.setReverse(mReverse);
            mPlayer.setUnknownFields(reader.getUnknownObjectFields());
        } catch (RuntimeException e) {
            System.out.println("!!! Error!");
            System.out.println("this -->" + this);
            throw e;
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mPlayer);
	}
}