package tetris.server;

import static java.util.Objects.nonNull;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;
import function.MultiConsumer;
import function.RunnableExc;
import pajc.PAJC;
import pajc.net.MySocket;
import pajc.net.SocketEvent;
import pajc.net.TELNET;
import tetris.TetrisEvent;
import tetris.Tetromino;
import tetris.panel.BoardPanel;
import tetris.panel.TetrisPanel;
import tetris.share.BiModel;
import tetris.single.TetrisModel;

/**
 * Controller for the Multiplayer mode.
 * 
 * @see BiModel
 * @see HostView
 * @see JoinView
 */
public class ServerController {
	public static final String PLAY_AGAIN_COMMAND = "@play again";
	public static final String NOT_TELNET_COMMAND = "@not telnet"; 
	public static final String WIN_MESSAGE =		"#####	Win	#####";
	public static final String GAME_OVER_MESSAGE = "##### Game Over #####";
	public static final String JSON_SEPARATOR_REGEX = "\\.\\.";
	private static final String JSON_MESSAGE = "JSON.%s..%s";
	private static final String PLAY_AGAIN_MESSAGE = "type \"%s\" to play again%s"
			.formatted(PLAY_AGAIN_COMMAND, PAJC.newLine); 
	
	private boolean isClientTelnet, isClientOnline, isGameOver;
	private boolean host_want_to_play_again, client_want_to_play_again;
	private BiModel model;
	private TetrisPanel tetrisPanel1, tetrisPanel2;
	private MySocket protocol;
	private Thread countDownThread, typewritterThread;
	
	
	public ServerController(BiModel model, HostView view) {
		this.model = model;
		tetrisPanel1 = view.tetrisPanel1;
		tetrisPanel2 = view.tetrisPanel2;
		
		tetrisPanel1.add(new BoardPanel(model.left), BoardPanel.constraints);
		tetrisPanel2.add(new BoardPanel(model.right), BoardPanel.constraints);
		
		
		model.when(TetrisEvent.GAME_OVER, () -> {
			tetrisPanel1.info_pnl.button.setVisible(true);
			isGameOver = true;
		}).when(TetrisEvent.REPAINT, () -> {
			tetrisPanel1.repaint();
			tetrisPanel2.repaint();
		});
		
		model.left.when(TetrisEvent.JSON, view.tetrisPanel1.nextPiece_pnl::setType)
		.when(TetrisEvent.ROW_REMOVED, (int n)->what_to_do_to_punish_the_opponent(model.right,n))
		.when(TetrisEvent.GAME_OVER, () -> {
			sendMessageToClient(WIN_MESSAGE);
			tetrisPanel1.info_pnl.setLabelText("Game over");
			tetrisPanel2.info_pnl.setLabelText("win");
		});
		
		model.right.when(TetrisEvent.JSON, view.tetrisPanel2.nextPiece_pnl::setType)
		.when(TetrisEvent.ROW_REMOVED, (int n)->what_to_do_to_punish_the_opponent(model.left,n))
		.when(TetrisEvent.GAME_OVER, () -> {
			sendMessageToClient(GAME_OVER_MESSAGE);
			tetrisPanel1.info_pnl.setLabelText("Win");
			tetrisPanel2.info_pnl.setLabelText("Game over");
		});
		
		
		model.left.triggerEvent(TetrisEvent.REPAINT);
		model.right.triggerEvent(TetrisEvent.REPAINT);
		
		
		PAJC.runServer(1234,
			serverSocket -> view.address_lbl.setText(setAddressLabel(serverSocket)),
			clientSocket -> {
				if (isClientOnline) { // there is already a connected player2
					clientSocket.close();
					return;
				}
				
				if (isGameOver) model.reset();
				isClientTelnet = isClientOnline = true;
				tetrisPanel2.info_pnl.setLabelText("");
				protocol = new MySocket();
				
				protocol.when(SocketEvent.MESSAGE_IN, this::onClientMessage)
				.when(SocketEvent.COMMAND_IN, this::onClientCommand)
				.when(SocketEvent.DISCONNECTED, this::onClientDisconnected);
				
				protocol.connectTo(clientSocket);
				
				
				TimeUnit.MILLISECONDS.sleep(100); // wait for NOT_TELNET_COMMAND to arrive
				if (isClientTelnet) telnetMode();
				else clientMode(model);
				
			}
		);
		
	}

	
	
