package ch.exense.monitoring.managedprocess.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;

/**
 * @author Jonathan Rubiero
 */
public class AbstractEnhancedKeyword extends AbstractKeyword {
	
	private static final String MISSING_INPUTS = "Following mandatory inputs are missing : ";
	
	
	public void checkMandatoryInputs(String... mandatoryInputs) {
		Set<String> mandatoryInputsSet = new HashSet<>(Arrays.asList(mandatoryInputs));
		mandatoryInputsSet.removeAll(input.keySet());
		if (!mandatoryInputsSet.isEmpty()) {
			setFailed();
			throw new MissingInputException(buildMessage(MISSING_INPUTS, mandatoryInputsSet));
		}
	}
	
	public String buildMessage(String header, Set<String> messageParts) {
		StringBuilder sb = new StringBuilder(header);
		messageParts.forEach(mp -> sb.append(mp).append(", "));
		String message = sb.toString();
		return message.substring(0, message.lastIndexOf(","));
	}
	
	public void failWithException(Exception e) {
		failWithException(e, true, true);
	}
	
	public void failWithException(Exception e, boolean withLog, boolean withStackStrace) {
		setFailed();
		if(withLog) logger.error(e.getMessage(), e);
		if(withStackStrace) e.printStackTrace();
		output.addAttachment(AttachmentHelper.generateAttachmentForException(e));
	}
	
	public void failWithErrorMessage(String errorMessage) {
		failWithErrorMessage(errorMessage, true, true);
	}
	
	public void failWithErrorMessage(String errorMessage, boolean withLog, boolean withTrace) {
		setFailed();
		if(withLog) logger.error(errorMessage);
		if(withTrace) System.out.println("Error : " + errorMessage);
		addErrorMessage(errorMessage);
	}
	
	public void addErrorMessage(String errorMessage) {
		output.add("Error", errorMessage);
	}
	
	public void setSuccess() {
		setStatus("true");
	}
	
	public void setFailed() {
		setStatus("false");
	}
	
	private void setStatus(String status) {
		output.add("success", status);
	}
}