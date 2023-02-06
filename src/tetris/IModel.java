package tetris;

/**
 * Any kind of model should implements this interface
 * so that controllers can handle time-related actions
 * without the models unnecessarily exposing their inner attributes. 
 */
public interface IModel {
	
	void start();
	void stop();
	void reset();
	boolean isRunning();
	
	default boolean isNotRunning() {
		return !isRunning();
	}
	
}
