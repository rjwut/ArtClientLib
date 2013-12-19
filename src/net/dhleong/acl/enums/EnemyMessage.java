package net.dhleong.acl.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Messages that can be sent to enemy NPCs.
 * @author rwalker 
 */
public enum EnemyMessage implements CommsMessage {
	WILL_YOU_SURRENDER("Will you surrender?"),
	TAUNT_1("[Taunt #1]"),
	TAUNT_2("[Taunt #2]"),
	TAUNT_3("[Taunt #3]");

	private static final Map<Faction, String[]> FACTION_MESSAGES;
	private static final String[] HUMAN_MESSAGES = {
	    "Hey donkey breath! Make like Romeo and kill thyself!",
	    "Your sister is so ugly she taught the Horsehead Nebula to whinny!",
	    "You call that a ship? It should be hauled away AS garbage!"
	};

	static {
		FACTION_MESSAGES = new HashMap<Faction, String[]>();
		FACTION_MESSAGES.put(Faction.TSN, HUMAN_MESSAGES);
		FACTION_MESSAGES.put(Faction.CIVILIAN, HUMAN_MESSAGES);
		FACTION_MESSAGES.put(Faction.KRALIEN, new String[] {
		    "Hey wormface! Can I borrow your Holy Scroll of Amborax? I need to wipe my stinky feet!",
		    "You call that a warship? I could crush that toy with my bare hands.",
		    "You're so ugly that your wife will thank me for killing you!"
		});
		FACTION_MESSAGES.put(Faction.ARVONIAN, new String[] {
			"Queen Marah looks like a hideous pustule and smells like a flatulent Space Whale.",
			"Your husband dresses like a Situlan scum slug!",
			"I'll kill you later, Arvonian. Right now I'm enjoying a bowl of space whale soup."
		});
		FACTION_MESSAGES.put(Faction.TORGOTH, new String[] {
			"Do us all a favor and take a bath, you fungus-infested pachyderm!",
			"Your mother is a Space Whale!",
			"That broken down rust bucket guarantees victory for Earth."
		});
		FACTION_MESSAGES.put(Faction.SKARAAN, new String[] {
			"Your sister is a flea-bitten Matarian mule!",
			"Your ship is so ugly my eyeballs burn just looking at it!",
			"Your corporate bosses are a bunch of money-losing morons!"
		});
		FACTION_MESSAGES.put(Faction.KRALIEN, new String[] {
			"3.14159265358979",
			"We are not your enemy!",
			"Please communicate with us."
		});
	}

	private String label;

	EnemyMessage(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

	/**
	 * Returns a String containing the faction-specific message that this enum
	 * value represents. This is the message you'd want to display in a UI.
	 */
	public String toString(Faction faction) {
		if (this == WILL_YOU_SURRENDER) {
			return toString();
		}

		return FACTION_MESSAGES.get(faction)[ordinal() - 1];
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
	public CommsTargetType getTargetType() {
		return CommsTargetType.ENEMY;
	}
}