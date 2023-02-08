package tetris;

import static pajc.PAJC.half;
import java.awt.Graphics2D;
import function.IntBiConsumer;
import function.IntBiPredicate;

/**
 * The {@code Piece} class represents a {@link Tetromino}
 * that can change it's position an rotation over time. 
 */
public class Piece {
	
	public int x, y, r;
	public Tetromino type;
	
	public Piece(Tetromino s) {
		setType(s);
	}
	
	public Piece randomize() {
		setType(Tetromino.randomExcept(this.type));
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
	
	/**
	 * Apply the specified action for each block of this type
	 * with this position and rotation.
	 * 
	 * @param action the action to be performed for each block
	 */
	public void forEachBlock(IntBiConsumer action) {
		type.forEachBlock(x, y, r, action);
	}
	/**
	 * Return whether any blocks of this type with this
	 * position and rotation verifies the specified predicate.
	 * 
	 * @param test the predicate to be verified
	 * @return {@code true} if any block of this type in this
	 *         position and rotation verifies the specified
	 *         predicate, {@code false} otherwise
	 */
	public boolean anyBlock(IntBiPredicate test) {
		return type.anyBlock(x, y, r, test);
	}
	
	public void applyDeltas(int dx, int dy, int dr) {
		x+=dx;
		y+=dy;
		rotate(dr);
	}
	
	/**
	 * Return a JSON-like {@code String} representing this {@code Piece}.
	 * 
	 * @return a JSON-like {@code String} representing this {@code Piece}
	 */
	public String toJSON() {
		return "Piece:{,%s,%d,%d,%d,}".formatted(type, x, y, r);
	}
	
	/**
	 * Extract and return the data stored in the specified JSON-link
	 * {@code String} that represent a {@code Piece}
	 * 
	 * @param json the JSON-link {@code String} representing a {@code Piece}
	 * @param out where to store the extracted data, if already created
	 * @return the extracted data
	 */
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
