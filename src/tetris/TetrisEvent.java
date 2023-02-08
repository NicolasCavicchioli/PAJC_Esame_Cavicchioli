package tetris;

import java.util.function.IntConsumer;
import function.StringConsumer;
import pajc.event.IEvent;
import tetris.single.TetrisModel;

/**
 * enum with all the {@link IEvent} related to the game Tetris
 * that {@link TetrisModel} can trigger.
 * 
 * @see IEvent
 */
public enum TetrisEvent implements IEvent {
	COUNTDOWN   (IntConsumer.class),
	ROW_REMOVED (IntConsumer.class),
	BOARD_CLEAR (Runnable.class),
	REPAINT     (Runnable.class),
	JSON        (StringConsumer.class),
	GAME_OVER   (Runnable.class);
	
	Class<?> actionClass;
	
	private TetrisEvent(Class<?> k) {
		actionClass = k;
	}
	
	@Override
	public Class<?> getActionClass() {return actionClass;}
	
}
