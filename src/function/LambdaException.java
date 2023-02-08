package function;

/**
 * Exception used by {@link RunnableExc}, {@link ConsumerExc} or {@link SupplierExc}
 * to indicate that some lambda expression of those types is not working properly.
 */
public class LambdaException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public LambdaException(Exception ex) {
		super("\nCaused by: "+ex);
		setStackTrace(ex.getStackTrace());
	}
	
	public static void printStackTrace(Exception ex) {
		new LambdaException(ex).printStackTrace();
	}
	
}
