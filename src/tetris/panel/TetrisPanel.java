package tetris.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public class TetrisPanel extends JPanel {
	
	public TetrisPanel() {
        nextPiece_pnl = new NextPiecePanel();
        info_pnl = new InfoPanel();
		init();
	}
	
	private void init() {
		this.setBackground(Color.LIGHT_GRAY);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{32*10, 32*4};
        gbl_panel.rowHeights = new int[]{64, 32*18, 0};
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        this.setLayout(gbl_panel);
        
        
        
        JPanel right_pnl = new JPanel();
        GridBagConstraints gbc_right_pnl = new GridBagConstraints();
        gbc_right_pnl.fill = GridBagConstraints.BOTH;
        gbc_right_pnl.gridx = 1;
        gbc_right_pnl.gridy = 1;
        this.add(right_pnl, gbc_right_pnl);
        GridBagLayout gbl_right_pnl = new GridBagLayout();
        gbl_right_pnl.columnWidths = new int[]{32*4, 0};
        gbl_right_pnl.rowHeights = new int[]{128, 366, 0};
        gbl_right_pnl.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_right_pnl.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        right_pnl.setLayout(gbl_right_pnl);
        
        right_pnl.add(nextPiece_pnl, NextPiecePanel.constraints);
        
        right_pnl.add(info_pnl, InfoPanel.constraints);
        
        this.setFocusable(true);
        this.requestFocus();
        
	}
	
	public final NextPiecePanel nextPiece_pnl;
	public final InfoPanel info_pnl;
	
}
