package net.square.event;

import net.square.event.listeners.ListenerPriority;

import java.lang.reflect.Method;

/**
 * Represents a transformed event listener.
 */
public record TransformedListener(Object listenerClassInstance, Method listenerMethod, ListenerPriority priority) {

}
