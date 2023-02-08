package function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import java.util.concurrent.TimeUnit;

/**
 * A version of {@link Runnable} that allow to throw checked exception with 3 different way to handle them:
 * <ol>
 * <li>{@link #runExc()}, that rethrow the exception to the caller;</li>
 * <li>{@link #run()}, that print the exception as a {@link LambdaException};</li>
 * <li>{@link #tryRun()}, that will ignore the exception.</li>
 * </ol>
 */
@FunctionalInterface
public interface RunnableExc extends Runnable {
	
	void runExc() throws Exception;
	
	@Override
	default void run() {
		try {runExc();} 
		catch (Exception ex) {LambdaException.printStackTrace(ex);}
	}
	
	/**
	 * Try to run this lambda's method, any exception thrown is ignored
	 * 
	 * @return {@code true} if no exception were thrown, otherwise {@code false}
	 */
	default boolean tryRun() {
		try {runExc(); return true;} 
		catch (Exception ignore) {return false;}
	}
	
	
	static RunnableExc of(RunnableExc runnable) {
		return runnable;
	}
	static RunnableExc ignore(RunnableExc runnable) {
		return runnable::tryRun;
	}
	
	
	static void closeAll(AutoCloseable...autoCloseables) {
		for (var ac : autoCloseables) {
			close(ac);
		}
	}
	static void close(AutoCloseable ac) {
		if (nonNull(ac)) {
			of(ac::close).run();
		}
	}
	
	
	/**
	 * Create and start a new Thread with the specified runnable
	 * 
	 * @param runnable the object whose {@code runExc} method is invoked when this thread
     *         is started.
	 * @return a new thread with the specified runnable
	 */
	static Thread thread(RunnableExc runnable) {
		var thread = new Thread(runnable);
		thread.start();
		return thread;		
	}
	
	static boolean tryInterrupt(Thread thread) {
		if (isNull(thread)) return true;
		return of(thread::interrupt).tryRun();
	}
	
	static Thread doAfter(int delay, RunnableExc action) {
		return thread(ignore(() -> {
			TimeUnit.MILLISECONDS.sleep(delay);
			action.runExc();
		}));
	}
	
}
