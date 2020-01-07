package ch.exense.monitoring.managedprocess;

import step.handlers.javahandler.Keyword;

public class JavaManagedProcessKeywords extends ManagedProcessKeywords {


	@Keyword(schema = "{\"properties\":{\"mainClassOrJar\":{\"type\":\"string\"},\"timeoutInMillis\":{\"type\":\"string\"},\"maxOutputPayloadSize\":{\"type\":\"string\"},\"maxOutputAttachmentSize\":{\"type\":\"string\"},\"executablePath\":{\"type\":\"string\"}},\"required\":[\"timeoutInMillis\",\"maxOutputPayloadSize\", \"maxOutputAttachmentSize\",\"executablePath\",\"mainClassOrJar\"]}")
	public void JavaManagedProcessKeyword() throws Exception{
		  
		getManagedProcessMandatoryInput();
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
