package step.examples.serenity.keywords;

import ch.exense.commons.io.FileHelper;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import step.examples.serenity.test.SerenityTestRunner;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import javax.json.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Collectors;

/**
 * Declares the Step Keyword that runs the JUnit test {@link SerenityTestRunner} and thus executes all the features
 */
public class SerenityKeyword extends AbstractKeyword {

    @Keyword
    public void executeAllFeatures() throws IOException {
        JUnitCore junit = new JUnitCore();
        Result result = junit.run(SerenityTestRunner.class);

        // Add the cucumber report to the attachments
        byte[] cucumberReport = Files.readAllBytes(Path.of("reports/cucumber-html-report.html"));
        output.addAttachment(AttachmentHelper.generateAttachmentFromByteArray(cucumberReport, "cucumberReport.html"));

        // Create a zip of the serenity report and add it to the attachments
        ByteArrayOutputStream serenityReport = new ByteArrayOutputStream();
        FileHelper.zip(new File("reports/serenity"), serenityReport);
        output.addAttachment(AttachmentHelper.generateAttachmentFromByteArray(serenityReport.toByteArray(), "serenityReport.zip"));

        // Parse the cucumber report JSON and create a performance measurement for all the steps
        JsonArray cucumberJson = Json.createReader(new FileInputStream("reports/cucumber.json")).readArray();
        cucumberJson.forEach(entries -> {
            ((JsonObject) entries).getJsonArray("elements").forEach(element -> {
                ((JsonObject) element).getJsonArray("steps").forEach(step -> {
                    JsonObject stepObject = (JsonObject) step;
                    JsonNumber stepDuration = stepObject.getJsonObject("result").getJsonNumber("duration");
                    JsonString stepName = stepObject.getJsonString("name");
                    output.addMeasure(stepName.toString(), Duration.ofNanos(stepDuration.longValue()).toMillis());
                });
            });
        });

        // If the test was not successful, report a business error
        if (!result.wasSuccessful()) {
            output.setBusinessError(result.getFailures().stream().map(f -> f.getMessage()).collect(Collectors.joining(",")));
        }
    }

}
