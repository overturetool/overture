package org.overture.ide.plugins.showtraceNextGen.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

public class TraceEventManager {
	private final TreeMap<Long, ArrayList<INextGenEvent>> events;
	private Long currentEventTime;
	
	public TraceEventManager(NextGenRTLogger logger) {
		this.events = (TreeMap<Long, ArrayList<INextGenEvent>>) logger.getEvents();
	}
	
	/**
	 * Get events from a specified start time. If no events at specified time the next series of 
	 * events is returned
	 * @param startTime time to start searching for events
	 * @return list of all events at this or the next event time
	 */
	public List<INextGenEvent> getEvents(Long startTime) {
		ArrayList<INextGenEvent> eventList = null;
		
		//Key events where key is equal to or greater (null if nothing is found)
		Long eventKey = events.ceilingKey(startTime); 
		
	    if(eventKey != null) {
	    	eventList = events.get(eventKey);
	    	currentEventTime = eventKey;
	    } else {
	    	eventList = new ArrayList<INextGenEvent>();
	    }
				
		return eventList;
	}
	
	/**
	 * Get events from the last referenced event time 
	 * @return List of events at the next referenced event time
	 */
	public List<INextGenEvent> getEvents() {
		return getEvents(getCurrentEventTime() + 1);
	}
	
	/**
	 * Get a list of all event times
	 * @return List of times
	 */
	public List<Long> getEventTimes() {
		return new ArrayList<Long>(events.keySet());
	}
	
	/**
	 * get the current event time
	 * @return Event time
	 */
	public Long getCurrentEventTime() {
		return currentEventTime;
	}
	
	/**
	 * Get time of last event group
	 * @return Event time
	 */
	public Long getLastEventTime() {
		return events.lastKey();
	}
	
	/**
	 * Reset the internal state
	 */
	public void reset() {
		currentEventTime = null;
	}
	
	

}
