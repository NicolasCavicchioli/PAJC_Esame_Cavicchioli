package tetris.server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import pajc.PAJC;
import tetris.Tetromino;
import tetris.panel.NextPiecePanel;
import tetris.panel.TetrisPanel;
import tetris.single.TetrisModel;

public class ClientPanel extends JPanel {
	
	Object[] gameState;
	NextPiecePanel nextPiece_pnl;
	
	public ClientPanel(TetrisPanel tetrisPanel) {
		this.setBackground(Color.LIGHT_GRAY);
		
		nextPiece_pnl = tetrisPanel.nextPiece_pnl;
		
	}
	
	public void onMessageReceived(String json) {
		gameState = TetrisModel.fromJSON(json, gameState);
		nextPiece_pnl.type = Tetromino.valueOf((String)gameState[2]);
		
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
		if (gameState==null) return;
		TetrisModel.paintFromObject(gameState, g2);
	}
	
}
