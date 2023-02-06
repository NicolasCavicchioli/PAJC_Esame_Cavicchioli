package tetris;

import static pajc.PAJC.half;
import java.awt.Graphics2D;
import function.IntBiConsumer;
import function.IntBiPredicate;

public class Piece {
	
	public int x, y, r;
	public Tetromino type;
	
	public Piece(Tetromino s) {
		setType(s);
	}
	
	public Piece randomize() {
		setType(Tetromino.randomExcept(type));
		return this;
	}
	public Piece setX(int x) {
		this.x = x;
		return this;
	}
	public Piece set(Piece other) {
		type = other.type;
		x = other.x;
		y = other.y;
		r = other.r;
		return this;
	}
	public void setType(Tetromino type) {
		this.type = type;
		x = y = half(type.size);
	}
	
	
	public void show(Graphics2D g) {
		type.show(x, y, r, g);
	}
	
	public void showGhost(int ghostY, Graphics2D g) {
		type.showGhost(x, ghostY, r, g);
	}
	
	public int getRotated(int dr) {
		return (r+dr+type.maxRotate) % type.maxRotate;
	}
	public void rotate(int dr) {
		if (dr!=0) r=getRotated(dr);
	}
	
	public void forEachBlock(IntBiConsumer action) {
		type.forEachBlock(x, y, r, action);
	}
	public boolean any(IntBiPredicate test) {
		return type.anyBlock(x, y, r, test);
	}
	
	public void applyDeltas(int dx, int dy, int dr) {
		x+=dx;
		y+=dy;
		rotate(dr);
	}
	
	public String toJSON() {
		return "Piece:{,%s,%d,%d,%d,}".formatted(type, x, y, r);
	}
	
	public static Object[] fromJSON(String json, Object[] out) {
		if (out==null) out = new Object[4];
		var ss = json.split(",");
		out[0] = ss[1]; // type
		out[1] = Integer.parseInt(ss[2]); // x
		out[2] = Integer.parseInt(ss[3]); // y
		out[3] = Integer.parseInt(ss[4]); // r
		return out;
	}
	
	public static void paintPieceAndGhostFromJSON(String json, int ghostY, Graphics2D g) {
		var pieceData = fromJSON(json, null);
		var type = Tetromino.valueOf((String)pieceData[0]);
		int x = (int)pieceData[1];
		int y = (int)pieceData[2];
		int r = (int)pieceData[3];
		type.showGhost(x, ghostY, r, g);
		type.show(x, y, r, g);
	}
	
}
