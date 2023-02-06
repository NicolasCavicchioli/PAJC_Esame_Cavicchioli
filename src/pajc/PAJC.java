package pajc;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import function.ConsumerExc;
import function.LambdaException;
import function.RunnableExc;
import function.SupplierExc;

public class PAJC implements WindowConstants {
	
	public static void main(String[] args) {
		RunnableExc.of(() -> {throw new Exception();}).run();
	}
	
	public static final String newLine = "\r\n";
	
	public static int half(int n) {return n>>1;}
	public static double lerp(double a, double b, double t) {return a + t*(b-a);}
	
	public static <K,V> V getOrPutDefault(Map<K,V> map, K key, Supplier<V> defaultValue) {
		return map.computeIfAbsent(key, k->defaultValue.get());
	}
	
	public static String getLocalAddress() throws LambdaException {
		return SupplierExc.of(InetAddress::getLocalHost).get().getHostAddress();
	}
	
	public static void runInPaintThread(Runnable task) {
		if (EventQueue.isDispatchThread()) task.run();	
		else SwingUtilities.invokeLater(task);
	}
	
	public static <T> void runApp(ConsumerExc<JFrame> creator) {
		EventQueue.invokeLater(RunnableExc.of(() -> {
			var frame = new JFrame();
			creator.accept(frame);
			frame.setVisible(true);
		}));
	}
	
	public static void runServer(int port, ConsumerExc<ServerSocket> onServerCreated, ConsumerExc<Socket> onClientConnected) {
		RunnableExc.thread(() -> {
			try (ServerSocket server = new ServerSocket(port)) {
				onServerCreated.accept(server);
				while (true) {
					Socket clientSocket = server.accept();
					onClientConnected.accept(clientSocket);
				}
			}
		});
	}
	
	
	public static void ResizeTransitionTo(Dimension to, JFrame frame, RunnableExc onEnd) {
		RunnableExc.thread(()->{
			Dimension from = frame.getSize();
			for (float t=0; t<=1.01; t+=0.1) {
				float t_ = t;
				PAJC.runInPaintThread(() -> frame.setSize(
					(int)PAJC.lerp(from.width, to.width, t_),
					(int)PAJC.lerp(from.height, to.height, t_)
				));
				TimeUnit.MILLISECONDS.sleep(10);
			}
			onEnd.run();
		});
	}
	
	public static Thread startCoundDown(int n, ConsumerExc<Integer> during, RunnableExc after) {
		return RunnableExc.doAfter(1000, () -> {
			for (int t=n; t>0; --t) {
				during.accept(t);
				TimeUnit.SECONDS.sleep(1);
			}
			after.runExc();
		});
	}
	
}
