package tetris.panel;

import static java.util.Objects.isNull;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import pajc.PAJC;
import tetris.JSONManager;
import tetris.single.TetrisModel;

/**
 * Panel to display a {@link TetrisModel} from a json representation of it.
 * @see JSONManager
 */
@SuppressWarnings("serial")
public class ClientPanel extends JPanel {
	
	Object[] gameState;
	NextPiecePanel nextPiece_pnl;
	
	public ClientPanel(TetrisPanel tetrisPanel) {
		this.setBackground(Color.LIGHT_GRAY);
		
		nextPiece_pnl = tetrisPanel.nextPiece_pnl;
		
	}
	
	public void onMessageReceived(String json) {
		gameState = JSONManager.fromJSON(json, gameState);
		nextPiece_pnl.type = JSONManager.getNextTypeFromData(gameState);
		
		PAJC.runInPaintThread(() -> {
			repaint();
			nextPiece_pnl.repaint();
		});
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		TetrisModel.scale(g2);
		TetrisModel.showBackground(g2);
		if (isNull(gameState)) return;
		JSONManager.paintFromObject(gameState, g2);
	}
	
}