	private void telnetMode() throws InterruptedException {
		tetrisPanel2.info_pnl.setLabelText("waiting for input");
		
		model.when(TetrisEvent.COUNTDOWN, (int t)->sendMessageToClient(t+""));
		
		model.left.when(TetrisEvent.GAME_OVER, ()->sendMessageToClient(PLAY_AGAIN_MESSAGE));
		
		model.right.when(TetrisEvent.GAME_OVER, ()->sendMessageToClient(PLAY_AGAIN_MESSAGE))
		.when(TetrisEvent.REPAINT, () -> {
			sendMessageToClient(TELNET.Escape.CLEAR_ALL.toString());
			sendMessageToClient(TELNET.modelToASCII(model.right));
		});
		
		protocol.getEventHandler().registerAction(SocketEvent.MESSAGE_IN,
			MultiConsumer.from((String msg)->{
				RunnableExc.tryInterrupt(typewritterThread);
				startCountDown();
				tetrisPanel2.info_pnl.label.setVisible(false);
			}).doOnce()
		);
		
		
		if (model.isNotRunning()) {
			TimeUnit.MILLISECONDS.sleep(1); // to make sure to not miss the first character of TELNET.WELCOME
			typewritterThread = protocol.typeWrite(TELNET.WELCOME);
		}
		
	}
	
	private void clientMode(BiModel model) {
		model.when(TetrisEvent.REPAINT, ()
		 -> sendMessageToClient(createJSONMessage()));
		
		model.triggerEvent(TetrisEvent.REPAINT);
		
		if (model.isNotRunning()) {
			startCountDown();
		}
		
	}
	
	
	public void hostKeyPressed(int keyCode) {
		model.left.keyPressed(keyCode);
	}
	private void clientKeyPressed(int keyCode) {
		model.right.keyPressed(keyCode);
	}
	
	public void host_want_to_play_again() {
		host_want_to_play_again = true;
		if (isClientTelnet) sendMessageToClient("host is ready to play again");
		else sendMessageToClient(PLAY_AGAIN_COMMAND);
		onPlayAgain();
	}
	
	
	private void what_to_do_to_punish_the_opponent(TetrisModel victim, int n) {
		if (n<4) victim.addPartialRows(n);
		else victim.setNextPiece(Tetromino.AMONGUS);
	}
	
	
	private void onClientMessage(String message) {
		if (!message.isEmpty()) {
			message = message.toUpperCase();
			clientKeyPressed(message.charAt(0));
		}
	}
	private void onClientCommand(String command) {
		if (command.equals(NOT_TELNET_COMMAND)) {
			isClientTelnet = false;
		} else if (command.equals(PLAY_AGAIN_COMMAND)) {
			client_want_to_play_again = true;
			tetrisPanel2.info_pnl.setLabelText("player is ready");
			onPlayAgain();
		}
	}
	
	private void onClientDisconnected(Exception ex) {
		RunnableExc.tryInterrupt(countDownThread);
		model.stop();
		isClientOnline = false;
		tetrisPanel1.info_pnl.label.setVisible(false);
		tetrisPanel2.info_pnl.setLabelText("player is offline");
		tetrisPanel2.repaint();
	}
	
	private void onPlayAgain() {
		if (host_want_to_play_again && client_want_to_play_again) {
			host_want_to_play_again = client_want_to_play_again = false;
			model.reset();
			isGameOver = false;
			tetrisPanel2.info_pnl.label.setVisible(false);
			startCountDown();
		}
	}
	
	private void sendMessageToClient(String msg) {
		if (nonNull(protocol)) protocol.sendMessage(msg);
	}
	
	public void startCountDown() {
		if (!isClientOnline) return;
		tetrisPanel1.info_pnl.setLabelText("");
		tetrisPanel1.info_pnl.button.setVisible(false);
		
		countDownThread = PAJC.startCoundDown(3, t -> {
			sendMessageToClient(t+"");
			tetrisPanel1.info_pnl.setLabelText(t+"");
			tetrisPanel1.info_pnl.repaint();
		}, () -> {
			sendMessageToClient("start");
			tetrisPanel1.info_pnl.setLabelText("start");
			tetrisPanel1.info_pnl.repaint();
			model.start();
			TimeUnit.SECONDS.sleep(1);
			tetrisPanel1.info_pnl.label.setVisible(false);
		});
	}
	
	private static String setAddressLabel(ServerSocket server) {
		return "Running server on: %s:%d".formatted(PAJC.getLocalAddress(), server.getLocalPort());
	}
	
	private String createJSONMessage() {
		return JSON_MESSAGE.formatted(model.right.toJSON(), model.left.toJSON());
	}
	
}
