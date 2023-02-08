package function;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts a single {@link Exception}
 * argument and returns no result.
 * @see Consumer
 */
public interface ExceptionConsumer extends Consumer<Exception> {}