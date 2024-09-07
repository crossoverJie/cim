package com.crossoverjie.cim.common.core.proxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
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