package com.reddate.ddc.util.http;

import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.net.DDCWuhan;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RestTemplateConfig {

    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
//        restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));

        //The default RestTemplate has a mechanism to throw an exception if the request status code is less than 200,
        // interrupting the rest of the operation.If you do not want to interrupt parsing of the resulting data,
        // you can override the default responseErrorHandler
        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return true;
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                HttpStatus statusCode = clientHttpResponse.getStatusCode();
                if (!statusCode.is2xxSuccessful()) {
                    String result = clientHttpResponse.getStatusText();
                    log.error("statuCode:{}, statuText:{}, header:{}, body:{}",
                            clientHttpResponse.getStatusCode(),
                            clientHttpResponse.getStatusText(),
                            clientHttpResponse.getHeaders(),
                            convertStreamToString(clientHttpResponse.getBody()));
                    throw new DDCException(ErrorMessage.REQUEST_FAILED.getCode(), result);
                }
            }
        };
        restTemplate.setErrorHandler(responseErrorHandler);
        return restTemplate;
    }

    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(DDCWuhan.getConnectTimeout());
        factory.setReadTimeout(DDCWuhan.getReadTimeout());
        return factory;
    }

    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(DDCWuhan.getConnectTimeout());
        clientHttpRequestFactory.setReadTimeout(DDCWuhan.getReadTimeout());
        clientHttpRequestFactory.setHttpClient(httpClientBuilder().build());
        return clientHttpRequestFactory;
    }


    private HttpClientBuilder httpClientBuilder() {
        PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
                .register(CookieSpecs.STANDARD,new DefaultCookieSpecProvider(publicSuffixMatcher))
                .register(CookieSpecs.STANDARD,new RFC6265CookieSpecProvider(publicSuffixMatcher))
                .register("easy", new EasySpecProvider())
                .build();

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(poolingConnectionManager());
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = (httpResponse, httpContext) -> DDCWuhan.CONNECTION_KEEP_ALIVE_TIME;
        httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy);
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler());
        httpClientBuilder.setDefaultCookieSpecRegistry(r);
        return httpClientBuilder;
    }


    public HttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(DDCWuhan.POOLING_CONNECTION_MANAGER_MAX_TOTAL);
        poolingConnectionManager.setDefaultMaxPerRoute(DDCWuhan.POOLING_CONNECTION_MANAGER_MAX_PER_ROUTE);
        poolingConnectionManager.setValidateAfterInactivity(DDCWuhan.POOLING_CONNECTION_MANAGER_VALIDATE_AFTER_INACTIVITY);
        poolingConnectionManager.closeIdleConnections(DDCWuhan.POOLING_CONNECTION_MANAGER_CLOSE_IDLE_CONNECTIONS, TimeUnit.SECONDS);
        poolingConnectionManager.closeExpiredConnections();
        return poolingConnectionManager;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


    class EasySpecProvider implements CookieSpecProvider {
        @Override
        public CookieSpec create(HttpContext context) {
            return new EasyCookieSpec();
        }
    }
    class EasyCookieSpec extends DefaultCookieSpec {
        @Override
        public void validate(Cookie arg0, CookieOrigin arg1) {
            //allow all cookies
        }
    }
}