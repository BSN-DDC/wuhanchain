package com.reddate.ddc.util.http;

import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.net.DDCWuhan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
                    throw new DDCException(ErrorMessage.REQUEST_FAILED.getCode(),result);
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
}