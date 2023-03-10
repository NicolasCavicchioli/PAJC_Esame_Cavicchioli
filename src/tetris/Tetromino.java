package tetris;

import static pajc.PAJC.half;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import function.IntBiConsumer;
import function.IntBiPredicate;

/**
 * A {@code Tetromino} is a Tetris piece formed by 4 block all with at least one side in common.
 * 
 * <p>This enum provide all the information needed for each Tetrominoes, with some utility methods.
 */
public enum Tetromino {
	I(1, 2, 8,
	"----"
	  +"XXXX"
	  +"----"
	  +"----"),
	O(1, 1, 6,
	"XX"
	  +"XX"),
	Z(1, 2, 4,
	"XX-"
	  +"-XX"
	  +"---"),
	S(1, 2, 3,
	"-XX"
	  +"XX-"
	  +"---"),
	T(1, 4, 5,
	"-X-"
	  +"XXX"
	  +"---"),
	L(1, 4, 7,
	"--X"
	  +"XXX"
	  +"---"),
	J(1, 4, 2,
	"X--"
	  +"XXX"
	  +"---"),
	AMONGUS(0, 4, 9,
	"-XXX"
	  +"XX--"
	  +"XXXX"
	  +"-X-X")
	;
	
	private static final int ghostImageIndex = 0;
	private static HashMap<Integer,Tetromino> imageIndexToType = new HashMap<>();
	private static final float[] weights;
	
	
	public final int size, maxRotate, imageIndex;
	private final float weight;
	private Point[] blockPos;
	
	
	static {
		float sum = 0;
		Tetromino[] ts = values();
		for (var t : ts) {
			sum += t.weight;
		}
		
		weights = new float[ts.length];
		for (int i=0; i<ts.length; i++) {
			weights[i] = ts[i].weight / sum;
		}
	}
	
	static {
		for (var t : values()) {
			imageIndexToType.put(t.imageIndex, t);
		}
	}
	
	
	private Tetromino(float w, int mr, int i, String display) {
		imageIndex = i;
		maxRotate = mr;
		weight = w;
		size = sqrt(display.length());
		blockPos = getBlockPos(display);
	}
	
	private int sqrt(int n) {
		return n==4? 2 : n==9 ? 3 : 4;
	}
	
	private Point[] getBlockPos(String display) {
		ArrayList<Point> points = new ArrayList<>(4);
		for (int i=0; i<display.length(); ++i) {
			if (display.charAt(i)=='X') {
				points.add(new Point(i%size, i/size));
			}
		}
		if (points.isEmpty()) throw new IllegalArgumentException("Tetromino %s has no blocks".formatted(this));
		return points.toArray(Point[]::new);
	}
	
	
    /**
     * Returns whether any blocks of the Tetromino with the specified
     * position and rotation verifies the provided predicate.
	 * 
	 * @param x the horizontal position of the Tetromino
	 * @param y the vertical position of the Tetromino
	 * @param r the rotation of the Tetromino
	 * @param test the predicate to verify
	 * @return {@code true} if any blocks verifies the provided predicate, otherwise {@code false}
	 */
	public boolean anyBlock(int x, int y, int r, IntBiPredicate test) {
		Point p = new Point(0,0);
		x -= half(size); // shift from center to upper-left corner
		y -= half(size);
		for (int i=0; i<blockPos.length; ++i) {
			rotate(blockPos[i], r, p);
			if (test.test(p.x+x, p.y+y)) return true;
		}
		return false;
	}
	
	/**
	 * Iterate over each position of the blocks of the Tetromino with the specified
	 * position and rotation.
	 * 
	 * @param x the horizontal position of the Tetromino
	 * @param y the vertical position of the Tetromino
	 * @param r the rotation of the Tetromino
	 * @param action consumer invoked with the position of the block of the Tetromino
	 */
	public void forEachBlock(int x, int y, int r, IntBiConsumer action) {
		Point p = new Point(0,0);
		x -= half(size); // shift from center to upper-left corner
		y -= half(size);
		for (int i=0; i<blockPos.length; ++i) {
			rotate(blockPos[i], r, p);
			action.accept(p.x+x, p.y+y);
		}
	}
	
	public void show(int x, int y, int r, Graphics2D g) {
		show(x, y, r, imageIndex, g);
	}
	public void showGhost(int x, int y, int r, Graphics2D g) {
		show(x, y, r, ghostImageIndex, g);
	}
	private void show(int x, int y, int r, int i, Graphics2D g) {
		forEachBlock(x,y,r, (u,v) -> { 
			g.drawImage(Assets.images[i], u, v, 1, 1, null);
		});
	}
	
	private void rotate(Point p, int r, Point target) {
		int s=size-1;
		switch(r) {
		case 0: target.set(p.x,p.y); break;
		case 1: target.set(s-p.y,p.x); break;
		case 2: target.set(s-p.x,s-p.y); break;
		case 3: target.set(p.y,s-p.x); break;
		}
	}
	
	public String initial() {
		return toString().substring(0,1);
	}
	
	
	public static Tetromino fromImageIndex(int i) {return imageIndexToType.get(i);}
	
	public static Tetromino random() {
		int i = 0;
		double r = Math.random();
		while (r>=0) {
			r-=weights[i++];
		}
		return values()[i-1];
	}
	public static Tetromino randomExcept(Tetromino toExclude) {
		Tetromino t;
		while ((t=random())==toExclude);
		return t;
	}
	
	
	
	private static class Point {
		int x,y;
		
		Point(int x, int y) {
			set(x,y);
		}
		
		void set(int x, int y) {
			this.x=x;this.y=y;
		}
		
	}
	
}
