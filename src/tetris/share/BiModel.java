package tetris.share;

import pajc.event.IHasEvents;
import pajc.event.MyEventHandler;
import tetris.IModel;
import tetris.TetrisEvent;
import tetris.single.TetrisModel;

public class BiModel implements IModel, IHasEvents<TetrisEvent> {
	
	private static final String LEFT_KEYS  = "ASDQWE";
	private static final String RIGHT_KEYS = "JKLUIO";
	final MyEventHandler<TetrisEvent> events = new MyEventHandler<>();
	public final TetrisModel left, right;
	
	@Override
	public MyEventHandler<TetrisEvent> getEventHandler() {return events;}
	
	public TetrisModel get(int n) {return n==0? left : n==1? right : null;}
	
	
	public BiModel(TetrisModel left, TetrisModel right) {
		this.left = left;
		this.right = right;
		
		left.when (TetrisEvent.GAME_OVER, this::stop);
		right.when(TetrisEvent.GAME_OVER, this::stop);
		left.when (TetrisEvent.REPAINT, ()->this.triggerEvent(TetrisEvent.REPAINT));
		right.when(TetrisEvent.REPAINT, ()->this.triggerEvent(TetrisEvent.REPAINT));
		
	}
	public BiModel() {
		this(new TetrisModel(), new TetrisModel());
	}
	
	
	public void keyPressed(int keyCode) {
		int i;
		if ((i=RIGHT_KEYS.indexOf(keyCode)) != -1) {
			right.keyPressed(LEFT_KEYS.charAt(i));
		} else if (LEFT_KEYS.indexOf(keyCode) != -1) {
			left.keyPressed(keyCode);
		}
	
	}
	
	@Override
	public void reset() {
		left.reset();
		right.reset();
	}
	@Override
	public void start() {
		left.start();
		right.start();
	}
	@Override
	public void stop() {
		left.stop();
		right.stop();
	}
	
	@Override
	public boolean isRunning() {
		return left.isRunning();
	}
	
}
