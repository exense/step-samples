package step.examples.serenity.keywords;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.testng.TestNG;
import step.examples.serenity.test.MyJUnitTest;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Input;
import step.handlers.javahandler.Keyword;

import java.util.stream.Collectors;

/**
 * Declares the Step Keyword that runs the JUnit test {@link MyJUnitTest} and thus executes all the features
 */
public class MyKeywords extends AbstractKeyword {

    @Keyword
    public void executeJUnitTest(@Input(name = "JUnitClassname") String jUnitClassname) throws ClassNotFoundException {
        JUnitCore junit = new JUnitCore();
        Result result = junit.run(Class.forName(jUnitClassname));

        // If the test was not successful, report a business error
        if (!result.wasSuccessful()) {
            output.setBusinessError(result.getFailures().stream().map(f -> f.getMessage()).collect(Collectors.joining(",")));
        }
    }

    @Keyword
    public void executeTestNGTest(@Input(name = "TestClassname") String testClassname) throws ClassNotFoundException {
        Class<?> testClass = Class.forName(testClassname);
        TestNG myTestNG=new TestNG();
        myTestNG.setTestClasses( new Class[] {
                testClass
        });
        myTestNG.run();
    }

    @Keyword
    public void test() {
        output.setBusinessError("Error 1");
        output.appendError("Error 2");
    }

}
