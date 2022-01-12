package com.reddate.ddc.util.http;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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
            }
        };
        restTemplate.setErrorHandler(responseErrorHandler);

        return restTemplate;
    }


    public ClientHttpRequestFactory simpleClientHttpRequestFactory(int timeout,int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}