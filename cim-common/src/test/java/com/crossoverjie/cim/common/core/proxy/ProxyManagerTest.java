package com.crossoverjie.cim.common.core.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProxyManagerTest {

    @Test
    public void testGet() {
        OkHttpClient client = new OkHttpClient();
        Github github =
                new ProxyManager<>(Github.class, "https://api.github.com/users/crossoverjie",
                        client)
                        .getInstance();
        GithubResponse githubResponse = github.userInfo();
        Assertions.assertEquals(githubResponse.getName(), "crossoverJie");

        github.userInfo2();
    }

    interface Github {
        @Request(method = Request.GET)
        GithubResponse userInfo();
        @Request(method = Request.GET)
        void userInfo2();
    }

    @NoArgsConstructor
    @Data
    public static class GithubResponse {
        @JsonProperty("name")
        private String name;
    }
}