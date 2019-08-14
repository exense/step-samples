package ch.exense.step.examples.common.helper;

public class MissingInputException extends RuntimeException {
	private static final long serialVersionUID = -560280824073155632L;

	public MissingInputException(String message) {
		super(message);
	}
}