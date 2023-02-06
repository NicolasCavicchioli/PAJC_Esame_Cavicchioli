package tetris.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import tetris.Tetromino;
import tetris.single.TetrisModel;

public class NextPiecePanel extends JPanel {
	public static final GridBagConstraints constraints;
	public Tetromino type;
	
	static {
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		
	}
	
	NextPiecePanel() {
		setBackground(Color.ORANGE);
        setLayout(null);
        
	}
	
	public void setType(String json) {
		type = TetrisModel.getTypeFromJSON(json);
		System.out.println("set");
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (type==null) return;
		Graphics2D g2 = (Graphics2D)g;
		TetrisModel.scale(g2);
		type.show(2, 2, 0, g2);
	}
	
}