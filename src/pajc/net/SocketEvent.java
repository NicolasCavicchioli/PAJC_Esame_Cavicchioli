package pajc.net;

import function.ExceptionConsumer;
import function.StringConsumer;
import pajc.event.IEvent;

/**
 * enum with all the {@link MySocket}-related events.
 * @see IEvent
 */
public enum SocketEvent implements IEvent {
	ILLEGAL_ARGUMENT  (ExceptionConsumer.class),
	UNABLE_TO_CONNECT (ExceptionConsumer.class),
	CONNECTED         (Runnable.class),
	DISCONNECTED      (ExceptionConsumer.class),
	COMMAND_IN        (StringConsumer.class),
	MESSAGE_IN        (StringConsumer.class);
	
	Class<?> actionClass;
	
	private SocketEvent(Class<?> k) {
		actionClass = k;
	}
	
	
	@Override
	public Class<?> getActionClass() {return actionClass;}
	
}
