package com.zy.recursion.util;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class httpPost {

    private static String URL = "http://39.107.238.25:80/analysisapi/";

    public static String  sendPostDataByJson(String url, String json, String encoding) throws ClientProtocolException, IOException {
        String result = "";
        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        // 设置参数到请求对象中
        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
//        stringEntity.setContentEncoding("utf-8");
        httpPost.setEntity(stringEntity);
        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpPost);
//        result = EntityUtils.toString(response.getEntity(), "utf-8");
        result = EntityUtils.toString(response.getEntity());// 返回json格式：
//        Logger logger=Logger.getLogger("=gnj");;
//        logger.info("POST请求返回的数据是："+responsejson);
        // 获取结果实
        // 判断网络连接状态码是否正常(0--200都数正常)
//        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//            result = EntityUtils.toString(response.getEntity(), "utf-8");
//        }
        // 释放链接
        response.close();
        return result;
    }

    public static String testSendPostDataByJson(JSONObject jsonObject) throws ClientProtocolException, IOException {
        return sendPostDataByJson(URL, jsonObject.toString(), "utf-8");
    }

//    public static void main(String[] args) {
//        System.out.println(testSendPostDataByJson());
//    }
}
