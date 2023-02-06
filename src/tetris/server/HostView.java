package tetris.server;

import static pajc.PAJC.ResizeTransitionTo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import pajc.event.KeyListener;
import tetris.panel.TetrisPanel;
import tetris.share.BiModel;

public class HostView {
	
	TetrisPanel tetrisPanel1, tetrisPanel2;
	JLabel address_lbl;
	
	public void apply(JFrame frame) {
		var model = new BiModel();
		
		frame.getContentPane().removeAll();
		frame.setTitle("Tetris 2 (server)");
		frame.setLayout(new BorderLayout());
		
		
		address_lbl = new JLabel();
		address_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(address_lbl, BorderLayout.NORTH);
		
		var mainPanel = new JPanel();
		mainPanel.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {32, 365, 0, 32};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		mainPanel.setLayout(gridBagLayout);
		
		
		tetrisPanel1 = new TetrisPanel();
		mainPanel.add(tetrisPanel1, new pajc.swing.GridBagConstraints()
				.setAnchor(GridBagConstraints.CENTER)
				.setFill(GridBagConstraints.BOTH)
				.setGrid(1, 0));
		
		
		tetrisPanel2 = new TetrisPanel();
		mainPanel.add(tetrisPanel2, new pajc.swing.GridBagConstraints()
				.setAnchor(GridBagConstraints.CENTER)
				.setFill(GridBagConstraints.BOTH)
				.setGrid(2, 0));
		
		var controller = new ServerController(model, this);
		
		
		tetrisPanel1.info_pnl.button.setText("Play Again");
		tetrisPanel1.info_pnl.button.addActionListener(e -> {
			controller.host_want_to_play_again();
		});
		
		tetrisPanel2.info_pnl.setLabelText("waiting player 2");
		
		
		frame.requestFocus();
		frame.setFocusable(true);
		frame.addKeyListener(KeyListener.whenKeyPressed(controller::hostKeyPressed));
		
		ResizeTransitionTo(new Dimension(32*30, 32*22), frame, ()->{});
	}
	
}
