package ch.exense.examples.oryon;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import ch.exense.oryon.client.OryonDriver;
import ch.exense.oryon.modules.syrius.SyriusAbstractionFacade;
import step.commons.processmanager.ManagedProcess.ManagedProcessException;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class OryonKeywords extends AbstractKeyword {

	@Keyword
	public void Login() throws Exception {
		OryonDriver driver = new OryonDriver("\"/path/to/java.exe\" {oryon-agent} -cp ...", "path/to/oryon/agent.jar","/path/to/oryon/license", "SyriusFX");
		
		SyriusAbstractionFacade gui = (SyriusAbstractionFacade) driver.getAbstraction();
		
		gui.setText("/xpath/to/Textfield",input.getString("Username"));
		
		session.put(driver);
		session.put(gui);
	}
	
	@Keyword
	public void Search() throws ManagedProcessException, IOException, TimeoutException, InterruptedException {
		SyriusAbstractionFacade gui = session.get(SyriusAbstractionFacade.class);
		
		gui.setText("/xpath/to/Textfield",input.getString("Criteria"));
		output.add("result", gui.getText("/xpath/to/Textfield"));
	}
}
