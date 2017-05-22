package net.belehradek.fumlstudio.event;

import java.util.HashMap;
import java.util.Map;

import net.belehradek.Global;
import net.belehradek.fumlstudio.controller.ProjectTreeController;

public class EventRouter {
    private static Map<Object, EventHandler> eventHandlerMap = new HashMap<>();

    public static void sendEvent(Object eventObject, Event event) {
    	Global.log("Send event: " + eventObject + " " + event);
    	if (eventHandlerMap.containsKey(eventObject)) {
    		EventHandler<Event> e = eventHandlerMap.get(eventObject);
    		e.handle(event);
    	}
    }

    public static void registerHandler (Object eventObject, EventHandler handler) {
    	Global.log("Register handler: " + eventObject + " " + handler);
        eventHandlerMap.put(eventObject, handler);
    }
}
