package tetris;

/**
 * Any kind of controller should implements this interface.
 */
public interface IController {
	
	void reset();
	void start();
	void keyPressed(int keyCode);
}
