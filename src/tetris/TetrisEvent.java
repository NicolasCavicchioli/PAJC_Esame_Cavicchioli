package tetris;

import java.util.function.IntConsumer;
import function.MultiConsumer;
import pajc.event.IEvent;

public enum TetrisEvent implements IEvent {
	COUNTDOWN (IntConsumer.class),
	ROW_REMOVED (IntConsumer.class),
	BOARD_CLEAR,
	GAME_OVER,
	REPAINT,
	SMALL_REPAINT,
	BIG_REPAINT,
	JSON (MultiConsumer.OfString.class)
	;
	
	Class<?> actionClass;
	
	private TetrisEvent(Class<?> k) {
		actionClass = k;
	}
	private TetrisEvent() {
		this(Runnable.class);
	}
	
	
	@Override
	public Class<?> getActionClass() {return actionClass;}
	
}
