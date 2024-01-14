package step.examples.loadtesting.okhttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import step.functions.io.AbstractSession;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OkHttpKeywords extends AbstractKeyword {
    private static final boolean WORKAROUND = true;

    private static final String OKHTTP_CLIENT = "okhttpClient"; // not working, keeps running into the following problems regardless of session etc.:
    // errorSummary=class okhttp3.OkHttpClient cannot be cast to class okhttp3.OkHttpClient (okhttp3.OkHttpClient is in unnamed module of loader step.grid.contextbuilder.JavaLibrariesClassLoader @514c7ee5; okhttp3.OkHttpClient is in unnamed module of loader step.grid.contextbuilder.JavaLibrariesClassLoader @69636681)
    private static final String OKHTTP_COOKIES_WORKAROUND = "okhttpClient_Cookies_Workaround";

    @Keyword(name = "okhttp Init")
    public void okhttpClientInit() {
        if (WORKAROUND) {
        } else {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.setCookieJar$okhttp(new TrivialCookieJar());
            session.put(OKHTTP_CLIENT, new OkHttpClient(clientBuilder));
        }
    }

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
                "email", "gustav@muster.ch",
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
        OkHttpClient client;
        if (WORKAROUND) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.setCookieJar$okhttp(new TrivialCookieJar(session));
            client = clientBuilder.build();
        } else {
            client = (OkHttpClient) session.get(OKHTTP_CLIENT);
        }

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String body = response.body().string();
            //System.err.println("BODY SIZE: " + body.length());
            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertContains(String expected, String body) {
        if (!body.contains(expected)) {
            //System.err.println(body);
            throw new RuntimeException("Response body did not contain expected String: " + expected);
        }
    }

    private static class TrivialCookieJar implements CookieJar {
        private final Map<String, List<Cookie>> cookieStore = new HashMap<>();

        // FIXME: WORKAROUND
        private final Map<String, List<String>> cookieStore2 = new HashMap<>();
        private static final ObjectMapper JSON_OBJECT_MAPPER = (new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        private final AbstractSession session;

        // FIXME: Both contructors are due to the workaround and should be removed.
        private TrivialCookieJar() {
            this(null);
        }
        private TrivialCookieJar(AbstractSession session) {
            if (WORKAROUND) {
                this.session = session;
                try {
                    String sCookiesString = (String) session.get(OKHTTP_COOKIES_WORKAROUND);
                    if (sCookiesString != null) {
                        cookieStore2.putAll(JSON_OBJECT_MAPPER.readValue(sCookiesString, Map.class));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                this.session = null;
            }
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (WORKAROUND) {
                try {
                    cookieStore2.put(url.host(), cookies.stream().map(Cookie::toString).collect(Collectors.toList()));
                    session.put(OKHTTP_COOKIES_WORKAROUND, JSON_OBJECT_MAPPER.writeValueAsString(cookieStore2));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                cookieStore.put(url.host(), cookies);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            if (WORKAROUND) {
                List<String> cookies = cookieStore2.get(url.host());
                if (cookies != null) {
                    return cookies.stream().map(s -> Cookie.parse(url, s)).collect(Collectors.toList());
                }
                return new ArrayList<>();
            } else {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<>();
            }
        }
    }
}
