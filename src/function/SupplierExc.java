package function;

import java.util.function.Supplier;

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
