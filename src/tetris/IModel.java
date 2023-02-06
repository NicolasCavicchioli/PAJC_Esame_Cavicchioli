package tetris;

public interface IModel {
	void start();
	void stop();
	void reset();
	boolean isRunning();
	
	default boolean isNotRunning() {
		return !isRunning();
	}
	
}
