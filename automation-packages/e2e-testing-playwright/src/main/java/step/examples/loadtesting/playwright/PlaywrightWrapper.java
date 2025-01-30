package step.examples.loadtesting.playwright;

import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.impl.PlaywrightImpl;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlaywrightWrapper implements AutoCloseable {
    private final Path alternativeTempDir;
    public final Playwright playwright;

    public static PlaywrightWrapper create() {
        return new PlaywrightWrapper();
    }

    private PlaywrightWrapper() {
        // Playwright automatically extracts its driver and required Node.js runtime
        // into a temporary directory, which is only deleted when the JVM terminates.
        // To clean up resources more aggressively (e.g., immediately after closing Playwright),
        // we configure an alternative temporary directory and delete it manually after the Keyword execution
        try {
            alternativeTempDir = Files.createTempDirectory("playwright");
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error while creating alternative temp dir for Playwright", e);
        }
        synchronized (System.out) {
            try {
                // Playwright allows setting the alternative temporary directory only via System property.
                // To prevent conflicts with other keywords running in parallel, Playwright initialization is performed
                // within a synchronized block to ensure thread safety.
                System.setProperty("playwright.driver.tmpdir", alternativeTempDir.toAbsolutePath().toString());
                // We call PlaywrightImpl.createImpl directly in order to force the new driver instance creation and thus
                // the use of the alternative temp dir
                playwright = PlaywrightImpl.createImpl(null, true);
            } catch (Throwable e) {
                close();
                throw e;
            }
        }
    }

    @Override
    public void close() {
        try {
            if (playwright != null) {
                playwright.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            // Delete the alternative temp dir right after closing playwright
            try {
                FileUtils.deleteDirectory(alternativeTempDir.toFile());
            } catch (IOException e) {
                throw new RuntimeException("Unexpected error while deleting temp dir for Playwright", e);
            }
        }
    }
}
