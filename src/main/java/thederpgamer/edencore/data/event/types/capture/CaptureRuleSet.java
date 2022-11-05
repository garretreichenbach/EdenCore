package thederpgamer.edencore.data.event.types.capture;

import api.network.PacketReadBuffer;
import org.schema.game.common.data.blockeffects.config.StatusEffectType;
import thederpgamer.edencore.data.SerializableData;
import thederpgamer.edencore.data.event.*;
import thederpgamer.edencore.data.event.types.EventRuleSet;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class CaptureRuleSet extends EventRuleSet implements SerializableData, Serializable {

	public CaptureRuleSet(CaptureEvent event) {
		super(event);
	}

	public CaptureRuleSet(PacketReadBuffer readBuffer) throws IOException {
		super(readBuffer);
	}

	@Override
	public void calculateValues(EventData eventData, SquadData squadData) {
		//Compare how many players are in the squad, what mass ships they have, vs how many enemies and what mass ships they have
		//Use this to calculate difficulty and modifiers
		int squadSize = squadData.squadMembers.size();
		if(squadSize <= minPlayers || squadSize >= maxPlayers) {
			//Squad is too small or too large, don't start the event
			return;
		}

		//Calculate difficulty
		double squadMass = 0;
		for(SquadMemberData squadMemberData : squadData.squadMembers) squadMass += squadMemberData.shipMass;
		double enemyMass = totalEnemyTargetMass;
		double difficulty = squadMass / enemyMass;
		if(squadSize < enemyCount) difficulty *= 0.5;
		else if(squadSize > enemyCount) difficulty *= 1.5;
		difficulty += Math.random() * 0.1; //Add a slight bit of randomness

		double playerPower = 0;
		double enemyPower = 0;
		//Calculate power of both sides using the mass of each ship, and take into account how many ships the side has total.

		playerModifiers.clear();
		enemyModifiers.clear();

		//Get the largest ship and smallest ships on both sides, and get an upper and lower average mass for each side.
		//If more ships are closer to the lower average mass, the sids is weaker, if more ships are closer to the upper average mass, the side is stronger.
		double playerUpperAverageMass = 0;
		double playerLowerAverageMass = 0;
		double enemyUpperAverageMass = 0;
		double enemyLowerAverageMass = 0;

		//Calculate the upper and lower average masses.
		for(SquadMemberData memberData : squadData.squadMembers) {
			if(memberData.shipMass > playerUpperAverageMass) playerUpperAverageMass = memberData.shipMass;
			else if(memberData.shipMass < playerLowerAverageMass) playerLowerAverageMass = memberData.shipMass;
		}
		//Now do the same for the enemy side.
		for(EventEnemyData enemyData : eventData.getEnemies()) {
			if(enemyData.getMass() > enemyUpperAverageMass) enemyUpperAverageMass = enemyData.getMass();
			else if(enemyData.getMass() < enemyLowerAverageMass) enemyLowerAverageMass = enemyData.getMass();
		}

		//Create a list of ships that are closer to the lower mass average, and a list of ships that are closer to the upper mass average.
		ArrayList<SquadMemberData> playerLowerAverageMassShips = new ArrayList<>();
		ArrayList<SquadMemberData> playerUpperAverageMassShips = new ArrayList<>();

		ArrayList<EventEnemyData> enemyLowerAverageMassShips = new ArrayList<>();
		ArrayList<EventEnemyData> enemyUpperAverageMassShips = new ArrayList<>();

		//Add ships to the lists.
		for(SquadMemberData memberData : squadData.squadMembers) {
			if(memberData.shipMass < playerUpperAverageMass) playerLowerAverageMassShips.add(memberData);
			else playerUpperAverageMassShips.add(memberData);
		}

		for(EventEnemyData enemyData : eventData.getEnemies()) {
			if(enemyData.getMass() < enemyUpperAverageMass) enemyLowerAverageMassShips.add(enemyData);
			else enemyUpperAverageMassShips.add(enemyData);
		}

		double squadModifierLevel = 0;
		double enemyModifierLevel = 0;
		//Try to balance these two values so that the event is challenging but not impossible. AI tends to be dumb, so give the enemies a slight advantage.
		//Pick some modifiers, and for each one calculate how strong it is and add it to the modifier level. If the modifier level is too high compared to the other side,
		//remove the weakest modifier and repeat until the values are roughly even.

		//If the player squad has more lighter ships than heavier ships, they have a lot of mobility so give the enemy better aiming.
		if(playerLowerAverageMassShips.size() > playerUpperAverageMassShips.size()) {
			float effectStrength = (float) (playerLowerAverageMassShips.size() - playerUpperAverageMassShips.size()) / (float) playerLowerAverageMassShips.size();
			EventModifier modifier = new EventModifier("Better Targeting", "This ship has been outfitted with the latest AI targeting technology, and has superior aiming against smaller craft.", StatusEffectType.AI_ACCURACY_TURRET, effectStrength);
			enemyModifierLevel += effectStrength * 1.15f; //Todo: Come up with a better way of calculating how powerful a specific modifier is.
		}

		//If the player squad has more heavier ships than lighter ships, they have a lot of firepower so give the enemy better armor.
		if(playerUpperAverageMassShips.size() > playerLowerAverageMassShips.size()) {
			float effectStrength = (float) (playerUpperAverageMassShips.size() - playerLowerAverageMassShips.size()) / (float) playerUpperAverageMassShips.size();
			EventModifier kineticMod = new EventModifier("Dense Armor", "This ship has very dense and heavy armor.", StatusEffectType.ARMOR_DEFENSE_KINETIC, effectStrength);
			EventModifier emMod = new EventModifier("Dense Armor", "This ship has very dense and heavy armor.", StatusEffectType.ARMOR_DEFENSE_EM, effectStrength);
			EventModifier heatMod = new EventModifier("Dense Armor", "This ship has very dense and heavy armor.", StatusEffectType.ARMOR_DEFENSE_HEAT, effectStrength);
			enemyModifierLevel += effectStrength * 3.35f; //Todo: Come up with a better way of calculating how powerful a specific modifier is.
			//Choose a random enemy ship to apply the modifier to. Prefer ships that are closer to the upper average mass.
			int randomIndex = (int) (Math.random() * enemyUpperAverageMassShips.size());
			enemyModifiers.put((String) enemyModifiers.keySet().toArray()[randomIndex], kineticMod);
			enemyModifiers.put((String) enemyModifiers.keySet().toArray()[randomIndex], emMod);
			enemyModifiers.put((String) enemyModifiers.keySet().toArray()[randomIndex], heatMod);
		}
		//Todo: More modifiers

		//If the enemy modifier level is too high, remove the weakest modifier and repeat until the values are roughly even.
		while(enemyModifierLevel > squadModifierLevel * 1.5f) {
			//Find the weakest modifier
			String enemyName = "";
			EventModifier weakestModifier = null;
			for(Map.Entry<String, EventModifier> entry : enemyModifiers.entrySet()) {
				if(weakestModifier == null || entry.getValue().effectValue < weakestModifier.effectValue) {
					enemyName = entry.getKey();
					weakestModifier = entry.getValue();
				}
			}
			//Remove the weakest modifier
			enemyModifiers.remove(enemyName);
			//Recalculate the modifier level
			enemyModifierLevel = 0;
			for(EventModifier modifier : enemyModifiers.values()) enemyModifierLevel += modifier.effectValue;
		}
	}
}
