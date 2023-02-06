package pajc.event;

/**
 * Any kind of event <b>must</b> implements this interface.
 * <p>
 * Each event is associated with a {@code Class<?>} attribute
 * to indicate which type of consumer they use as action.
 * 
 * @see #getActionClass()
 * @see MultiConsumer
 */
public interface IEvent {
	
	/**
	 * Return the type of the action related to this event.
	 * @return the type of the action related to this event
	 */
	Class<?> getActionClass();
	
}