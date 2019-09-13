package ch.exense.monitoring.managedprocess;

import java.io.IOException;

import step.commons.processmanager.ManagedProcess.ManagedProcessException;
import step.handlers.javahandler.Keyword;

public class JavaManagedProcessKeywords extends ManagedProcessKeywords {

	@Keyword
	public void JavaManagedProcessKeyword() throws IOException, ManagedProcessException{
		  
		getManagedProcessMandatoryInput();
		checkMandatoryInputs("mainClassOrJar");
		
		String cmd = buildCommandLine();
		
		executeManagedCommand(cmd);
		
	}
	
	@Override
	protected String buildCommandLine() {
	   StringBuilder sb = new StringBuilder()
   			.append(input.getString("executablePath"));
        
        if (input.containsKey("vmArgs")) {
   			sb.append(" ").append(input.getString("vmArgs"));
        }
        
        if (input.containsKey("classPath")) {
        	sb.append(" -cp ").append(input.getString("classPath"));
        }
       
        String mainClassOrJar = input.getString("mainClassOrJar");
        if (mainClassOrJar.endsWith(".jar")) {
    	   sb.append(" -jar ").append(mainClassOrJar);
        } else {
    	   sb.append(" ").append(mainClassOrJar);
        }
        
   		sb.append(" ")
   			.append(input.getString("programArgs"));
        
   		return sb.toString();
	}
}
