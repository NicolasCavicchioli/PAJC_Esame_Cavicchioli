package tetris.single;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.function.IntPredicate;
import javax.swing.Timer;
import pajc.event.IHasEvents;
import pajc.event.MyEventHandler;
import tetris.*;

/**
 * The base Model of the whole game.
 * @see TetrisEvent
 */
public final class TetrisModel implements IModel, IHasEvents<TetrisEvent> {
	
	public static final int mapWidth=10, mapHeight=20;
	public static final int EMPTY_CELL = 0;
	
	public final int[][] map = new int[mapHeight][mapWidth];
	public final Piece current, next;
	public int ghostY;
	private final int spawnX = 5;
	private Timer timer = new Timer(800, e->update());
	private final MyEventHandler<TetrisEvent> events = new MyEventHandler<>();
	private final JSONManager jsonManager = new JSONManager(this);
	
	@Override
	public MyEventHandler<TetrisEvent> getEventHandler() {return events;}
	
	
	public TetrisModel() {
		current = new Piece(Tetromino.random()).setX(spawnX);
		next = new Piece(Tetromino.randomExcept(current.type));
		timer.setInitialDelay(400);
		
		when(TetrisEvent.REPAINT, ()->triggerEvent(TetrisEvent.JSON, toJSON()));
		
		updateGhost();
	}
	
	
	public void update() {
		if (canSoftDrop()) {
			triggerEvent(TetrisEvent.REPAINT);
			return;
		}
		
		if (isGameOver()) {
			gameOver();
			return;
		}
		
		lockPiece();
		nextPiece();
		updateGhost();
		
	}
	
	public void show(Graphics2D g) {
		scale(g);
		showBackground(g);
		showGround(g);
		current.showGhost(ghostY, g);
		current.show(g);
	}
	
