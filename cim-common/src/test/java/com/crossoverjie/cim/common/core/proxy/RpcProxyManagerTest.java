package com.crossoverjie.cim.common.core.proxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

    interface Echo {
        @Request(url = "sample-request?author=beeceptor")
        EchoResponse echo(EchoRequest message);
    }

    @Data
    public static class EchoRequest{
        private String name;
        private int age;
        private String city;
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