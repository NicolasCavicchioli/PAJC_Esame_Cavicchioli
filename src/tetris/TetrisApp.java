package tetris;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import pajc.PAJC;
import tetris.server.HostView;
import tetris.server.JoinView;
import tetris.share.ShareView;
import tetris.single.SingleView;

public class TetrisApp {
	
	private JFrame frame;
	public JTextField address_field;
	public JLabel error_lbl;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		PAJC.runApp(TetrisApp::new);
	}
	
	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public TetrisApp() {
		initialize();
	}
	public TetrisApp(JFrame frame) {
		initialize(frame);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		initialize(frame);
	} 
	private void initialize(JFrame frame) {
		frame.setBounds(100, 0, 500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Assets.icon);
		frame.setBackground(Color.LIGHT_GRAY);
		frame.setResizable(false);
		applyMenuView(frame);
		
	}
	
	public TetrisApp applyMenuView(JFrame frame) {
		frame.setTitle("Tetris 2");
		frame.getContentPane().setLayout(new GridLayout(2, 2, 0, 0));
		
		
		JPanel single_pnl = new JPanel();
		frame.getContentPane().add(single_pnl);
		GridBagLayout gbl_single_pnl = new GridBagLayout();
		gbl_single_pnl.columnWidths = new int[]{0, 138, 0};
		gbl_single_pnl.rowHeights = new int[]{43, 0, 0, 0};
		gbl_single_pnl.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_single_pnl.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		single_pnl.setLayout(gbl_single_pnl);
		
		JLabel single_lbl = new JLabel("Single Player");
		GridBagConstraints gbc_single_lbl = new GridBagConstraints();
		gbc_single_lbl.insets = new Insets(0, 0, 5, 0);
		gbc_single_lbl.gridx = 1;
		gbc_single_lbl.gridy = 1;
		single_pnl.add(single_lbl, gbc_single_lbl);
		
		JButton single_btn = new JButton("Play");
		single_btn.addActionListener(e -> SingleView.apply(frame));
		GridBagConstraints gbc_single_btn = new GridBagConstraints();
		gbc_single_btn.gridx = 1;
		gbc_single_btn.gridy = 2;
		single_pnl.add(single_btn, gbc_single_btn);
		
		JPanel share_pnl = new JPanel();
		frame.getContentPane().add(share_pnl);
		GridBagLayout gbl_share_pnl = new GridBagLayout();
		gbl_share_pnl.columnWidths = new int[]{0, 138, 0};
		gbl_share_pnl.rowHeights = new int[]{43, 0, 0, 0};
		gbl_share_pnl.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_share_pnl.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		share_pnl.setLayout(gbl_share_pnl);
		
		JLabel share_lbl = new JLabel("Multi player (Share screen)");
		GridBagConstraints gbc_share_lbl = new GridBagConstraints();
		gbc_share_lbl.insets = new Insets(0, 0, 5, 0);
		gbc_share_lbl.gridx = 1;
		gbc_share_lbl.gridy = 1;
		share_pnl.add(share_lbl, gbc_share_lbl);
		
		JButton share_btn = new JButton("Play");
		share_btn.addActionListener(e -> ShareView.apply(frame));
		GridBagConstraints gbc_share_btn = new GridBagConstraints();
		gbc_share_btn.gridx = 1;
		gbc_share_btn.gridy = 2;
		share_pnl.add(share_btn, gbc_share_btn);
		
		JPanel host_pnl = new JPanel();
		frame.getContentPane().add(host_pnl);
		GridBagLayout gbl_host_pnl = new GridBagLayout();
		gbl_host_pnl.columnWidths = new int[]{0, 138, 0};
		gbl_host_pnl.rowHeights = new int[]{43, 0, 0, 0};
		gbl_host_pnl.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_host_pnl.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		host_pnl.setLayout(gbl_host_pnl);
		
		JLabel host_lbl = new JLabel("Multi player");
		GridBagConstraints gbc_host_lbl = new GridBagConstraints();
		gbc_host_lbl.insets = new Insets(0, 0, 5, 0);
		gbc_host_lbl.gridx = 1;
		gbc_host_lbl.gridy = 1;
		host_pnl.add(host_lbl, gbc_host_lbl);
		
		JButton host_btn = new JButton("Host");
		host_btn.addActionListener(e -> {
			new HostView().apply(frame);
		});
		GridBagConstraints gbc_host_btn = new GridBagConstraints();
		gbc_host_btn.gridx = 1;
		gbc_host_btn.gridy = 2;
		host_pnl.add(host_btn, gbc_host_btn);
		
		JPanel join_pnl = new JPanel();
		frame.getContentPane().add(join_pnl);
		GridBagLayout gbl_join_pnl = new GridBagLayout();
		gbl_join_pnl.columnWidths = new int[] {144, 51};
		gbl_join_pnl.rowHeights = new int[]{43, 14, 23, 0, 0};
		gbl_join_pnl.columnWeights = new double[]{0.0, 0.0};
		gbl_join_pnl.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		join_pnl.setLayout(gbl_join_pnl);
		
		JLabel join_lbl = new JLabel("Multi player");
		join_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_join_lbl = new GridBagConstraints();
		gbc_join_lbl.fill = GridBagConstraints.HORIZONTAL;
		gbc_join_lbl.gridwidth = 2;
		gbc_join_lbl.insets = new Insets(0, 0, 5, 0);
		gbc_join_lbl.gridx = 0;
		gbc_join_lbl.gridy = 1;
		join_pnl.add(join_lbl, gbc_join_lbl);
		
		address_field = new JTextField("localhost:1234");
		address_field.setToolTipText("x.y.z.w:1234");
		address_field.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_txtLocalhost = new GridBagConstraints();
		gbc_txtLocalhost.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLocalhost.insets = new Insets(0, 0, 5, 5);
		gbc_txtLocalhost.gridx = 0;
		gbc_txtLocalhost.gridy = 2;
		join_pnl.add(address_field, gbc_txtLocalhost);
		address_field.setColumns(10);
		
		JButton join_btn = new JButton("Join");
		GridBagConstraints gbc_join_btn = new GridBagConstraints();
		gbc_join_btn.insets = new Insets(0, 0, 5, 0);
		gbc_join_btn.gridx = 1;
		gbc_join_btn.gridy = 2;
		join_pnl.add(join_btn, gbc_join_btn);
		
		error_lbl = new JLabel();
		error_lbl.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_error_lbl = new GridBagConstraints();
		gbc_error_lbl.gridwidth = 2;
		gbc_error_lbl.gridx = 0;
		gbc_error_lbl.gridy = 3;
		join_pnl.add(error_lbl, gbc_error_lbl);
		
		join_btn.addActionListener(e -> {
			JoinView.tryApply(frame, address_field.getText(), error_lbl);
		});
		
		frame.revalidate();
		frame.repaint();
		
		return this;
	}
	
	
}
