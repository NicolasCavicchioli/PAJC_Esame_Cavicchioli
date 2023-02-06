package function;

import java.util.function.Consumer;

/**
 * A version of {@link Consumer} that allow to throw checked exception with 3 different way to handle them:
 * <ol>
 * <li>{@link #acceptExc(Object)}, that rethrow the exception to the caller;</li>
 * <li>{@link #accept(Object)}, that print the exception as a {@link LambdaException};</li>
 * <li>{@link #tryAccept(Object)}, that will ignore the exception.</li>
 * </ol>
 */
@FunctionalInterface
public interface ConsumerExc<T> extends Consumer<T> {
	
	void acceptExc(T t) throws Exception;
	
	@Override
	default void accept(T t) {
		try {acceptExc(t);}
		catch(Exception ex) {LambdaException.printStackTrace(ex);}
	}
	
	default boolean tryAccept(T t) {
		try {acceptExc(t); return true;}
		catch(Exception ignore) {return false;}
	}
	
	
	static <T> ConsumerExc<T> of(ConsumerExc<T> consumer) {
		return consumer;
	}
	
}
