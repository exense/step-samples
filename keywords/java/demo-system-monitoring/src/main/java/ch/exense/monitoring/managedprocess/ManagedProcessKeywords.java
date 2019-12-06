package ch.exense.monitoring.managedprocess;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.concurrent.TimeoutException;

import ch.exense.commons.io.FileHelper;
import ch.exense.commons.processes.ManagedProcess;
import ch.exense.commons.processes.ManagedProcess.ManagedProcessException;
import ch.exense.monitoring.managedprocess.helper.AbstractEnhancedKeyword;
import step.functions.io.OutputBuilder;
import step.grid.io.Attachment;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.Keyword;



public class ManagedProcessKeywords extends AbstractEnhancedKeyword {
	
	int timeoutInMillis;
	Long maxOutputPayloadSize;
	Long maxOutputAttachmentSize;
	String executablePath;
	
	@Keyword
	public void ManagedProcessKeyword() throws IOException, ManagedProcessException{
		
		getManagedProcessMandatoryInput();
		 
		String cmd;
		try {
			cmd = buildCommandLine();
			executeManagedCommand(cmd);
		} catch (Exception e) {
			failWithException(e);
		}
	}
	
	protected  void getManagedProcessMandatoryInput() {
		checkMandatoryInputs("timeoutInMillis","maxOutputPayloadSize", "maxOutputAttachmentSize","executablePath");
		timeoutInMillis = Integer.parseInt(input.getString("timeoutInMillis"));
		maxOutputPayloadSize = Long.parseLong(input.getString("maxOutputPayloadSize"));
		maxOutputAttachmentSize = Long.parseLong(input.getString("maxOutputAttachmentSize"));
		executablePath = input.getString("executablePath");
	}
	
	protected String buildCommandLine() throws Exception {
		StringBuilder sb = new StringBuilder()
			.append(executablePath);
		if (input.containsKey("args")) {
			sb.append(" ").append(input.getString("args"));
		}		
		return sb.toString();
	}
	
	
	
	protected void executeManagedCommand(String cmd) throws ManagedProcessException, IOException {
		logger.info(cmd);
		ManagedProcess process = new ManagedProcess("ManagedProcess", cmd);

		try {
			process.start();
			process.waitFor(Math.max(0, timeoutInMillis));

			String stdout = attachOutput(maxOutputPayloadSize, maxOutputAttachmentSize, output, "stdout", process.getProcessOutputLog());
			attachOutput(maxOutputPayloadSize, maxOutputAttachmentSize, output, "stderr", process.getProcessErrorLog());
			setSuccess();
			if (stdout != null ) {
				executionPostProcess(process.getProcessOutputLog());
			} 
			
		} catch (TimeoutException e) {
			failWithErrorMessage("Timeout while waiting for process termination.");
		} catch (Exception e) {
			failWithException(e);
		} finally {
			process.close();
			File pfolder = null;
			try{
				pfolder = process.getExecutionDirectory();
				FileHelper.deleteFolder(process.getExecutionDirectory().getAbsoluteFile());
			}
			catch(Exception e){
				output.add("technicalWarning", "Process folder " + pfolder + " could not be deleted. ExceptionMessage="+e.getMessage());
			}
		}
	}
	
	protected void executionPostProcess(File file) throws IOException {
		//override this method 
	}
	
	protected String attachOutput(Long maxOutputPayloadSize, Long maxOutputAttachmentSize, OutputBuilder output,
			String outputName, File file) throws IOException {
		StringBuilder processOutput = new StringBuilder();
		if(file.length()<maxOutputPayloadSize) {
			Files.readAllLines(file.toPath(), Charset.defaultCharset()).forEach(l->processOutput.append(l).append("\n"));
			
			output.add(outputName, processOutput.toString());
			return processOutput.toString();
		} else {
			if(file.length()<maxOutputAttachmentSize) {
				byte[] bytes = Files.readAllBytes(file.toPath());
				Attachment attachment = AttachmentHelper.generateAttachmentFromByteArray(bytes, outputName+".log");
				output.addAttachment(attachment);		
				output.add("technicalWarning", outputName + " size exceeded. "+outputName+" has been attached.");
			} else {
				output.add("technicalWarning", outputName + " size exceeded. "+outputName+" couldn't be attached.");
			}
			return null;
		}
	}
	
}
