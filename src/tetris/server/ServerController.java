package tetris.server;

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
import tetris.panel.TetrisPanel;
import tetris.share.BiModel;
import tetris.single.TetrisModel;
import static pajc.PAJC.*;

public class ServerController {
	public static final String PLAY_AGAIN_COMMAND = "@play again";
	public static final String NOT_TELNET_COMMAND = "@not telnet"; 
	public static final String JSON_SEPARATOR_REGEX = "\\.\\.";
	public static final String WIN_MESSAGE =        "#####    Win    #####";
	public static final String GAME_OVER_MESSAGE = "##### Game Over #####";
	private static final String PLAY_AGAIN_MESSAGE = "type \"%s\" to play again%s"
								.formatted(PLAY_AGAIN_COMMAND, PAJC.newLine); 
	private static final String JSON_MESSAGE = "JSON.%s..%s";
	
	private boolean isClientTelnet, isClientOnline;
	private boolean host_want_to_play_again, client_want_to_play_again;
	private BiModel model;
	private TetrisPanel tetrisPanel1, tetrisPanel2;
	private MySocket protocol;
	private Thread countDownThread, typewritterThread;
	
	
	public ServerController(BiModel model, HostView view) {
		this.model = model;
		tetrisPanel1 = view.tetrisPanel1;
		tetrisPanel2 = view.tetrisPanel2;
		
		
		model.when(TetrisEvent.GAME_OVER, () -> {
			model.stop();
			tetrisPanel1.info_pnl.button.setVisible(true);
		}).when(TetrisEvent.REPAINT, () -> {
			tetrisPanel1.repaint();
			tetrisPanel2.repaint();
		});
		
		model.left.when(TetrisEvent.JSON, view.tetrisPanel1.nextPiece_pnl::setType)
		.when(TetrisEvent.SMALL_REPAINT, ()->model.triggerEvent(TetrisEvent.SMALL_REPAINT))
		.when(TetrisEvent.BIG_REPAINT, ()->model.triggerEvent(TetrisEvent.BIG_REPAINT))
		.when(TetrisEvent.ROW_REMOVED, (int n)->what_to_do_to_punish_the_oppponent(model.right,n))
		.when(TetrisEvent.GAME_OVER, () -> {
			model.triggerEvent(TetrisEvent.GAME_OVER);
			sendMessageToClient(WIN_MESSAGE);
			view.tetrisPanel1.info_pnl.label.setText("Game over");
			view.tetrisPanel2.info_pnl.label.setText("win");
		});
		
		model.right.when(TetrisEvent.JSON, view.tetrisPanel2.nextPiece_pnl::setType)
		.when(TetrisEvent.SMALL_REPAINT, ()->model.triggerEvent(TetrisEvent.SMALL_REPAINT))
		.when(TetrisEvent.BIG_REPAINT, ()->model.triggerEvent(TetrisEvent.BIG_REPAINT))
		.when(TetrisEvent.ROW_REMOVED, (int n)->what_to_do_to_punish_the_oppponent(model.left,n))
		.when(TetrisEvent.GAME_OVER, () -> {
			model.triggerEvent(TetrisEvent.GAME_OVER);
			sendMessageToClient(GAME_OVER_MESSAGE);
			view.tetrisPanel2.info_pnl.label.setText("Game over");
			view.tetrisPanel1.info_pnl.label.setText("Win");
		});
		
		
		model.left.triggerEvent(TetrisEvent.SMALL_REPAINT);
		model.right.triggerEvent(TetrisEvent.SMALL_REPAINT);
		
		
		runServer(1234,
			serverSocket -> view.address_lbl.setText(setAddressLabel(serverSocket)),
			clientSocket -> {
				
				isClientTelnet = isClientOnline = true;
				tetrisPanel1.info_pnl.label.setText("");
				tetrisPanel2.info_pnl.label.setVisible(false);
				protocol = new MySocket();
				
				protocol.when(SocketEvent.MESSAGE_IN, this::onClientMessage)
				.when(SocketEvent.COMMAND_IN, this::onClientCommand)
				.when(SocketEvent.DISCONNECTED, this::onClientDisconnected);
				
				protocol.connectTo(clientSocket);
				
				
				TimeUnit.MILLISECONDS.sleep(50);
				
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
		model.when(TetrisEvent.JSON, protocol::sendMessage)
		.when(TetrisEvent.SMALL_REPAINT, ()
		 -> model.triggerEvent(TetrisEvent.JSON, createShortJSONMessage()))
		.when(TetrisEvent.BIG_REPAINT, ()
		 -> model.triggerEvent(TetrisEvent.JSON, createJSONMessage()));
		
		model.triggerEvent(TetrisEvent.SMALL_REPAINT);
		
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
	
	
	private void what_to_do_to_punish_the_oppponent(TetrisModel victim, int n) {
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
			tetrisPanel2.info_pnl.label.setVisible(true);
			tetrisPanel2.info_pnl.label.setText("player want to play again");
			onPlayAgain();
		}
	}
	
	private void onClientDisconnected(Exception ex) {
		RunnableExc.tryInterrupt(countDownThread);
		model.stop();
		isClientOnline = false;
		tetrisPanel2.info_pnl.label.setVisible(true);
		tetrisPanel2.info_pnl.label.setText("player 2 is offline");
		tetrisPanel1.info_pnl.label.setVisible(false);
		tetrisPanel2.repaint();
	}
	
	private void onPlayAgain() {
		if (host_want_to_play_again && client_want_to_play_again) {
			host_want_to_play_again = client_want_to_play_again = false;
			model.reset();
			tetrisPanel2.info_pnl.label.setVisible(false);
			startCountDown();
		}
	}
	
	private void sendMessageToClient(String msg) {
		protocol.sendMessage(msg);
	}
	
	public void startCountDown() {
		if (!isClientOnline) return;
		tetrisPanel1.info_pnl.button.setVisible(false);
		tetrisPanel1.info_pnl.label.setText("");
		tetrisPanel1.info_pnl.label.setVisible(true);
		
		countDownThread = startCoundDown(3, t -> {
			sendMessageToClient(t+"");
			tetrisPanel1.info_pnl.label.setText(t+"");
			tetrisPanel1.info_pnl.repaint();
		}, () -> {
			sendMessageToClient("start");
			tetrisPanel1.info_pnl.label.setText("start");
			tetrisPanel1.info_pnl.repaint();
			model.start();
			TimeUnit.SECONDS.sleep(1);
			tetrisPanel1.info_pnl.label.setVisible(false);
		});
	}
	
	private static String setAddressLabel(ServerSocket server) {
		return "Running server on: %s:%d".formatted(getLocalAddress(), server.getLocalPort());
	}
	
	private String createShortJSONMessage() {
        return JSON_MESSAGE.formatted(model.right.getShortJSON(), model.left.getShortJSON());
    }
	private String createJSONMessage() {
		return JSON_MESSAGE.formatted(model.right.getLongJSON(), model.left.getLongJSON());
	}
		
}
