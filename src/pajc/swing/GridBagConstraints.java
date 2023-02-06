package pajc.swing;

public class GridBagConstraints extends java.awt.GridBagConstraints {
	
	public GridBagConstraints() {
		super();
	}
	
	public GridBagConstraints setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}
	public GridBagConstraints setFill(int fill) {
		this.fill = fill;
		return this;
	}
	
	public GridBagConstraints setGrid(int x, int y) {
		gridx = x;
		gridy = y;
		return this;
	}
	public GridBagConstraints setGrid(int x, int y, int w, int h) {
		setGrid(x,y);
		gridwidth = w;
		gridheight = h;
		return this;
	}
	
}
