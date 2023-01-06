package thederpgamer.edencore.manager;

import api.common.GameServer;
import api.utils.StarRunnable;
import org.jdesktop.swingx.calendar.DateUtils;
import org.schema.game.common.data.player.PlayerState;
import org.schema.schine.network.RegisteredClientOnServer;
import thederpgamer.edencore.EdenCore;
import thederpgamer.edencore.data.event.EventData;
import thederpgamer.edencore.data.event.types.defense.DefenseEvent;
import thederpgamer.edencore.data.player.PlayerData;
import thederpgamer.edencore.utils.DataUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [10/13/2021]
 */
public class EventManager {

	private static final long EVENT_UPDATE_INTERVAL = 60000; //1 minute
	private static final ConcurrentHashMap<Integer, EventData> eventMap = new ConcurrentHashMap<>();

	/**
	 * Runs the date and time checkers for all event intervals and generates new ones when needed.
	 * <p>For Example, if this is run on a sunday between 12:00 AM - 1:00 AM it will generate a new weekly and a new daily event,
	 * and even a new monthly one if it's the first week of the month.</p>
	 */
	public static void doRunners() {
		startEventChecker();
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

	public static void forceGen() {
		{ //Daily
			int eventCode = EventData.DAILY;
			EventData[] events = new EventData[] {generateEvent(eventCode |  EventData.PVE), generateEvent(eventCode | EventData.PVP)};
			eventMap.replace(eventCode | EventData.PVE, events[0]);
			eventMap.replace(eventCode | EventData.PVP, events[1]);
			announceEvent(eventCode | EventData.PVE, events[0]);
			announceEvent(eventCode | EventData.PVP, events[1]);
		}

		{ //Weekly
			int eventCode = EventData.WEEKLY;
			EventData[] events = new EventData[] {generateEvent(eventCode |  EventData.PVE), generateEvent(eventCode | EventData.PVP)};
			eventMap.replace(eventCode | EventData.PVE, events[0]);
			eventMap.replace(eventCode | EventData.PVP, events[1]);
			announceEvent(eventCode | EventData.PVE, events[0]);
			announceEvent(eventCode | EventData.PVP, events[1]);
		}

		{ //Monthly
			int eventCode = EventData.MONTHLY;
			EventData[] events = new EventData[] {generateEvent(eventCode |  EventData.PVE), generateEvent(eventCode | EventData.PVP)};
			eventMap.replace(eventCode | EventData.PVE, events[0]);
			eventMap.replace(eventCode | EventData.PVP, events[1]);
			announceEvent(eventCode | EventData.PVE, events[0]);
			announceEvent(eventCode | EventData.PVP, events[1]);
		}
	}

	private static void startEventChecker() {
		try {
			new StarRunnable() {
				@Override
				public void run() {
					for(EventData eventData : getAllEvents()) {
						if(eventData.getSquadData().ready() && eventData.waitingTime <= 0) {
							eventData.start();
							announceEvent(eventData.getCode(), eventData);
						} else eventData.waitingTime --;
					}
				}
			}.runTimer(EdenCore.getInstance(), EVENT_UPDATE_INTERVAL);
		} catch(Exception exception) {
			exception.printStackTrace();
			startEventChecker();
		}
	}

	public static ArrayList<EventData> getAllEvents() {
		return new ArrayList<>(eventMap.values());
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
		return EventData.createRandom(EventData.DAILY | EventData.PVE);
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

	public static void startEventEditor(PlayerState sender, EventData eventData) {
		//TODO
	}

	public static EventData getEventByName(String arg) {
		for(EventData eventData : getAllEvents()) {
			if(eventData.getName().equalsIgnoreCase(arg)) return eventData;
		}
		return null;
	}

	public static EventData createEvent(String type, String combatType, String name) {
		if("DEFENSE".equals(type.toUpperCase())) {
			return DefenseEvent.create(combatType, name);
		}
		return null;
	}
}