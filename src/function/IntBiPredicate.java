package function;

/**
 * Represents a predicate (boolean-valued function) of two {@code int}-valued arguments.
 * This is the {@code int}-consuming primitive type specialization of {@link BiPredicate}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #test(int,int)}.
 *
 * @see BiPredicate
 * @since 1.8
 */
@FunctionalInterface
public interface IntBiPredicate {
	
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
	boolean test(int a, int b);
	
}
