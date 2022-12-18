package thederpgamer.edencore.manager;

import api.common.GameServer;
import org.jdesktop.swingx.calendar.DateUtils;
import org.schema.schine.network.RegisteredClientOnServer;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.EventInstanceData;
import thederpgamer.edencore.data.event.SquadData;
import thederpgamer.edencore.data.player.PlayerData;
import thederpgamer.edencore.utils.DataUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/13/2021]
 */
public class EventManager {

	private static final ConcurrentHashMap<Integer, EventData> eventMap = new ConcurrentHashMap<>();

	/**
	 * Runs the date and time checkers for all event intervals and generates new ones when needed.
	 * <p>For Example, if this is run on a sunday between 12:00 AM - 1:00 AM it will generate a new weekly and a new daily event,
	 * and even a new monthly one if it's the first week of the month.</p>
	 */
	public static void doRunners() {
		long ms = System.currentTimeMillis();
		Date date = new Date(ms);
		Date twelveAM = DateUtils.startOfDay(date);
		Date oneAM = DateUtils.startOfDay(date);
		oneAM.setTime(oneAM.getTime() + 3600000);
		if(date.after(twelveAM) && date.before(oneAM)) {
			{ //Daily
				int eventCode = EventData.DAILY;
				EventData[] events = new EventData[] {generateEvent(eventCode |  EventData.PVE), generateEvent(eventCode | EventData.PVP)};
				eventMap.replace(eventCode | EventData.PVE, events[0]);
				eventMap.replace(eventCode | EventData.PVP, events[1]);
				announceEvent(eventCode | EventData.PVE, events[0]);
				announceEvent(eventCode | EventData.PVP, events[1]);
			}

			{ //Weekly
				if(DateUtils.getDayOfWeek(ms) == 1) {
					int eventCode = EventData.WEEKLY;
					EventData[] events = new EventData[] {generateEvent(eventCode |  EventData.PVE), generateEvent(eventCode | EventData.PVP)};
					eventMap.replace(eventCode | EventData.PVE, events[0]);
					eventMap.replace(eventCode | EventData.PVP, events[1]);
					announceEvent(eventCode | EventData.PVE, events[0]);
					announceEvent(eventCode | EventData.PVP, events[1]);
				}
			}

			{ //Monthly
				if(DateUtils.isFirstOfMonth(ms)) {
					int eventCode = EventData.MONTHLY;
					EventData[] events = new EventData[] {generateEvent(eventCode |  EventData.PVE), generateEvent(eventCode | EventData.PVP)};
					eventMap.replace(eventCode | EventData.PVE, events[0]);
					eventMap.replace(eventCode | EventData.PVP, events[1]);
					announceEvent(eventCode | EventData.PVE, events[0]);
					announceEvent(eventCode | EventData.PVP, events[1]);
				}
			}
		}
	}

	public static EventData generateEvent(int eventCode) {
		if((eventCode & EventData.PVE) == EventData.PVE) {
			if((eventCode & EventData.DAILY) == EventData.DAILY) return generateDailyPVE();
			//else if((eventCode & EventData.WEEKLY) == EventData.WEEKLY) return new generateWeeklyPVE();
			//else if((eventCode & EventData.MONTHLY) == EventData.MONTHLY) return new generateMonthlyPVE();
		} else if((eventCode & EventData.PVP) == EventData.PVP) {
			//if((eventCode & EventData.DAILY) == EventData.DAILY) return new generateDailyPVP();
			//else if((eventCode & EventData.WEEKLY) == EventData.WEEKLY) return new generateWeeklyPVP();
			//else if((eventCode & EventData.MONTHLY) == EventData.MONTHLY) return new generateWeeklyPVP();
		}
		return null;
	}

	private static EventData generateDailyPVE() {
		try {
			File folder = new File(DataUtils.getWorldDataPath() + "/pve/daily");
			//Pick a random file from the folder
			File[] files = folder.listFiles();
			assert files != null;
			File file = files[(int) (Math.random() * files.length)];
			//Deserialize the file
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			EventData eventData = (EventData) objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();
			return eventData;
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static void announceEvent(int eventCode, EventData eventData) {
		if(GameServer.getServerState() != null) {
			for(RegisteredClientOnServer client : GameServer.getServerState().getClients().values()) {
				PlayerData playerData = DataUtils.getPlayerDataByName(client.getPlayerName());
				if(playerData != null) {
					if(playerData.settings.getSubscribedEvents().contains(eventCode)) {
						try {
							client.serverMessage(eventData.getAnnouncement());
						} catch(IOException exception) {
							throw new RuntimeException(exception);
						}
					}
				}
			}
		}
	}

	/**
	 * Scales an event's difficulty based off the event itself and the squad data and creates a new instance of the event for the squad.
	 * <p>The difficulty is scaled based on the ratio between the recommended mass and the squad's current mass:</p>
	 * <ul>0.5x - 50% decrease in difficulty</ul>
	 * <ul>0.75x - 25% decrease in difficulty</ul>
	 * <ul>1.0x - 0% increase in difficulty</ul>
	 * <ul>1.5x - 50% increase in difficulty</ul>
	 * <ul>2.0x - 100% increase in difficulty</ul>
	 * <ul>3.0x - 200% increase in difficulty</ul>
	 * And so on...
	 * <p>After the modifier is determined, this is then translated into actual actions to be taken when the event starts.</p>
	 * <p>For example, if the modifier is 1.5x there might be more enemies spawned or the enemies spawned might be larger / tougher depending on the squad's composition.</p>
	 *
	 *
	 * @param eventData The event data.
	 * @param squadData The squad data.
	 * @return A new instance of the event data with the scaled difficulty for the squad.
	 */
	public static EventInstanceData scaleEvent(EventData eventData, SquadData squadData) {
		/*
		double totalMass = 0.0;
		for(SquadMemberData memberData : squadData.squadMembers) totalMass += memberData.shipMass;
		double averageMass = totalMass / squadData.squadMembers.size();
		double difficultyModifier = eventData.getBaseDifficulty();
		if(averageMass < eventData.getRecommendedMass()) { //Decrease difficulty
			if(averageMass < eventData.getRecommendedMass() / 2) difficultyModifier *= 0.5;
			else difficultyModifier *= 0.75;
		} else { //Increase difficulty
			difficultyModifier *= averageMass / eventData.getRecommendedMass();
		}

		if(!eventData.getEnemies().isEmpty()) {
			if(difficultyModifier < 0.5) {
				eventData.getToughestEnemy().setSpawnCount(0);
				eventData.getToughestEnemy().addModifier(EventEnemyData.CAPACITY_MODIFIER, 0.75);
				eventData.getToughestEnemy().addModifier(EventEnemyData.RECHARGE_MODIFIER, 0.75);
				eventData.getToughestEnemy().addModifier(EventEnemyData.DAMAGE_MODIFIER, 0.75);
			} else if(difficultyModifier < 0.75) {
				eventData.getToughestEnemy().addModifier(EventEnemyData.CAPACITY_MODIFIER, 0.85);
				eventData.getToughestEnemy().addModifier(EventEnemyData.RECHARGE_MODIFIER, 0.85);
				eventData.getToughestEnemy().addModifier(EventEnemyData.DAMAGE_MODIFIER, 0.85);
			}
		} else { //Probably a PvP event, so we'll just make the objective a bit harder / more time-consuming to complete.

		}
		temp disabled for 1.7.2 release
		 */
		//return new EventInstanceData(eventData, difficultyModifier);
		return null;
	}
}