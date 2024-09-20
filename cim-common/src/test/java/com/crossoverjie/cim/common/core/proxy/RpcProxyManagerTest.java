package com.crossoverjie.cim.common.core.proxy;

import com.crossoverjie.cim.common.exception.CIMException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

class RpcProxyManagerTest {

    @Test
    public void testGet() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.github.com/users";
        Github github = RpcProxyManager.create(Github.class, url, client);
        GithubResponse githubResponse = github.crossoverjie();
        Assertions.assertEquals(githubResponse.getName(), "crossoverJie");
        github.torvalds();
    }

    @Test
    public void testPost() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://echo.free.beeceptor.com";
        Echo echo = RpcProxyManager.create(Echo.class, url, client);
        EchoRequest request = new EchoRequest();
        request.setName("crossoverJie");
        request.setAge(18);
        request.setCity("shenzhen");
        EchoResponse response = echo.echo(request);
        Assertions.assertEquals(response.getParsedBody().getName(), "crossoverJie");
        Assertions.assertEquals(response.getParsedBody().getAge(), 18);
        Assertions.assertEquals(response.getParsedBody().getCity(), "shenzhen");
    }

    @Test
    public void testUrl() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://echo.free.beeceptor.com/sample-request?author=beeceptor";
        Echo echo = RpcProxyManager.create(Echo.class, client);
        EchoRequest request = new EchoRequest();
        request.setName("crossoverJie");
        request.setAge(18);
        request.setCity("shenzhen");
        EchoResponse response = echo.echoTarget(url,request);
        Assertions.assertEquals(response.getParsedBody().getName(), "crossoverJie");
        Assertions.assertEquals(response.getParsedBody().getAge(), 18);
        Assertions.assertEquals(response.getParsedBody().getCity(), "shenzhen");
        response = echo.echoTarget(request, url);
        Assertions.assertEquals(response.getParsedBody().getName(), "crossoverJie");

        String req = "/request";
        response = echo.request("http://echo.free.beeceptor.com", request);
        Assertions.assertEquals(response.getPath(), req);
        Assertions.assertEquals(response.getParsedBody().getAge(), 18);

        Assertions.assertThrows(CIMException.class, () -> echo.echoTarget(request));
    }

    @Test
    public void testFail() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://echo.free.beeceptor.com";
        Echo echo = RpcProxyManager.create(Echo.class, url, client);
        EchoRequest request = new EchoRequest();
        request.setName("crossoverJie");
        request.setAge(18);
        request.setCity("shenzhen");
        Assertions.assertThrows(IllegalArgumentException.class, () -> echo.fail(request, "test",""));
    }


    @Test
    public void testGeneric() {
        OkHttpClient client = new OkHttpClient();
        String url = "http://echo.free.beeceptor.com";
        Echo echo = RpcProxyManager.create(Echo.class, url, client);
        EchoRequest request = new EchoRequest();
        request.setName("crossoverJie");
        request.setAge(18);
        request.setCity("shenzhen");
        EchoGeneric<EchoResponse.HeadersDTO> response = echo.echoGeneric(request);
        Assertions.assertEquals(response.getHeaders().getHost(), "echo.free.beeceptor.com");
    }

    interface Echo {
        @Request(url = "sample-request?author=beeceptor")
        EchoResponse echo(EchoRequest message);

        @Request(url = "sample-request?author=beeceptor")
        EchoResponse echoTarget(@DynamicUrl(useMethodEndpoint = false) String url,  EchoRequest message);
        EchoResponse echoTarget(EchoRequest message, @DynamicUrl(useMethodEndpoint = false) String url);
        @Request(url = "sample-request?author=beeceptor")
        EchoResponse echoTarget(@DynamicUrl EchoRequest message);
        EchoResponse request(@DynamicUrl() String url, EchoRequest message);
        @Request(url = "sample-request?author=beeceptor")
        EchoResponse fail(EchoRequest message, String s, String s1);

        @Request(url = "sample-request?author=beeceptor")
        EchoGeneric<EchoResponse.HeadersDTO> echoGeneric(EchoRequest message);
    }

    @Data
    public static class EchoRequest{
        private String name;
        private int age;
        private String city;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CIMServerResVO implements Serializable {

        private String ip ;
        private Integer cimServerPort;
        private Integer httpPort;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EchoGeneric<T> {
        private String method;
        private String protocol;
        private String host;

        private T headers;
    }

    @NoArgsConstructor
    @Data
    public static class EchoResponse{

        @JsonProperty("method")
        private String method;
        @JsonProperty("protocol")
        private String protocol;
        @JsonProperty("host")
        private String host;
        @JsonProperty("path")
        private String path;
        @JsonProperty("ip")
        private String ip;
        @JsonProperty("headers")
        private HeadersDTO headers;
        @JsonProperty("parsedQueryParams")
        private ParsedQueryParamsDTO parsedQueryParams;
        @JsonProperty("parsedBody")
        private ParsedBodyDTO parsedBody;

        @NoArgsConstructor
        @Data
        public static class HeadersDTO {
            @JsonProperty("Host")
            private String host;
            @JsonProperty("User-Agent")
            private String userAgent;
            @JsonProperty("Content-Length")
            private String contentLength;
            @JsonProperty("Accept")
            private String accept;
            @JsonProperty("Content-Type")
            private String contentType;
            @JsonProperty("Accept-Encoding")
            private String acceptEncoding;
        }

        @NoArgsConstructor
        @Data
        public static class ParsedQueryParamsDTO {
            @JsonProperty("author")
            private String author;
        }

        @NoArgsConstructor
        @Data
        public static class ParsedBodyDTO {
            @JsonProperty("name")
            private String name;
            @JsonProperty("age")
            private Integer age;
            @JsonProperty("city")
            private String city;
        }
    }

    interface Github {
        @Request(method = Request.GET)
        GithubResponse crossoverjie();

        @Request(method = Request.GET)
        void torvalds();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GithubResponse {
        @JsonProperty("name")
        private String name;
    }
}