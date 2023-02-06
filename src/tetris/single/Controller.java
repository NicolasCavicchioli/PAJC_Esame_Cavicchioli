package tetris.single;

import tetris.IController;
import tetris.TetrisEvent;
import tetris.panel.BoardPanel;
import tetris.panel.TetrisPanel;

public class Controller implements IController {
	
	TetrisModel model;
	
	
	public Controller(TetrisModel model, TetrisPanel tetrisPanel) {
		this.model = model;
		
		tetrisPanel.add(new BoardPanel(model), BoardPanel.constraints);
		
		model.when(TetrisEvent.REPAINT, ()->tetrisPanel.repaint())
		.when(TetrisEvent.JSON, tetrisPanel.nextPiece_pnl::setType)
		.when(TetrisEvent.GAME_OVER, () -> {
			model.stop();
			tetrisPanel.info_pnl.label.setVisible(true);
			tetrisPanel.info_pnl.button.setVisible(true);
		});
		
		model.triggerEvent(TetrisEvent.REPAINT);
		
	}
	
	@Override
	public void reset() {
		model.reset();
	}
	@Override
	public void start() {
		model.start();
	}
	@Override
	public void keyPressed(int keyCode) {
		model.keyPressed(keyCode);
	}
	
}
