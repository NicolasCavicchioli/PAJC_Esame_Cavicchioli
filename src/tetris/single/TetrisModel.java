package tetris.single;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.function.Predicate;
import javax.swing.Timer;

import pajc.event.IHasEvents;
import pajc.event.MyEventHandler;
import tetris.Assets;
import tetris.IModel;
import tetris.Piece;
import tetris.TetrisEvent;
import tetris.Tetromino;

public final class TetrisModel implements IModel, IHasEvents<TetrisEvent> {
	
	public static final int mapWidth=10, mapHeight=20;
	private static final int EMPTY_CELL = 0;
	
	public final int[][] map = new int[mapHeight][mapWidth];
	private final int spawnX = 5;
	private Timer timer;
	private final MyEventHandler<TetrisEvent> events = new MyEventHandler<>();
	public final Piece current;
	public final Piece next;
	public int ghostY;
	
	@Override
	public MyEventHandler<TetrisEvent> getEventHandler() {return events;}
	
	
	public TetrisModel() {
		current = new Piece(Tetromino.random()).setX(spawnX);
		next = new Piece(Tetromino.randomExcept(current.type));
		timer = new Timer(800, e->update());
		timer.setInitialDelay(400);
		
		
		when(TetrisEvent.SMALL_REPAINT, () -> {
			triggerEvent(TetrisEvent.JSON, getShortJSON());
		 	triggerEvent(TetrisEvent.REPAINT);
		})
		.when(TetrisEvent.BIG_REPAINT, () -> {
			triggerEvent(TetrisEvent.REPAINT);
		});
		
		updateGhost();
	}
	
	
	public void update() {
		if (canSoftDrop()) return;
		
		if (isGameOver()) {
			gameOver();
			return;
		}
		
		lockPiece();
		nextPiece();
		updateGhost();
		events.trigger(TetrisEvent.BIG_REPAINT);
		events.trigger(TetrisEvent.REPAINT);
		
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
		triggerEvent(TetrisEvent.SMALL_REPAINT);
	}
	
	private void updateGhost() {
		ghostY = current.y;
		while (checkIfTypeFit(current.type, current.x, ghostY+1, current.r)) ghostY+=1;
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
			|| applyIfPieceFit(  0, 1, dr)// I help
			|| applyIfPieceFit(  0, 2, dr)// I help
			|| applyIfPieceFit(  1, 0, dr)
			|| applyIfPieceFit( -1, 0, dr)
			|| applyIfPieceFit(  2, 0, dr, Tetromino.I)
			|| applyIfPieceFit( -2, 0, dr, Tetromino.I)
			|| applyIfPieceFit(-dr, 2, dr, 1) // T SPIN
			|| applyIfPieceFit(-dr, 1, dr, 1) // T SPIN
			|| applyIfPieceFit( dr, 2, dr*2, 1)// JL SPIN
			|| applyIfPieceFit(  0, 0, dr*2, 1)// L
			;
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
		if (timer.isRunning() && current.y<ghostY) timer.restart();
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
		triggerEvent(TetrisEvent.BIG_REPAINT);
		if (boardClear) {
			triggerEvent(TetrisEvent.BOARD_CLEAR);
		}
		
	}
	
	public void nextPiece() {
		current.set(next).setX(spawnX);
		next.randomize();
	}
	
	
	private boolean anyRow(Predicate<Integer> test) {
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
		triggerEvent(TetrisEvent.BIG_REPAINT);
	}
	
	public void setNextPiece(Tetromino type) {
		next.type = type;
		triggerEvent(TetrisEvent.SMALL_REPAINT);
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
		triggerEvent(TetrisEvent.BIG_REPAINT);
	}
	
	public void start() {
		timer.start();
	}
	public void stop() {
		timer.stop();
	}
	@Override
	public boolean isRunning() {
		return timer.isRunning();
	}
	
	
	public String getShortJSON() {
		return "S.%s.g:%d.n:%s".formatted(current.toJSON(), ghostY, next.type);
	}
	public String getLongJSON() {
		StringBuffer sb = new StringBuffer(getShortJSON());
		sb.replace(0, 1, "L");
		sb.append(".map:");
		
		for (int y=mapHeight-1; y>=0; --y) {
			StringBuffer row = new StringBuffer();
			boolean noMoreBlocks = true;
			for (int x=mapWidth-1,v; x>=0; --x) {
				v = map[y][x];
				if (v!=EMPTY_CELL) noMoreBlocks = false;
				row.append(v);
			}
			if (noMoreBlocks) break;
			sb.append(row.toString());
		}
		
		return sb.toString();
	}
	public static Object[] fromJSON(String json, Object[] out) {
		if (out==null) {
		    out = new Object[]{null,null,null,""};
		}
		var splitted = json.split("\\.");
		
		out[0] = splitted[1].substring(6); // current
		out[1] = Integer.parseInt( splitted[2].substring(2) ); // ghost
		out[2] = splitted[3].substring(2); // next
		if (splitted[0].equals("L")) out[3] = splitted[4].substring(4); // map
		
		return out;
	}
	public static void paintFromObject(Object[] modelData, Graphics2D g) {
		String map = (String)modelData[3];
		String empty = EMPTY_CELL+"";
		for (int x,y, i=0, n=map.length(); i<n; ++i) {
			String c = map.substring(i,i+1);
			if (c.equals(empty)) continue;
			x = mapWidth-1 - i % mapWidth;
			y = mapHeight-1 - i / mapWidth;
			g.drawImage(Assets.images[Integer.parseInt(c)],
					x, y, 1, 1, null);
		}
		Piece.paintPieceAndGhostFromJSON((String)modelData[0], (int)modelData[1], g);
	}
	
	public static Tetromino getTypeFromJSON(String json) {
		return Tetromino.valueOf(json.split("\\.")[3].substring(2));
	}
	
}
