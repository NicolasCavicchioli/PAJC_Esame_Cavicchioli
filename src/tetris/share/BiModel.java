package tetris.share;

import pajc.event.IHasEvents;
import pajc.event.MyEventHandler;
import tetris.IModel;
import tetris.TetrisEvent;
import tetris.single.TetrisModel;

/**
 * Class that handle two {@link TetrisModel} at the same time.
 */
public class BiModel implements IModel, IHasEvents<TetrisEvent> {
	
	final MyEventHandler<TetrisEvent> events = new MyEventHandler<>();
	public final TetrisModel left, right;
	
	@Override
	public MyEventHandler<TetrisEvent> getEventHandler() {return events;}
	
	public TetrisModel get(int n) {return n==0? left : n==1? right : null;}
	
	
	public BiModel(TetrisModel left, TetrisModel right) {
		this.left = left;
		this.right = right;
		
		this.when(TetrisEvent.GAME_OVER, this::stop);
		
		left.when(TetrisEvent.REPAINT, ()->triggerEvent(TetrisEvent.REPAINT))
		.when(TetrisEvent.GAME_OVER, ()->triggerEvent(TetrisEvent.GAME_OVER));
		
		right.when(TetrisEvent.REPAINT, ()->triggerEvent(TetrisEvent.REPAINT))
		.when(TetrisEvent.GAME_OVER, ()->triggerEvent(TetrisEvent.GAME_OVER));
		
	}
	public BiModel() {
		this(new TetrisModel(), new TetrisModel());
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
