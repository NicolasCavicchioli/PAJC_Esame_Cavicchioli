package tetris.panel;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel with a Label and a Button.
 */
@SuppressWarnings("serial")
public class InfoPanel extends JPanel {
	public static final GridBagConstraints constraints;
	public final JLabel label;
	public final JButton button;
	
	static {
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
	}
	
	InfoPanel() {
        this.setLayout(new GridLayout(2, 1));
        
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVisible(false);
        this.add(label);
        
        button = new JButton("Play again");
        button.setVisible(false);
        this.add(button);
        
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
		label.setVisible(!text.isEmpty());
		return this;
	}
	
}