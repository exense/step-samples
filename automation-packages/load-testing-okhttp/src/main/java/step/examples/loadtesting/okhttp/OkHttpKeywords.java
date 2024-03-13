package step.examples.loadtesting.okhttp;

import okhttp3.*;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkHttpKeywords extends AbstractKeyword {
    private static final String SESSION_OKHTTP_CLIENT = "okhttpClient";

    @Keyword(name = "OpenCart Home")
    public void openCartHome() {
        get("https://opencart-prf.exense.ch/");
    }

    @Keyword(name = "OpenCart Add MacBook")
    public void openCartAddMacBook() {
        get("https://opencart-prf.exense.ch/macbook");
        // add to cart
        assertContains("success", post("https://opencart-prf.exense.ch/index.php?route=checkout%2Fcart%2Fadd",
                Map.of("quantity", "1", "product_id", "43")
        ));
        // verify cart contents
        assertContains("MacBook", get("https://opencart-prf.exense.ch/index.php?route=common%2Fcart%2Finfo"));
    }

    @Keyword(name = "OpenCart Checkout")
    public void openCartCheckout() {
        // Step 1
        assertContains("Guest Shipping", get("https://opencart-prf.exense.ch/index.php?route=checkout/checkout"));
        // Step 2
        assertContains("First Name", get("https://opencart-prf.exense.ch/index.php?route=checkout/guest"));
        assertContains("Ticino", get("https://opencart-prf.exense.ch/index.php?route=checkout/checkout/country&country_id=204"));
        Map<String, String> data = new HashMap<>(Map.of(
                "customer_group_id", "1",
                "firstname", "Gustav",
                "lastname", "Muster",
                "email", "customer@opencart.demo",
                "telephone", "+41777777777",
                "address_1", "Bahnhofstrasse 1",
                "city", "Zurich",
                "postcode", "8001",
                "country_id", "204",
                "zone_id", "3120"
        ));
        data.putAll(Map.of("company", "", "address_2", ""));
        assertContains("[]", post("https://opencart-prf.exense.ch/index.php?route=checkout/guest/save", data));
        // Step 3
        assertContains("Payment method required", post("https://opencart-prf.exense.ch/index.php?route=checkout/payment_method/save", Map.of("comment", "", "agree", "1")));
    }

    private String get(String url) {
        return perform(new Request.Builder().url(url).build());
    }

    private String post(String url, Map<String, String> params) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        params.forEach(bodyBuilder::add);
        Request request = new Request.Builder().url(url).post(bodyBuilder.build()).build();
        return perform(request);
    }

    private String perform(Request request) {
        OkHttpClient client = getOrCreateClient();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private OkHttpClient getOrCreateClient() {
        OkHttpClient client = (OkHttpClient) session.get(SESSION_OKHTTP_CLIENT);
        if (client == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.setCookieJar$okhttp(new TrivialCookieJar());
            client = new OkHttpClient(clientBuilder);
            session.put(SESSION_OKHTTP_CLIENT, client);
        }
        return client;
    }

    private void assertContains(String expected, String body) {
        if (!body.contains(expected)) {
            throw new RuntimeException("Response body did not contain expected String: " + expected);
        }
    }

    private static class TrivialCookieJar implements CookieJar {
        private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            return cookieStore.getOrDefault(url.host(), new ArrayList<>());
        }
    }
}
