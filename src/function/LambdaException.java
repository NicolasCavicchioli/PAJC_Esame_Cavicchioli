package function;

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
