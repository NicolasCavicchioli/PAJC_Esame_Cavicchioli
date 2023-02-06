package tetris.panel;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class InfoPanel extends JPanel {
	public static final GridBagConstraints constraints;
	public final JLabel label;
	public final JButton button, menuButton;
	
	static {
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
	}
	
	InfoPanel() {
        this.setLayout(new GridLayout(2, 1));
        
        label = new JLabel("");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVisible(false);
        this.add(label);
        
        button = new JButton("Try again");
        button.setVisible(false);
        this.add(button);
        
        menuButton = new JButton("Menu");
        menuButton.setVisible(false);
        this.add(menuButton);
        
	}
	
	public InfoPanel hideBoth() {
		label.setVisible(false);
		button.setVisible(false);
		return this;
	}
	public InfoPanel showBoth() {
		label.setVisible(true);
		button.setVisible(true);
		return this;
	}
	public InfoPanel setLabelText(String text) {
		label.setText(text);
		label.setVisible(text.isEmpty());
		return this;
	}
	
}