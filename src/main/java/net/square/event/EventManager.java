package net.square.event;

import net.square.event.exceptions.EventDispatchException;
import net.square.event.exceptions.EventRegisterException;
import net.square.event.listeners.EventHandler;
import net.square.event.listeners.Listener;
import net.square.event.listeners.ListenerPriority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registers and manages listeners and handles event dispatching
 */
@SuppressWarnings("unused")
public final class EventManager {

    private static final HashMap<Class<? extends AbstractEvent>,
        CopyOnWriteArrayList<TransformedListener>> registeredListeners = new HashMap<>();

    private EventManager() {
    }

    public static void registerListeners(final Object listenerClassInstance) {
        Class<?> aClass = listenerClassInstance.getClass();

        if (!Listener.class.isAssignableFrom(aClass)) {
            throw new EventRegisterException("Illegal class: " + aClass.getSimpleName() +
                                             ": Class is not implements Listener.class (required: 1)");
        }

        for (Method method : aClass.getMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) {
                continue;
            }

            // illegal parameter count
            if (method.getParameterCount() != 1) {
                throw new EventRegisterException("Illegal event handler: " + method.getName() +
                                                 ": Wrong number of arguments (required: 1)");
            }

            // illegal parameter
            if (!AbstractEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
                throw new EventRegisterException(
                    "Illegal event handler: " + method.getName() + ": Argument must extend " +
                    AbstractEvent.class.getName());
            }

            @SuppressWarnings("unchecked") Class<? extends AbstractEvent> eventType =
                (Class<? extends AbstractEvent>) method.getParameterTypes()[0];

            ListenerPriority priority = method.getAnnotation(EventHandler.class).priority();

            TransformedListener cashedListener = new TransformedListener(listenerClassInstance, method, priority);
            addListener(eventType, cashedListener);
        }
    }

    private static void addListener(final Class<? extends AbstractEvent> eventType,
                                    final TransformedListener cashedListener) {

        if (!registeredListeners.containsKey(eventType)) {
            registeredListeners.put(eventType, new CopyOnWriteArrayList<>());
        }

        registeredListeners.get(eventType).add(cashedListener);
    }

    public static void unregisterListeners(final Object listenerClassInstance) {
        for (CopyOnWriteArrayList<TransformedListener> cashedListenerList : registeredListeners.values()) {
            for (int i = 0; i < cashedListenerList.size(); i++) {
                if (cashedListenerList.get(i).listenerClassInstance() == listenerClassInstance) {
                    cashedListenerList.remove(i);
                    i -= 1;
                }
            }
        }
    }

    public static void unregisterListenersOfEvent(final Class<? extends AbstractEvent> eventClass) {
        registeredListeners.get(eventClass).clear();
    }

    public static void callEvent(final AbstractEvent event) {
        for (ListenerPriority value : ListenerPriority.values()) {
            dispatchEvent(event, value);
        }
    }

    private static void dispatchEvent(final AbstractEvent event, final ListenerPriority priority) {
        CopyOnWriteArrayList<TransformedListener> cashedListeners = registeredListeners.get(event.getClass());
        if (cashedListeners != null) {
            for (TransformedListener cashedListener : cashedListeners) {
                if (cashedListener.priority() == priority) {
                    try {
                        cashedListener.listenerMethod().setAccessible(true);
                        cashedListener.listenerMethod().invoke(cashedListener.listenerClassInstance(), event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new EventDispatchException("Cant dispatch given event, cause: " + e.getMessage());
                    }
                }
            }
        }
    }
}
