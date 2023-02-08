package tetris;

import java.awt.Graphics2D;

import javax.swing.JPanel;

import tetris.single.TetrisModel;

/**
 * Class that handle the conversion between a {@link TetrisModel} and a JSON-like String.
 * <p> It is used to update {@link NextPiecePanel}s to update itself and by {@link ServerController}
 * to send via {@link MySocket} a message with the current game state, that {@link JoinView} receives
 * and read using this same class to display the game state.
 * 
 * @see NextPiecePanel
 * @See ServerController
 * @see JoinView
 */
public class JSONManager {
	private final TetrisModel model;
	private Tetromino current;
	private String map_json;
	
	
	public JSONManager(TetrisModel model) {
		this.model = model;
	}
	
	
	/**
	 * Convert a {@link TetrisModel} to a JSON-like String.
	 * <p>It self detect when the model's map had been updated,
	 * and update the map_json field only when it's necessarily to improve performance.
	 * 
	 * @return a JSON-line String representation of a {@link TetrisModel}
	 */
	public String toJSON() {
		if (model.current.type!=current) {
			current = model.current.type;
			map_json = getJSONMap();
		}
		return getJSONHeader()+map_json;
	}
	
	/**
	 * Return a String with the current, ghostY and next field of the model.
	 * 
	 * @return a String with the current, ghostY and next field of the model
	 */
	private String getJSONHeader() {
		return "jm.%s.g:%d.n:%s"
		.formatted(model.current.toJSON(), model.ghostY, model.next.type);
	}
	/**
	 * Return a String with the map field of the model, read bottom-up,
	 * stopping where a row doesn't have any blocks. 
	 * 
	 * @return a String with the map field of the model
	 */
	private String getJSONMap() {
		StringBuffer sb = new StringBuffer(".map:");
		
		for (int y=TetrisModel.mapHeight-1; y>=0; --y) {
			StringBuffer row = new StringBuffer();
			boolean noMoreBlocks = true;
			for (int x=TetrisModel.mapWidth-1,v; x>=0; --x) {
				v = model.map[y][x];
				if (v!=TetrisModel.EMPTY_CELL) noMoreBlocks = false;
				row.append(v);
			}
			if (noMoreBlocks) break;
			sb.append(row.toString());
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Given a string representation of a {@link TetrisModel}, obtain the Model's data
	 * and saving those in an Array of Object.
	 * 
	 * @param json a string representation of a {@link TetrisModel}, to be read
	 * @param out where to store the data, if {@code null} a new one is created
	 * @return an Array of Object containing the Model's data read from the String {@code json}
	 */
	public static Object[] fromJSON(String json, Object[] out) {
		if (out==null) out = new Object[4];
		
		var splitted = json.split("\\.");
		out[0] = splitted[1].substring(6); // current
		out[1] = Integer.parseInt( splitted[2].substring(2) ); // ghost
		out[2] = splitted[3].substring(2); // next
		out[3] = splitted[4].substring(4); // map
		
		return out;
	}
	
	public static Tetromino getNextTypeFromData(Object[] modelData) {
		return Tetromino.valueOf((String)modelData[2]);
	}
	public static Tetromino getNextTypeFromJSON(String piece_json) {
		var next = piece_json.split("\\.")[3].substring(2);
		return Tetromino.valueOf(next);
	}
	
	/**
	 * Given the data information about a {@link TetrisModel}, use them to paint
	 * a {@link TetrisModel} to the specified {@code Graphics2D}.  
	 * 
	 * @param modelData data about a {@link TetrisModel}, obtained with {@link #fromJSON(String, Object[])}
	 * @param g {@code Graphics2D} of the {@link JPanel} that need to paint a {@link TetrisModel}
	 */
	public static void paintFromObject(Object[] modelData, Graphics2D g) {
		String map = (String)modelData[3];
		String empty = TetrisModel.EMPTY_CELL+"";
		for (int x,y, i=0, n=map.length(); i<n; ++i) {
			String c = map.substring(i,i+1);
			if (c.equals(empty)) continue;
			x = TetrisModel.mapWidth-1 - i % TetrisModel.mapWidth;
			y = TetrisModel.mapHeight-1 - i / TetrisModel.mapWidth;
			g.drawImage(Assets.images[Integer.parseInt(c)], x, y, 1, 1, null);
		}
		Piece.paintPieceAndGhostFromJSON((String)modelData[0], (int)modelData[1], g);
	}
	
}
