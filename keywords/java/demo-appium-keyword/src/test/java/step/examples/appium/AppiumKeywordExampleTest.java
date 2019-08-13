package step.examples.appium;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import step.handlers.javahandler.KeywordRunner;
import step.handlers.javahandler.KeywordRunner.ExecutionContext;

public class AppiumKeywordExampleTest {

	private ExecutionContext ctx;

	@Before
	public void setUp() {
		Map<String, String> properties = new HashMap<>();
		ctx = KeywordRunner.getExecutionContext(properties, AppiumKeywordExample.class);
	}

	@Test
	public  void testApiDemo() throws Exception {
		String apkPath = Paths.get(this.getClass().getClassLoader().getResource("apk/ApiDemos-debug.apk").toURI()).toString().replace("\\", "/");
		String avd = "Nexus_5X_API_26";
		
		ctx.run("startAppium");
		ctx.run("startEmulator", "{"
				+ "\"avd\":\"" + avd + "\","
				//+ "\"avdArgs\":\"-no-audio -wipe-data\","
				+ "\"avdArgs\":\"-no-audio\","
				+ "\"avdLaunchTimeout\": 600000,"
				+ "\"avdReadyTimeout\": 600000,"
				+ "\"newCommandTimeout\": 0,"
				+ "\"appPath\":\""+ apkPath +"\""
				+ "}");
	
		ctx.run("startApplication");
		
		ctx.run("animateDrawable");
		ctx.run("testClick");
		ctx.run("testSlider");	
		ctx.run("displayTextClock");
		
		ctx.run("stopApplication");
		ctx.run("stopAppium");
	}

	@After
	public void tearDown() {
		ctx.close();
	}
}