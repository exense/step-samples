package ch.exense.step.examples;

import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class MyCustomKeyword extends AbstractKeyword{
	
	@Keyword(name = "MyCustomKeyword")
	public void execKeywordSleep() throws Exception {
		String aStringInput = input.getString("someInput");

		output.add("yourStringInputWas", aStringInput);
	}
}