	public static void scale(Graphics2D g) {
		g.scale(32, 32);
	}
	public static void showBackground(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 2, mapWidth, mapHeight-2);
	}
	private void showGround(Graphics2D g) {
		for (int y=mapHeight-1; y>=0; --y) {
			boolean emptyRow = true;
			for (int x=0; x<mapWidth; ++x) {
				if (map[y][x]!=EMPTY_CELL) {
					emptyRow=false;
					g.drawImage(Assets.images[map[y][x]], x, y, 1, 1, null);
				}
			}
			if (emptyRow) break;
		}
	}
	
	public void keyPressed(int keyCode) {
		if (!isRunning()) return;
		
		switch(keyCode) {
		case 'A': if (!applyIfPieceFit(-1, 0 ,0)) return; break;
		case 'D': if (!applyIfPieceFit( 1, 0, 0)) return; break;
		case 'S': if (!canSoftDrop()) return; break;
		case 'Q': if (!tryRotate(-1)) return; break;
		case 'E': if (!tryRotate(+1)) return; break;
		case ' ': case 'W': hardDrop(); break;
		default: return;
		}
		
		updateGhost();
		triggerEvent(TetrisEvent.REPAINT);
	}
	
	private void updateGhost() {
		ghostY = current.y;
		while (checkIfTypeFit(current.type, current.x, ghostY+1, current.r)) ghostY+=1;
		triggerEvent(TetrisEvent.REPAINT);
	}
	
	private boolean canSoftDrop() {
		return applyIfPieceFit(0, 1, 0);
	}
	
	private boolean isGameOver() {
		return !checkIfPieceFit(0, 0, 0)
			|| current.any((int x, int y)->y<2);
	}

	private boolean tryRotate(int dr) {
		return applyIfPieceFit(  0, 0, dr)
			|| applyIfPieceFit(  0, 1, dr)// I
			|| applyIfPieceFit(  0, 2, dr)// I
			|| applyIfPieceFit(  1, 0, dr)
			|| applyIfPieceFit( -1, 0, dr)
			|| applyIfPieceFit(  2, 0, dr, Tetromino.I)
			|| applyIfPieceFit( -2, 0, dr, Tetromino.I)
			|| applyIfPieceFit(-dr, 2, dr, 1) // T SPIN
			|| applyIfPieceFit(-dr, 1, dr, 1) // T SPIN
			|| applyIfPieceFit( dr, 2, dr*2, 1)// JL SPIN
			|| applyIfPieceFit(  0, 0, dr*2, 1);// L
	}
	
	private boolean applyIfPieceFit(int dx, int dy, int dr, Tetromino t) {
		return current.type==t && applyIfPieceFit(dx, dy, dr);
	}
	private boolean applyIfPieceFit(int dx, int dy, int dr, int dt) {
		if (!applyIfPieceFit(dx, dy, dr)) return false;
		timer.restart();
		return true;
	}
	private boolean applyIfPieceFit(int dx, int dy, int dr) {
		if (!checkIfPieceFit(dx, dy, dr)) return false;
		current.applyDeltas (dx, dy, dr);
		return true;
	}
	
	private boolean checkIfPieceFit(int dx, int dy, int dr) {
		return checkIfTypeFit(current.type, current.x+dx, current.y+dy, current.getRotated(dr));
	}
	private boolean checkIfTypeFit(Tetromino type, int x, int y, int r) {
		return !type.anyBlock(x, y, r, (u,v) -> isOutBound(u, v) || map[v][u]!=EMPTY_CELL);
	}
	private boolean isOutBound(int u, int v) {
		return u<0 || v<0 || u>=mapWidth || v>=mapHeight;
	}
	
	
	private void hardDrop() {
		if (isRunning() && current.y<ghostY) timer.restart();
		current.y = ghostY;
	}
	
	
	private void lockPiece() {
		HashSet<Integer> placedY = new HashSet<>(4);
		
		current.forEachBlock((u,v) -> {
			map[v][u] = current.type.imageIndex;
			placedY.add(v);
		});
		
		int maxFullRow = findFullRows(placedY);
		if (maxFullRow!=-1) {
			removeFullRows(maxFullRow, placedY.size());
		}
		
	}
	private int findFullRows(HashSet<Integer> set) {
		int[] maxFullRows = new int[1];
		
		set.removeIf(y -> {
			if (anyRow(x -> map[y][x]==EMPTY_CELL)) return true;
			if (y>maxFullRows[0]) maxFullRows[0]=y;
			return false;
		});
		
		return set.isEmpty() ? -1 : maxFullRows[0];
	}
	private void removeFullRows(int maxFullRow, int size) {
		boolean boardClear = true;
		
		for (int x=0,y; x<mapWidth; ++x) {
			for (y=maxFullRow; y>size; --y) {
				map[y][x] = map[y-size][x];
				if (map[y-size][x]!=EMPTY_CELL) boardClear=false;
			}
			for (; y>=0; --y) map[y][x]=EMPTY_CELL;
		}
		
		ghostY += size;
		
		triggerEvent(TetrisEvent.ROW_REMOVED, size);
		triggerEvent(TetrisEvent.REPAINT);
		if (boardClear) {
			triggerEvent(TetrisEvent.BOARD_CLEAR);
		}
		
	}
	
	public void nextPiece() {
		current.set(next).setX(spawnX);
		next.randomize();
	}
	
	
	private boolean anyRow(IntPredicate test) {
		for (int x=0; x<mapWidth; ++x) {
			if (test.test(x)) return true;
		}
		return false;
	}
	
	private void gameOver() {
		timer.stop();
		triggerEvent(TetrisEvent.GAME_OVER);
	}
	
	public void reset() {
		for (int y=mapHeight-1; y>=0; --y) {
			boolean rowEmpty = true;
			for (int x=0; x<mapWidth; ++x) {
				if (map[y][x]!=EMPTY_CELL) {
					rowEmpty=false;
					map[y][x]=EMPTY_CELL;
				}
			}
			if (rowEmpty) break;
		}
		
		current.randomize().setX(spawnX);
		next.randomize();
		updateGhost();
	}
	
	public void setNextPiece(Tetromino type) {
		next.type = type;
		triggerEvent(TetrisEvent.REPAINT);
	}
	
	public void addPartialRows(int size) {
		int r = (int)(Math.random() * mapWidth);
		for (int x=0,y; x<mapWidth; ++x) {
			for (y=0; y<mapHeight-size; ++y) {
				map[y][x] = map[y+size][x];
			}
			for (;y<mapHeight; ++y) {
				map[y][x] = x==r? EMPTY_CELL : Assets.XBlockImageIndex;
			}
		}
		ghostY -= size;
		while (!checkIfPieceFit(0, 0, 0)) current.y-=1;
		triggerEvent(TetrisEvent.REPAINT);
	}
	
	@Override
	public void start() {
		timer.start();
	}
	@Override
	public void stop() {
		timer.stop();
	}
	@Override
	public boolean isRunning() {
		return timer.isRunning();
	}
	
	public String toJSON() {
		return jsonManager.toJSON();
	}
	
}
