package step.examples.sikulix;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class SikuliKeywords extends AbstractKeyword {

	@Keyword
	public void openWord() throws FindFailed {
		Screen s = new Screen();
		String baseFolder = this.getClass().getClassLoader().getResource("OpenWord.sikuli").getPath() + "/";
		
		s.click(baseFolder + "1625644370402.png");
		s.type(baseFolder + "1625644370402.png", "word");
		s.click(baseFolder + "1625644522872.png");
		
		output.startMeasure("Word oeffnen");
		s.wait(baseFolder + "1625644577728.png");
		output.stopMeasure();
		
		s.click(baseFolder + "1625644577728.png");
		s.click(baseFolder + "1625644598806.png");
		
		s.type(baseFolder + "1625655852423.png", "C:\\Users\\jecom\\Documents\\Test.docx");

		s.click(baseFolder + "1625656019455.png");
		output.startMeasure("Dokument oeffnen");
		s.wait(baseFolder + "1625656075229.png");
		output.stopMeasure();
		
		s.click(baseFolder + "1625657312506.png");
	}
}
