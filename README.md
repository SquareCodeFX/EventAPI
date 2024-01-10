# Repository
https://repo.squarecode.de/
# Usage
##Events
An event derives from the abstract Event class:
```Java
public SampleEvent extends Event {
	// your event contents and logic
}
```
If the same event gets called on multiple occasions, it is recommended to implement ```Typed``` to differentiate the
different event origins. The interface provides a function returning an integer representing the event type. A developer
may decide to define integer constants for the different event types. Event handlers can be configured to only listen
to certain types of typed events.

##Handlers
An event handler is a public non-static method that is annotated with as EventHandler:
```Java
@EventHandler
public void onSampleEvent(final SampleEvent event) { … }
```
It may not define any additional parameters. The annotation ```EventHandler``` accepts an argument defining the
cashedListener’s priority. Priorities define the order, different handlers for the same event get executed. There are five
priorities (highest, high, normal, low, lowest) defined and the default priority is normal.

If the handler wishes only to listen for a certain type of a typed event this annotation can be added:
```Java
@EventHandler
@Typed(2)
public void onEvent(…) {…}
```
Where 2 is the event type and should, of course, be replaced with an integer constant defined somewhere else.