package step.examples.playwright.library;

import com.microsoft.playwright.*;
import step.grid.io.AttachmentHelper;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import javax.swing.plaf.basic.BasicTreeUI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class for Playwright-based keywords.
 * Provides common functionality and allow to share the playwright driver and browser across keywords even when deployed from multiple Automation Packages
 */
public abstract class AbstractPlaywrightKeyword extends AbstractKeyword {

    /**
     * This method check if playwright context (Playwright driver, browser and page) is already stored in the Keyword's session. Otherwise, it is created and stored in session.
     * All objects implementing AutoCloseable and stored in the session are automatically closed when the session is terminated
     * In this case we use a wrapper to ensure the playwright resources are closed in the right order
     */
    protected Page getOrCreatePlaywrightPage() {
        PlaywrightWrapper playwrightWrapper = session.get(PlaywrightWrapper.class);
        if (playwrightWrapper == null) {
            Playwright playwright = null;
            Browser browser = null;
            Page page = null;
            try {
                playwright = Playwright.create();
                browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
                page = browser.newContext().newPage();
            } catch (Throwable t) {
                //In case of error we clea nup resources and rethrow the error
                Optional.ofNullable(playwright).ifPresent(Playwright::close);
                Optional.ofNullable(browser).ifPresent(Browser::close);
                Optional.ofNullable(page).ifPresent(Page::close);
                throw t;
            }
            playwrightWrapper = new PlaywrightWrapper(playwright, browser, page);
            session.put(playwrightWrapper);
        }
        return playwrightWrapper.page;
    }

    @Override
    public boolean onError(Exception e) {
        if (e.getCause() != null && (e.getCause() instanceof com.microsoft.playwright.PlaywrightException ||
                e.getCause() instanceof org.opentest4j.AssertionFailedError)) {
            output.setBusinessError(e.getCause().getMessage());
            return false;
        } else {
            return super.onError(e);
        }
    }

    private static class PlaywrightWrapper implements AutoCloseable {
        public final Playwright playwright;
        public final Browser browser;
        public final Page page;

        public PlaywrightWrapper(Playwright playwright, Browser browser, Page page) {
            this.playwright = Objects.requireNonNull(playwright);
            this.browser = Objects.requireNonNull(browser);
            this.page = Objects.requireNonNull(page);
        }

        @Override
        public void close() throws Exception {
            page.close();
            browser.close();
            playwright.close();
        }
    }
}
