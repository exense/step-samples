package ch.exense.monitoring.managedprocess;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import ch.exense.commons.io.FileHelper;
import ch.exense.commons.processes.ManagedProcess;
import step.functions.io.OutputBuilder;
import step.grid.io.Attachment;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;


public class ManagedProcessKeywords extends AbstractKeyword {
	
	int timeoutInMillis;
	int maxOutputPayloadSize;
	int maxOutputAttachmentSize;
	int expectedExitCode;
	String executablePath;
	
	@Keyword(schema = "{\"properties\":{\"timeoutInMillis\":{\"type\":\"string\"},"
			+ "\"maxOutputPayloadSize\":{\"type\":\"string\"},"
			+ "\"maxOutputAttachmentSize\":{\"type\":\"string\"},"
			+ "\"expectedExitCode\":{\"type\":\"string\"},"
			+ "\"executablePath\":{\"type\":\"string\"}},\"required\":[\"executablePath\"]}")
	public void ManagedProcessKeyword() throws Exception{
		
		getManagedProcessMandatoryInput();
		 
		String cmd = buildCommandLine();
		executeManagedCommand(cmd);
	}
	
	protected  void getManagedProcessMandatoryInput() {
		timeoutInMillis = Integer.parseInt(input.getString("timeoutInMillis","10000"));
		maxOutputPayloadSize = Integer.parseInt(input.getString("maxOutputPayloadSize","256"));
		maxOutputAttachmentSize = Integer.parseInt(input.getString("maxOutputAttachmentSize","100000"));
		expectedExitCode = Integer.parseInt(input.getString("expectedExitCode","0"));
				
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
	
	protected void executeManagedCommand(String cmd) throws Exception {
		logger.info(cmd);
		ManagedProcess process = new ManagedProcess("ManagedProcess",cmd);

		try {
			process.start();
			int exitCode = process.waitFor(Math.max(0, timeoutInMillis));
			
			if (exitCode!=expectedExitCode) {
				output.setBusinessError("Exit code was not null (was "+exitCode+")");
			}
				
			String stdout = attachOutput(maxOutputPayloadSize, maxOutputAttachmentSize, output, "stdout", process.getProcessOutputLog());
			attachOutput(maxOutputPayloadSize, maxOutputAttachmentSize, output, "stderr", process.getProcessErrorLog());
			
			if (stdout != null ) {
				executionPostProcess(process.getProcessOutputLog());
			}
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
	
	protected void executionPostProcess(File file) throws Exception {
		//override this method 
	}
	
	protected String attachOutput(int maxOutputPayloadSize, int maxOutputAttachmentSize, OutputBuilder output,
			String outputName, File file) throws IOException {
		StringBuilder processOutput = new StringBuilder();
		Files.readAllLines(file.toPath(), Charset.defaultCharset()).forEach(l->processOutput.append(l).append("\n"));
		
		if(file.length()<maxOutputPayloadSize) {
			output.add(outputName, processOutput.toString());
			return processOutput.toString();
		} else {
			byte[] bytes;
			
			output.add(outputName, processOutput.toString().substring(0, maxOutputPayloadSize));
			
			if(file.length()>maxOutputAttachmentSize) {
				bytes = Arrays.copyOf(Files.readAllBytes(file.toPath()),maxOutputAttachmentSize);
			} else {
				bytes = Files.readAllBytes(file.toPath());
			}
			Attachment attachment = AttachmentHelper.generateAttachmentFromByteArray(bytes, outputName+".log");
			output.addAttachment(attachment);
			
			output.add("technicalWarning", outputName + " size exceeded. "+outputName+" has been attached and truncated.");
			
			return null;
		}
	}
}
