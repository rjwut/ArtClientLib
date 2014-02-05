package net.dhleong.acl.protocol.core.world;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.BeamFrequency;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.MainScreenView;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPlayer;

/**
 * Provides principal updates for a player's ship. Some engineering- or
 * weapons-oriented updates are provided by the EngPlayerUpdatePacket or
 * WeapPlayerUpdatePacket.
 */
public class MainPlayerUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, WORLD_TYPE,
				ObjectType.PLAYER_SHIP.getId(), new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return MainPlayerUpdatePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new MainPlayerUpdatePacket(reader);
			}
		});
	}

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
    	PITCH,
    	ROLL,

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

    private List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    private MainPlayerUpdatePacket(PacketReader reader) {
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
            float pitch = reader.readFloat(Bit.PITCH, Float.MIN_VALUE);
            float roll = reader.readFloat(Bit.ROLL, Float.MIN_VALUE);
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
            byte availableCoolant = reader.readByte(Bit.AVAILABLE_COOLANT, (byte) -1);
            int scanTarget = reader.readInt(Bit.SCIENCE_TARGET, -1); // 1 means no target
            int captainTarget = reader.readInt(Bit.CAPTAIN_TARGET, -1);
            byte driveType = reader.readByte(Bit.DRIVE_TYPE, (byte) -1);
            int scanningId = reader.readInt(Bit.SCAN_OBJECT_ID, -1);
            float scanProgress = reader.readFloat(Bit.SCAN_PROGRESS, -1);
            BoolState mReverse = reader.readBool(Bit.REVERSE_STATE, 1);

            reader.readObjectUnknown(Bit.UNK_5_5, 4);
            reader.readObjectUnknown(Bit.UNK_5_6, 1);
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
            player.setPitch(pitch);
            player.setRoll(roll);
            player.setHeading(heading);
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

    public MainPlayerUpdatePacket() {
    	super(ConnectionType.SERVER, WORLD_TYPE);
    }

    @Override
    public List<ArtemisObject> getObjects() {
        return mObjects;
    }

    public void setObjects(List<ArtemisObject> objects) {
    	mObjects = objects;
    }

    @Override
	protected void writePayload(PacketWriter writer) {
		Bit[] bits = Bit.values();

		for (ArtemisObject obj : mObjects) {
			ArtemisPlayer player = (ArtemisPlayer) obj;
			int shipIndex = player.getShipIndex();
			int shipNumber = shipIndex == -1 ? -1 : shipIndex + 1;
			writer	.startObject(obj, bits)
					.writeUnknown(Bit.UNK_1_1)
					.writeFloat(Bit.IMPULSE, player.getImpulse(), -1)
					.writeFloat(Bit.RUDDER, player.getSteering(), -1)
					.writeFloat(Bit.TOP_SPEED, player.getTopSpeed(), -1)
					.writeFloat(Bit.TURN_RATE, player.getTurnRate(), -1)
					.writeBool(Bit.AUTO_BEAMS, player.getAutoBeams(), 1)
					.writeByte(Bit.WARP, player.getWarp(), (byte) -1)
					.writeFloat(Bit.ENERGY, player.getEnergy(), -1)
					.writeBool(Bit.SHIELD_STATE, player.getShieldsState(), 2)
					.writeInt(Bit.SHIP_NUMBER, shipNumber, -1)
					.writeInt(Bit.SHIP_TYPE, player.getHullId(), -1)
					.writeFloat(Bit.X, player.getX(), Float.MIN_VALUE)
					.writeFloat(Bit.Y, player.getY(), Float.MIN_VALUE)
					.writeFloat(Bit.Z, player.getZ(), Float.MIN_VALUE)
					.writeFloat(Bit.PITCH, player.getPitch(), Float.MIN_VALUE)
					.writeFloat(Bit.ROLL, player.getRoll(), Float.MIN_VALUE)
					.writeFloat(Bit.HEADING, player.getHeading(), Float.MIN_VALUE)
					.writeFloat(Bit.VELOCITY, player.getVelocity(), -1)
					.writeUnknown(Bit.UNK_3_3)
					.writeString(Bit.NAME, player.getName())
					.writeFloat(Bit.FORE_SHIELDS, player.getShieldsFront(), Float.MIN_VALUE)
					.writeFloat(Bit.FORE_SHIELDS_MAX, player.getShieldsFrontMax(), -1)
					.writeFloat(Bit.AFT_SHIELDS, player.getShieldsRear(), Float.MIN_VALUE)
					.writeFloat(Bit.AFT_SHIELDS_MAX, player.getShieldsRearMax(), -1)
					.writeInt(Bit.DOCKING_STATION, player.getDockingStation(), -1)
					.writeBool(Bit.RED_ALERT, player.getRedAlertState(), 1)
					.writeUnknown(Bit.UNK_4_3);

			MainScreenView screen = player.getMainScreen();

			if (screen != null) {
				writer.writeByte(Bit.MAIN_SCREEN, (byte) screen.ordinal(), (byte) -1);
			}

			BeamFrequency beamFreq = player.getBeamFrequency();

			if (beamFreq != null) {
				writer.writeByte(Bit.BEAM_FREQUENCY, (byte) beamFreq.ordinal(), (byte) -1);
			}

			writer	.writeByte(Bit.AVAILABLE_COOLANT, (byte) player.getAvailableCoolant(), (byte) -1)
					.writeInt(Bit.SCIENCE_TARGET, player.getScienceTarget(), -1)
					.writeInt(Bit.CAPTAIN_TARGET, player.getCaptainTarget(), -1);

			DriveType drive = player.getDriveType();

			if (drive != null) {
				writer.writeByte(Bit.DRIVE_TYPE, (byte) drive.ordinal(), (byte) -1);
			}

			writer	.writeInt(Bit.SCAN_OBJECT_ID, player.getScanObjectId(), -1)
					.writeFloat(Bit.SCAN_PROGRESS, player.getScanProgress(), -1)
					.writeBool(Bit.REVERSE_STATE, player.getReverseState(), 1)
					.writeUnknown(Bit.UNK_5_5)
					.writeUnknown(Bit.UNK_5_6)
					.writeUnknown(Bit.UNK_5_7)
					.endObject();
		}

		writer.writeInt(0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisObject obj : mObjects) {
			b.append("\nObject #").append(obj.getId()).append(obj);
		}
	}
}