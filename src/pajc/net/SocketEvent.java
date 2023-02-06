package pajc.net;

import function.MultiConsumer;
import pajc.event.IEvent;

public enum SocketEvent implements IEvent {
	ILLEGAL_ARGUMENT (MultiConsumer.OfException.class),
	UNABLE_TO_CONNECT (MultiConsumer.OfException.class),
	CONNECTED (Runnable.class),
	DISCONNECTED (MultiConsumer.OfException.class),
	COMMAND_IN (MultiConsumer.OfString.class),
	MESSAGE_IN (MultiConsumer.OfString.class);
	
	Class<?> actionClass;
	
	private SocketEvent(Class<?> k) {
		actionClass = k;
	}
	
	
	@Override
	public Class<?> getActionClass() {return actionClass;}
	
}
