package tetris.single;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import pajc.event.KeyListener;
import tetris.TetrisApp;
import tetris.panel.BoardPanel;
import tetris.panel.TetrisPanel;

import static pajc.PAJC.ResizeTransitionTo;;

public class SingleView {
	
	public static void apply(JFrame frame) {
		var model = new TetrisModel();
		var tetrisPanel = new TetrisPanel();
		tetrisPanel.add(new BoardPanel(model), BoardPanel.constraints);
		var controller = new Controller(model, tetrisPanel);
		
		
		frame.getContentPane().removeAll();
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(tetrisPanel);
		frame.revalidate();
		frame.repaint();
		
		
		tetrisPanel.info_pnl.label.setText("Game Over");
		tetrisPanel.info_pnl.button.addActionListener(e -> {
			tetrisPanel.info_pnl.hideBoth();
			tetrisPanel.info_pnl.menuButton.setVisible(false);
			controller.reset();
			controller.start();
		});
		tetrisPanel.info_pnl.menuButton.addActionListener(e -> {
			frame.getContentPane().removeAll();
			new TetrisApp(frame);
			ResizeTransitionTo(new Dimension(500, 300), frame, () -> {});
		});
		
		
		tetrisPanel.setFocusable(true);
		tetrisPanel.requestFocus();
		tetrisPanel.addKeyListener(KeyListener.whenKeyPressed(controller::keyPressed));
		
		ResizeTransitionTo(new Dimension(32*16, 32*22), frame, model::start);
		
	}
	
}
