package ch.exense.monitoring.managedprocess;

import java.io.File;
import java.nio.file.Files;

import step.handlers.javahandler.Keyword;

public class WindowsServiceStatusKeywords extends ManagedProcessKeywords {

	@Keyword
	public void WindowsServiceStatusKeyword() throws Exception {
		getManagedProcessMandatoryInput();
		String cmd = buildCommandLine();
		executeManagedCommand(cmd);
	}

	@Override
	protected String buildCommandLine() throws Exception {
		String serviceDisplayName = input.getString("serviceDisplayName");
		return new StringBuilder().append(executablePath)
				.append(" Get-Service -DisplayName '" + serviceDisplayName + "' | Format-List Status").toString();

	}

	@Override
	protected void executionPostProcess(File file) throws Exception {
		String processOutput = new String(Files.readAllBytes(file.toPath())).replace("\n", "").replace("\r", "");
		String status = processOutput.substring(processOutput.lastIndexOf(":") + 1).toLowerCase().trim();
		output.add("status", status);
	}
}