package tetris.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import tetris.single.TetrisModel;

@SuppressWarnings("serial")
public class BoardPanel extends JPanel {
	
	public static final GridBagConstraints constraints;
	public final TetrisModel model;
	
	static {
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridheight = 2;
		constraints.gridx = 0;
		constraints.gridy = 0;
	}
	
	
	public BoardPanel(TetrisModel m) {
		model = m;
		setBackground(Color.LIGHT_GRAY);
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		model.show((Graphics2D)g);
	}
	
}