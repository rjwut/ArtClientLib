package net.dhleong.acl.enums;

/**
 * Stance towards the player:
 * - PLAYER: A playable vessel (self or ally)
 * - FRIENDLY: Civilian vessel, will follow orders unless hijacked
 * - ENEMY: Hostile AI-controlled vessel
 * @author rjwut
 */
public enum Allegiance {
	PLAYER,
	FRIENDLY,
	ENEMY
}