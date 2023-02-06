package function;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import pajc.event.IEvent;
import pajc.event.IHasEvents;
import pajc.event.MyEventHandler;

/**
 * Represents an operation that accepts multiple input argument and returns no
 * result.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object[])}.
 * 
 * @see MyEventHandler
 * @see IHasEvents
 */
@SuppressWarnings("unchecked")
@FunctionalInterface
public interface MultiConsumer {
	
	void accept(Object[] os);
	
	
	default <Event extends IEvent> MultiConsumer doOnce() {
		byte[] done = {0}; // allocating 1 byte of space
		return arg -> {
			if (done[0]>0) return;
			done[0] = 1;
			accept(arg);
		};
	}
	
	
	static <T> MultiConsumer from(Runnable runnable) {
		return t->runnable.run();
	}
	static MultiConsumer from(IntConsumer consumer) {
		return t->consumer.accept((int)t[0]);
	}
	static <T> MultiConsumer from(Consumer<T> consumer) {
		return t->consumer.accept((T)t[0]);
	}
	static <T,U> MultiConsumer from(BiConsumer<T,U> consumer) {
		return t->consumer.accept((T)t[0], (U)t[1]);
	}
	
}
