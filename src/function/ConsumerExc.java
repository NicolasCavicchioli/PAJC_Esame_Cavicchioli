package function;

import java.util.function.Consumer;

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
