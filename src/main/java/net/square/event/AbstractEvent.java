package net.square.event;

@SuppressWarnings("unused")
public abstract class AbstractEvent {

    private boolean cancelled = false;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
