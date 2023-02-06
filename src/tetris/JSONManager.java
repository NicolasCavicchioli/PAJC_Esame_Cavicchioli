package tetris;

import java.awt.Graphics2D;
import tetris.single.TetrisModel;

public class JSONManager {
	final TetrisModel model;
	Tetromino current;
	String map_json;
	
	public JSONManager(TetrisModel model) {
		this.model = model;
	}
	
	
	public String toJSON() {
		if (model.current.type!=current) {
			current = model.current.type;
			map_json = getJSONMap();
		}
		return getJSONHeader()+map_json;
	}
	
	private String getJSONHeader() {
		return "jm.%s.g:%d.n:%s"
		.formatted(model.current.toJSON(), model.ghostY, model.next.type);
	}
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
	public static Object[] fromJSON(String json, Object[] out) {
		if (out==null) {
		    out = new Object[]{null,null,null,""};
		}
		var splitted = json.split("\\.");
		
		out[0] = splitted[1].substring(6); // current
		out[1] = Integer.parseInt( splitted[2].substring(2) ); // ghost
		out[2] = splitted[3].substring(2); // next
		out[3] = splitted[4].substring(4); // map
		
		return out;
	}
	
	public static Tetromino getNextTypeFromJSON(String json) {
		return Tetromino.valueOf(json.split("\\.")[3].substring(2));
	}
	
	public static void paintFromObject(Object[] modelData, Graphics2D g) {
		String map = (String)modelData[3];
		String empty = TetrisModel.EMPTY_CELL+"";
		for (int x,y, i=0, n=map.length(); i<n; ++i) {
			String c = map.substring(i,i+1);
			if (c.equals(empty)) continue;
			x = TetrisModel.mapWidth-1 - i % TetrisModel.mapWidth;
			y = TetrisModel.mapHeight-1 - i / TetrisModel.mapWidth;
			g.drawImage(Assets.images[Integer.parseInt(c)],
					x, y, 1, 1, null);
		}
		Piece.paintPieceAndGhostFromJSON((String)modelData[0], (int)modelData[1], g);
	}
	
}
