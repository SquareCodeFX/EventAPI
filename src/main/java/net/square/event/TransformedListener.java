package net.square.event;

import net.square.event.listeners.ListenerPriority;

import java.lang.reflect.Method;

public record TransformedListener(Object listenerClassInstance, Method listenerMethod, ListenerPriority priority) {

}
