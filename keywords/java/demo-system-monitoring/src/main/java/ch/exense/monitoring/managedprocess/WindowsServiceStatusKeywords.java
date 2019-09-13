package ch.exense.monitoring.managedprocess;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import step.handlers.javahandler.Keyword;

public class WindowsServiceStatusKeywords extends ManagedProcessKeywords {
	
	
	@Keyword
	public void WindowsServiceStatusKeyword() {
		getManagedProcessMandatoryInput();
		 
		String cmd;
		try {
			cmd = buildCommandLine();
			executeManagedCommand(cmd);
		} catch (Exception e) {
			failWithException(e);
		}
	}
	
	@Override
	protected String buildCommandLine() throws IOException {
		String serviceDisplayName = input.getString("serviceDisplayName");
		return new StringBuilder()
			.append(executablePath)
			.append(" Get-Service -DisplayName '" + serviceDisplayName + "' | Format-List Status" ).toString();
		
	}
	

   @Override
	protected void executionPostProcess(File file) throws IOException {
		String processOutput = new String(Files.readAllBytes(file.toPath())).replace("\n", "").replace("\r", "");
		String status = processOutput.substring(processOutput.lastIndexOf(":") +1).toLowerCase().trim();
		output.add("status", status);
	}
}