package step.examples.loadtesting.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.io.IOException;

public class OkHttpKeywords extends AbstractKeyword {

    @Keyword(name = "OpenCart home")
    public void openCartHome() throws InterruptedException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://opencart-prf.exense.ch/")
                .build();

        try (Response response = client.newCall(request).execute()) {
            response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}