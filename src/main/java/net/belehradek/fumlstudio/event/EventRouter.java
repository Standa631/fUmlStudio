package net.belehradek.fumlstudio.event;

import java.util.HashMap;
import java.util.Map;

public class EventRouter {
    private static Map<Object, EventHandler> eventHandlerMap = new HashMap<>();

    public static void sendEvent(Object eventObject, Event event) {
    	System.out.println("Send event: " + eventObject + " " + event);
    	if (eventHandlerMap.containsKey(eventObject))
    		eventHandlerMap.get(eventObject).handle(event);
    }

    public static void registerHandler (Object eventObject, EventHandler handler) {
    	System.out.println("Register handler: " + eventObject + " " + handler);
        eventHandlerMap.put(eventObject, handler);
    }
}
