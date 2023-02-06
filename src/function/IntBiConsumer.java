package function;

import java.util.function.BiConsumer;

/**
 * Represents an operation that accepts two {@code int}-valued arguments and
 * returns no result.  This is the primitive type specialization of
 * {@link BiConsumer} for {@code int}. 
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(int,int)}.
 *
 * @see BiConsumer
 */
@FunctionalInterface
public interface IntBiConsumer {
	
    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     */
	void accept(int a, int b);
	
}
