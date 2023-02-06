package pajc.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import function.RunnableExc;
import function.SupplierExc;
import pajc.event.IHasEvents;
import pajc.event.MyEventHandler;

/**
 * The one and only class needed to handle socket connections, both server-side and client-side.
 * <p>
 * It enables two programs to talk to each others!
 * @see SocketEvent
 */
public class MySocket implements IHasEvents<SocketEvent> {
	final MyEventHandler<SocketEvent> events = new MyEventHandler<>();
	Socket socket;
	PrintWriter out;
	private int port = -1;
	private boolean isRunning = false;
	
	@Override
	public MyEventHandler<SocketEvent> getEventHandler() {return events;}
	public int getPort() {return port;}
	public boolean isRunning() {return isRunning;}
	
	
	public MySocket() {
		when(SocketEvent.CONNECTED, () -> {
			port = socket.getPort();
			isRunning = true;
		});
	}
	
	public void connectTo(Socket socket) {
		connectTo(()->socket);
	}
	public void connectTo(String ip, int port) {
		connectTo(()->new Socket(ip, port));
	}
	
	private void connectTo(SupplierExc<Socket> sup) {
		RunnableExc.thread(() -> {
			BufferedReader in = null;
			
			try {
				this.socket = sup.getExc();
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
				
				triggerEvent(SocketEvent.CONNECTED);
				
				String s ="";
				while (isRunning && (s=in.readLine())!=null) {
					if (s.isEmpty()) continue;
					if (s.charAt(0)=='@') {
						triggerEvent(SocketEvent.COMMAND_IN, s);
					} else {
						triggerEvent(SocketEvent.MESSAGE_IN, s);
					}
				}
				
				if (s==null) triggerEvent(SocketEvent.DISCONNECTED, new SocketException());
				
			}
			catch(IllegalArgumentException ex) {triggerEvent(SocketEvent.ILLEGAL_ARGUMENT, ex);}
			catch(ConnectException ex) {triggerEvent(SocketEvent.UNABLE_TO_CONNECT, ex);}
			catch(SocketException ex) {triggerEvent(SocketEvent.DISCONNECTED, ex);}
			catch(Exception ex) {ex.printStackTrace();}
			finally {
				isRunning = false;
				RunnableExc.closeMany(out, in, socket);
			}
			
		});
	}
	
	
	public void sendMessage(String msg) {
		if (canSendMessage()) {
			out.println(msg);
		}
	}
	public Thread typeWrite(String msg) {
		if (!canSendMessage() || msg.isEmpty()) return null;
		return RunnableExc.thread(() -> {
			for (int i=0; i<msg.length(); ++i) {
				out.printf("%c", msg.charAt(i));
				TimeUnit.MILLISECONDS.sleep(20);
			}
		});
	}
	
	public boolean canSendMessage() {
		return isRunning && out!=null && !socket.isOutputShutdown();
	}
	
}
