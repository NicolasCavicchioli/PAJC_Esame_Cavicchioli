package tetris.server;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import function.RunnableExc;
import pajc.event.KeyListener;
import pajc.net.MySocket;
import pajc.net.SocketEvent;
import tetris.TetrisApp;
import tetris.panel.ClientPanel;
import tetris.panel.TetrisPanel;
import static pajc.PAJC.ResizeTransitionTo;

public class JoinView {
	private static final String IP_PORT_REGEX = "^(?:localhost|(?:\\d{1,3}.){3}\\d{1,3}):[1-6]\\d{0,4}$";
	
	public static void tryApply(JFrame frame, String ip, JLabel error_lbl) {
		if (ip.matches(IP_PORT_REGEX) == false) {
			error_lbl.setText("invalid ip format");
			RunnableExc.doAfter(2000, () -> error_lbl.setText(""));
			return;
		}
		
		error_lbl.setText("Connecting...");
		
		String[] ss = ip.split(":");
		String host = ss[0];
		int port = Integer.parseInt(ss[1]);
		
		MySocket socket = new MySocket();
		socket.when(SocketEvent.ILLEGAL_ARGUMENT, (Exception ex) -> {
			error_lbl.setText(ex.getLocalizedMessage());
			frame.repaint();
		}).when(SocketEvent.UNABLE_TO_CONNECT, (Exception ex) -> {
			error_lbl.setText("Unable to reach the server, try again later");
			frame.repaint();
		}).when(SocketEvent.CONNECTED, () -> {
			apply(frame, socket);
			socket.sendMessage(ServerController.NOT_TELNET_COMMAND);
		}).when(SocketEvent.DISCONNECTED, (Exception ex) -> {
			frame.getContentPane().removeAll();
			var app = new TetrisApp(frame); // creating instance to access fields
			app.address_field.setText(ip);
			app.error_lbl.setText("Connection with server lost");
			frame.revalidate();
			frame.repaint();
			ResizeTransitionTo(new Dimension(500, 300), frame, () -> {});
		});
		
//		socket.when(SocketEvent.MESSAGE_IN, (String msg) -> System.out.println(msg));
		
		socket.connectTo(host, port);
		
	}

	private static void apply(JFrame frame, MySocket socket) {
		frame.getContentPane().removeAll();
		frame.setTitle("Tetris 2 (client)");
		frame.setSize(32*34, 32*22);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {32, 365, 0, 32};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		
		
		TetrisPanel tetrisPanel1 = new TetrisPanel();
		var clientPanel1 = new ClientPanel(tetrisPanel1);
		tetrisPanel1.add(clientPanel1, new pajc.swing.GridBagConstraints()
				.setFill(GridBagConstraints.BOTH)
				.setGrid(0, 0, 1, 2));
		frame.getContentPane().add(tetrisPanel1, new pajc.swing.GridBagConstraints()
				.setAnchor(GridBagConstraints.CENTER)
				.setFill(GridBagConstraints.BOTH)
				.setGrid(1, 0));
		
		
		
		var tetrisPanel2 = new TetrisPanel();
		var clientPanel2 = new ClientPanel(tetrisPanel2);
		tetrisPanel2.add(clientPanel2, new pajc.swing.GridBagConstraints()
				.setFill(GridBagConstraints.BOTH)
				.setGrid(0, 0, 1, 2));
		frame.getContentPane().add(tetrisPanel2, new pajc.swing.GridBagConstraints()
				.setAnchor(GridBagConstraints.CENTER)
				.setFill(GridBagConstraints.BOTH)
				.setGrid(2, 0));
		
		
		tetrisPanel1.info_pnl.button.addActionListener(e -> {
			socket.sendMessage(ServerController.PLAY_AGAIN_COMMAND);
		});
		
		
		frame.revalidate();
		frame.setFocusable(true);
		frame.requestFocus();
		frame.addKeyListener(KeyListener.whenKeyPressed((KeyEvent e)->{
			socket.sendMessage(String.valueOf(e.getKeyChar()));
		}));
		
		
		socket.when(SocketEvent.MESSAGE_IN, (String msg) -> {
			if (msg.startsWith("JSON.")) {
				msg = msg.substring(5);
				var splitted = msg.split(ServerController.JSON_SEPARATOR_REGEX);
				clientPanel1.onMessageReceived(splitted[0]);
				clientPanel2.onMessageReceived(splitted[1]);
				
			} else if (msg.equals(ServerController.WIN_MESSAGE)) {
				tetrisPanel1.info_pnl.setLabelText("Win");
				tetrisPanel1.info_pnl.button.setVisible(true);
				tetrisPanel1.repaint();
				
			} else if (msg.equals(ServerController.GAME_OVER_MESSAGE)) {
				tetrisPanel1.info_pnl.setLabelText("Game Over");
				tetrisPanel1.info_pnl.button.setVisible(true);
				tetrisPanel1.repaint();
				
			} else if (Character.isDigit(msg.charAt(0))) {
				tetrisPanel2.info_pnl.label.setVisible(false);
				tetrisPanel1.info_pnl.button.setVisible(false);
				tetrisPanel1.info_pnl.setLabelText(msg.charAt(0)+"");
				frame.repaint();
				
			} else if (msg.equals("start")) {
				tetrisPanel1.info_pnl.label.setText("start");
				tetrisPanel1.repaint();
				RunnableExc.doAfter(1000, () -> {
					tetrisPanel1.info_pnl.label.setVisible(false);
					tetrisPanel1.repaint();
				});
				
			}
		});
		
		socket.when(SocketEvent.COMMAND_IN, (String command) -> {
			if (command.equals(ServerController.PLAY_AGAIN_COMMAND)) {
				tetrisPanel2.info_pnl.setLabelText("Player want to\nplay again");
				tetrisPanel2.repaint();
			}
		});
		
	}
	
}
