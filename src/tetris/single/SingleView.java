package tetris.single;

import static pajc.PAJC.ResizeTransitionTo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import pajc.event.KeyListener;
import tetris.panel.TetrisPanel;

/**
 * View of the Simple player mode.
 *
 * @see TetrisModel
 * @see Controller
 */
public class SingleView {
	
	public static void apply(JFrame frame) {
		var model = new TetrisModel();
		var tetrisPanel = new TetrisPanel();
		var controller = new Controller(model, tetrisPanel);
		
		
		frame.getContentPane().removeAll();
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(tetrisPanel);
		frame.revalidate();
		frame.repaint();
		
		
		tetrisPanel.info_pnl.label.setText("Game Over");
		tetrisPanel.info_pnl.button.addActionListener(e -> {
			tetrisPanel.info_pnl.hideBoth();
			controller.reset();
			controller.start();
		});
		
		
		tetrisPanel.setFocusable(true);
		tetrisPanel.requestFocus();
		tetrisPanel.addKeyListener(KeyListener.whenKeyPressed(controller::keyPressed));
		
		ResizeTransitionTo(new Dimension(32*16, 32*22), frame, model::start);
		
	}
	
}
