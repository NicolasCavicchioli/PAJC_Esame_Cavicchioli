package function;

import java.util.function.Supplier;

/**
 * A version of {@link Supplier} that allow to throw checked exception with 3 different way to handle them:
 * <ol>
 * <li>{@link #getExc()}, that rethrow the exception to the caller;</li>
 * <li>{@link #get()}, that throw a {@link LambdaException}</li>
 * <li>{@link #tryGet()}, that will ignore the exception</li>
 * </ol>
 */
@FunctionalInterface
public interface SupplierExc<T> extends Supplier<T> {
	
	T getExc() throws Exception;
	
	@Override
	default T get() {
		try {return getExc();}
		catch(Exception ex) {throw new LambdaException(ex);}
	}
	
	default T tryGet() {
		try {return getExc();}
		catch(Exception ignore) {return null;}
	}
	
	
	static <T> SupplierExc<T> of(SupplierExc<T> supplier) {
		return supplier;
	}
	
}
