package pajc.event;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import function.MultiConsumer;
import static java.util.Objects.requireNonNull;

public interface IHasEvents<Event extends IEvent> {
	
	MyEventHandler<Event> getEventHandler();
	
	
	default IHasEvents<Event> when(Event event, Runnable action) {
		requireAssignable(event, action);
		getEventHandler().registerAction(event, MultiConsumer.from(action));
		return this;
	}
	default IHasEvents<Event> when(Event event, IntConsumer action) {
		requireAssignable(event, action);
		getEventHandler().registerAction(event, MultiConsumer.from(action));
		return this;
	}
	default  IHasEvents<Event> when(Event event, MultiConsumer.OfString action) {
		requireAssignable(event, action);
		getEventHandler().registerAction(event, MultiConsumer.from(action));
		return this;
	}
	default IHasEvents<Event> when(Event event, MultiConsumer.OfException action) {
		requireAssignable(event, action);
		getEventHandler().registerAction(event, MultiConsumer.from(action));
		return this;
	}
	default <T> IHasEvents<Event> when(Event event, Consumer<T> action) {
		requireAssignable(event, action);
		getEventHandler().registerAction(event, MultiConsumer.from(action));
		return this;
	}
	
	
	default void triggerEvent(Event event, Object...args) {
		getEventHandler().trigger(event, args);
	}
	
	
	private void requireAssignable(Event event, Object action) {
		requireNonNull(event);
		requireNonNull(action);
		if (event.getActionClass().isAssignableFrom(getInterface(action))) return;
		
		String s = "%s tried to register a %s event with a %s, %nbut it require an action of type %s"
				.formatted(this.getClass(),
						event,
						getInterface(action).getSimpleName(),
						event.getActionClass());
		throw new IllegalArgumentException(s);
	}
	private Class<?> getInterface(Object action) {
		return action.getClass().getInterfaces()[0];
	}
	
}
