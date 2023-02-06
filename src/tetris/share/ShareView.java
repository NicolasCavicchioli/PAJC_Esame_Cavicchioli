package tetris.share;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import pajc.event.KeyListener;
import tetris.panel.TetrisPanel;

public class ShareView {
	
	public static void apply(JFrame frame) {
		frame.getContentPane().removeAll();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {32, 365, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		
		var model = new BiModel();
		
		
		var tetrisPanel1 = new TetrisPanel();
		tetrisPanel1.info_pnl.label.setVisible(true);
		frame.getContentPane().add(tetrisPanel1, new pajc.swing.GridBagConstraints()
				.setAnchor(GridBagConstraints.CENTER)
				.setFill(GridBagConstraints.BOTH)
				.setGrid(1, 0));
		
		var tetrisPanel2 = new TetrisPanel();
		frame.getContentPane().add(tetrisPanel2, new pajc.swing.GridBagConstraints()
				.setAnchor(GridBagConstraints.CENTER)
				.setFill(GridBagConstraints.BOTH)
				.setGrid(2, 0));
		
		var controller = new ShareController(frame, model, tetrisPanel1, tetrisPanel2);
		
		tetrisPanel1.info_pnl.button.addActionListener(e -> {
			tetrisPanel1.info_pnl.hideBoth();
			tetrisPanel2.info_pnl.label.setVisible(false);
			controller.reset();
			controller.start();
		});
		tetrisPanel2.info_pnl.button.addActionListener(e -> {
			tetrisPanel2.info_pnl.hideBoth();
			tetrisPanel1.info_pnl.label.setVisible(false);
			controller.reset();
			controller.start();
		});
		
		
		frame.getContentPane().setFocusable(true);
		frame.getContentPane().requestFocus();
		frame.getContentPane().addKeyListener(KeyListener.whenKeyPressed(controller::keyPressed));
		
	}
	
}
