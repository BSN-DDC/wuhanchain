package com.reddate.ddc.util.http;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.net.DDCWuhan;
import com.reddate.ddc.net.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.internal.Function;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.web3j.utils.Strings;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RestTemplateUtil {

    private static RestTemplate restTemplate;

    public RestTemplateUtil() {
        RestTemplateConfig restTemplateConfig = new RestTemplateConfig();
        restTemplate = restTemplateConfig.restTemplate(restTemplateConfig.simpleClientHttpRequestFactory());
    }

    public static <T> T sendPost(String url, Object params, Class<T> t) throws RestClientException {
        HttpHeaders header = new HttpHeaders();
        // Requirements need to be passed in form-data format
        header.set("charset", "UTF-8");
        header.set("Content-Type", "application/json");

        String value = JSONObject.toJSONString(params);

        log.debug("send http request to {} ,the params are {}", url, value);

        HttpEntity httpEntity = new HttpEntity(value, header);
        return restTemplate.postForObject(url, httpEntity, t);
    }

    public <T> T sendGet(String url, Object params, Class<T> t) throws RestClientException {
        HttpHeaders header = new HttpHeaders();
        // Requirements need to be passed in form-data format
        header.set("charset", "UTF-8");
        header.set("Content-Type", "application/json");
        String value = JSONObject.toJSONString(params);

        log.debug("send http request to {} ,the params are {}", url, value);

        HttpEntity httpEntity = new HttpEntity(value, header);
        return restTemplate.getForObject(url, t, httpEntity);
    }

    public <T> T sendDel(String url, Map<String, Object> urlParams, Object bodyParams, Class<T> t) throws RestClientException {
        HttpHeaders header = new HttpHeaders();
        // Requirements need to be passed in form-data format
        header.set("charset", "UTF-8");
        header.set("Content-Type", "application/json");
        String value = JSONObject.toJSONString(bodyParams);

        log.debug("send http request to {} ", url);

        HttpEntity httpEntity = new HttpEntity(value, header);

        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, t, urlParams);

        return response.getBody();
    }

    public <T> T sendPostFile(String url, String filePath, String fileName, Class<T> t) throws RestClientException {
        //Set ReqHeader
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        //Set ReqBody，LinkedMultiValueMap
        FileSystemResource fileSystemResource = new FileSystemResource(filePath + "/" + fileName);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", fileSystemResource);
        form.add("filename", fileName);

        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);

        return restTemplate.postForObject(url, files, t);
    }

    public static <T> T get(String url, Class<T> clazz, long timeout, int limit) throws Exception {
        return reTry(url, timeout, limit, u -> restTemplate.getForEntity(u, clazz).getBody());
    }

    public static <T> T sendPost(Object params, Class<T> t, RequestOptions options) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiKey = DDCWuhan.getGatewayApiKey();
        if (!Strings.isEmpty(apiKey)) {
            headers.add("x-api-key", apiKey);
        }
        HttpEntity entity = new HttpEntity<>(params, headers);

        String url = options.getGateWayUrl();

        if (Objects.nonNull(options)) {
            return reTry(url, options.getConnectTimeout(), options.getNetworkRetries(), u -> restTemplate.postForEntity(u, entity, t).getBody());
        }
        return restTemplate.postForEntity(url, entity, t).getBody();
    }

    private static <T> T reTry(String url, long timeout, int limit, Function<String, T> function) throws Exception {
        Exception exception = new Exception("try failed...");
        for (int i = 0; i <= limit; i++) {
            try {
                CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> function.apply(url));
                T response = future.get(timeout, TimeUnit.MILLISECONDS);
                return response;
            } catch (Exception e) {
                exception = e;
            }
        }
        throw exception;
    }
}
