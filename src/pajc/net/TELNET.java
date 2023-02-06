package pajc.net;

import java.util.HashSet;
import pajc.PAJC;
import tetris.Tetromino;
import tetris.single.TetrisModel;

/**
 * Class to handle ASCII representations of {@link TetrisModel}
 * for TELNET connections.
 */
public class TELNET {
	public enum Escape {
		CLEAR_ALL("\u001B[2J" ),
		DEFAULT	 ("\u001B[0m" ),
		BOLD	 ("\u001B[1m" ),
		RED		 ("\u001B[31m"),
		GREEN	 ("\u001B[32m"),
		YELLOW	 ("\u001B[33m"),
		BLUE	 ("\u001B[34m"),
		MAGENTA	 ("\u001B[35m"),
		CYAN	 ("\u001B[36m"),
		WHITE	 ("\u001B[37m");
		
		String str;
		private Escape(String s) {str=s;}
		@Override
		public String toString() {return str;}
		
	}
	
	
	public static final String WELCOME = """
			TELNET activated...
			Avaliable commands are:
			 A = move to the left (<-)
			 D = mode to the right (->)
			 S = soft drop
			 W or SPACE = hard drop
			 Q = rotate counter-clockwise
			 E = rotate clockwise
			
			Enter any key to start
			"""
			.replace("\n", PAJC.newLine);
	
	private static final String[] colors = {
			""+Escape.BLUE,
			""+Escape.GREEN,
			""+Escape.RED,
			""+Escape.MAGENTA,
			""+Escape.YELLOW,
			""+Escape.WHITE,
			""+Escape.CYAN,
			""+Escape.RED
	};
	
	private static String getColor(int n) {
		return Escape.BOLD+colors[n-2];
	}
	
	public static String modelToASCII(TetrisModel model) {
		StringBuffer sb = new StringBuffer("Next: %-4S".formatted(model.next.type)+PAJC.newLine);
		HashSet<Integer> blockPos = new HashSet<>();
		
		model.current.type.forEachBlock(0, 0, model.current.r, (int x, int y) -> {
			blockPos.add((x+model.current.x)*100+y+model.current.y);
			blockPos.add((100+x+model.current.x)*100+y+model.ghostY);
		});
		
		sb.append(Escape.DEFAULT);
		for (int y=0; y<TetrisModel.mapHeight; ++y) {
			for (int x=0; x<TetrisModel.mapWidth; ++x) {
				sb.append(getColoredChar(model.map, x, y, model.current.type, blockPos));
			}
			sb.append(PAJC.newLine);
		}
		sb.append(Escape.DEFAULT);
		
		return sb.toString();
	}
	
	public static String getColoredChar(int[][] map, int x, int y, Tetromino type, HashSet<Integer> blockPos) {
		String c = getColor(type.imageIndex);
		if (blockPos.remove(x*100+y)) return c+type.initial()+Escape.DEFAULT;
		if (blockPos.remove(x*100+y+10000)) return c+"+"+Escape.DEFAULT;
		
		int v = map[y][x];
		
		return y<2 ? "."
			: v==0 ? y==map.length-1 ? Escape.DEFAULT+"_" : " "
			: v==1 ? Escape.DEFAULT+"X"
			: getColor(v)+Tetromino.fromImageIndex(v).initial();
	}
	
}
