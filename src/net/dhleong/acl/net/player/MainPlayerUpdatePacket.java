package net.dhleong.acl.net.player;


import net.dhleong.acl.enums.MainScreenView;
import net.dhleong.acl.net.setup.SetShipSettingsPacket.DriveType;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.util.ObjectParser;
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

    public MainPlayerUpdatePacket(byte[] data) {
        super(data);
        ObjectParser p = new ObjectParser(mData, 0);

        try {
        	while (p.hasMore()) {
                p.start(Bit.values());
                p.readInt(Bit.UNK_0);

                float impulseSlider = p.readFloat(Bit.IMPULSE, -1); 
                float steeringSlider = p.readFloat(Bit.RUDDER, Float.MIN_VALUE);
                float topSpeed = p.readFloat(Bit.TOP_SPEED, -1);
                float turnRate = p.readFloat(Bit.TURN_RATE, -1);
                BoolState mAutoBeams = p.readBoolByte(Bit.AUTO_BEAMS);
                byte warp = p.readByte(Bit.WARP, (byte) -1);
                float energy = p.readFloat(Bit.ENERGY, -1);
                BoolState shields;

                if (p.has(Bit.SHIELD_STATE)) {
                    shields = BoolState.from(p.readShort() != 0);
                } else {
                    shields = BoolState.UNKNOWN;
                }

                int shipNumber = p.readInt(Bit.SHIP_NUMBER);
                int hullId = p.readInt(Bit.SHIP_TYPE);
                float x = p.readFloat(Bit.X, -1);
                float y = p.readFloat(Bit.Y, -1);
                float z = p.readFloat(Bit.Z, -1);

                p.readUnknown(Bit.UNK_3, 4);
                p.readUnknown(Bit.UNK_4, 4);

                float heading = p.readFloat(Bit.HEADING, Float.MIN_VALUE);
                float velocity = p.readFloat(Bit.VELOCITY, -1);

                p.readUnknown(Bit.UNK_5, 2);

                String name = p.readName(Bit.NAME);
                float shieldsFront = p.readFloat(Bit.FORE_SHIELDS, -1);
                float shieldsFrontMax = p.readFloat(Bit.FORE_SHIELDS_MAX, -1);
                float shieldsRear = p.readFloat(Bit.AFT_SHIELDS, -1);
                float shieldsRearMax = p.readFloat(Bit.AFT_SHIELDS_MAX, -1);

                // I don't *think* the server sends us
                //  this value when we undock...
                int dockingStation = p.readInt(Bit.DOCKING_STATION, 0);
                BoolState redAlert = p.readBoolByte(Bit.RED_ALERT);
                
                p.readUnknown(Bit.UNK_6, 4);

                MainScreenView mainScreen;

                if (p.has(Bit.MAIN_SCREEN)) {
                    mainScreen = MainScreenView.values()[p.readByte()];
                } else {
                    mainScreen = null;
                }

                byte beamFreq = p.readByte(Bit.BEAM_FREQUENCY, (byte) -1);

                // total available coolant?
                byte availableCoolant = p.readByte(Bit.AVAILABLE_COOLANT, (byte)-1); // MUST
                int scanTarget = p.readInt(Bit.SCIENCE_TARGET, Integer.MIN_VALUE); // 1 means no target
                int captainTarget = p.readInt(Bit.CAPTAIN_TARGET, Integer.MIN_VALUE);
                byte driveType = p.readByte(Bit.DRIVE_TYPE, (byte)-1);
                int scanningId = p.readInt(Bit.SCAN_OBJECT_ID);
                float scanProgress = p.readFloat(Bit.SCAN_PROGRESS, -1);
                BoolState mReverse = p.readBoolByte(Bit.REVERSE_STATE);

                ArtemisPlayer player = new ArtemisPlayer(
                		p.getTargetId(), name, hullId, shipNumber, redAlert,
                		shields
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
                player.setShipEnergy(energy);
                player.setDockingStation(dockingStation);
                player.setMainScreen(mainScreen);
                player.setBeamFrequency(beamFreq);
                player.setAvailableCoolant(availableCoolant);
                player.setScanTarget(scanTarget);
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
                player.setUnknownFields(p.getUnknownFields());
                mObjects.add(player);
            }
        } catch (RuntimeException e) {
            System.out.println("!!! Error!");
            debugPrint();
            System.out.println("this -->" + this);
            throw e;
        }
    }
    
    
    @Override
    public void debugPrint() {
    	for (ArtemisPlayer player : mObjects) {
	        System.out.println(
	        		String.format(
	        				"DEBUG: %s:%d(%.0f)@[%.2f,%.2f,%.2f]<%.2f>",
	        				player.getName(),
	        				player.getHullId(),
	        				player.getEnergy(),
	        				player.getX(),
	        				player.getY(),
	        				player.getZ(),
	        				player.getBearing()
	        		)
	        );
	        System.out.println("-------Ship numb: " + player.getId());
	        System.out.println("-------Red Alert: " + player.getRedAlertState());
	        System.out.println("-------ShieldsUp: " + player.getShieldsState());
	        System.out.println("---------Coolant: " + player.getAvailableCoolant());

	        DriveType driveType = player.getDriveType();

	        if (driveType != null) {
	            System.out.println("-----------Drive: " + player.getDriveType());
	        }

	        System.out.println(
	        		String.format(
	        				"-------[%.1f/%.2f  %.1f,%.1f]",
	        				player.getShieldsFront(),
	        				player.getShieldsFrontMax(),
	        				player.getShieldsRear(),
	        				player.getShieldsRearMax()
	        		)
	        );
    	}
    }
}
