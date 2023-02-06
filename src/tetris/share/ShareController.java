package tetris.share;

import static pajc.PAJC.ResizeTransitionTo;
import static pajc.PAJC.startCoundDown;

import java.awt.Dimension;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import tetris.IController;
import tetris.TetrisEvent;
import tetris.Tetromino;
import tetris.panel.TetrisPanel;
import tetris.single.TetrisModel;

public class ShareController implements IController {
	
	BiModel model;
	
	public ShareController(JFrame frame, BiModel model, TetrisPanel tetrisPanel1, TetrisPanel tetrisPanel2) {
		this.model = model;
		
		model.left.when(TetrisEvent.REPAINT, ()->tetrisPanel1.repaint())
		.when(TetrisEvent.JSON, tetrisPanel1.nextPiece_pnl::setType)
		.when(TetrisEvent.BOARD_CLEAR, () ->model.right.triggerEvent(TetrisEvent.GAME_OVER))
		.when (TetrisEvent.ROW_REMOVED, (int n)->what_to_do_to_punish_the_oppponent(model.right,n))
		.when(TetrisEvent.GAME_OVER, ()->{
			tetrisPanel1.info_pnl.setLabelText("Game Over");
			tetrisPanel1.info_pnl.button.setVisible(true);
			tetrisPanel2.info_pnl.setLabelText("Win");
		});
		
		
		model.right.when(TetrisEvent.REPAINT, ()->tetrisPanel2.repaint())
		.when(TetrisEvent.JSON, tetrisPanel2.nextPiece_pnl::setType)
		.when(TetrisEvent.BOARD_CLEAR, () ->model.left.triggerEvent(TetrisEvent.GAME_OVER))
		.when(TetrisEvent.ROW_REMOVED, (int n)->what_to_do_to_punish_the_oppponent(model.left,n))
		.when(TetrisEvent.GAME_OVER, ()->{
			tetrisPanel2.info_pnl.setLabelText("Game Over");
			tetrisPanel2.info_pnl.button.setVisible(true);
			tetrisPanel1.info_pnl.setLabelText("Win");
		});
		
		
		model.left.triggerEvent(TetrisEvent.SMALL_REPAINT);
		model.right.triggerEvent(TetrisEvent.SMALL_REPAINT);
		
		
		ResizeTransitionTo(new Dimension(32*34, 32*22), frame, () -> {
			TimeUnit.SECONDS.sleep(1);
			startCoundDown(3,
				t -> tetrisPanel1.info_pnl.label.setText(t+""),
				() -> {
				tetrisPanel1.info_pnl.label.setText("Start");
				model.start();
				TimeUnit.SECONDS.sleep(1);
				tetrisPanel1.info_pnl.label.setVisible(false);
			});
		});
		
	}
	
	public void keyPressed(int keyCode) {
		model.keyPressed(keyCode);
	}
	
	protected void what_to_do_to_punish_the_oppponent(TetrisModel victim, int n) {
		if (n<4) victim.addPartialRows(n);
		else victim.setNextPiece(Tetromino.AMONGUS);
	}

	@Override
	public void reset() {
		model.reset();
	}

	@Override
	public void start() {
		model.start();
	}
	
}
