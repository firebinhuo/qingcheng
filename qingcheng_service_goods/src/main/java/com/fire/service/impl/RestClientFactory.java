package com.fire.service.impl;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author fire
 * @date 2021年06月06日16:43
 */
public class RestClientFactory {
    public static RestHighLevelClient getRestHighLevelClient(String hostname, int port) {
        // 1.连接rest接口
        HttpHost httpHost = new HttpHost(hostname, port, "http");
        RestClientBuilder builder = RestClient.builder(httpHost);
        return new RestHighLevelClient(builder.build());//高级客户端对象
    }
}
