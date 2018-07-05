//
//  Created by  fred on 2016/10/26.
//  Copyright © 2016年 Alibaba. All rights reserved.
//

package com.ocr.aliocrlibrary.http;

import com.alibaba.cloudapi.sdk.constant.SdkConstant;
import com.alibaba.cloudapi.sdk.model.ApiCallback;
import com.alibaba.cloudapi.sdk.model.HttpClientBuilderParams;
import com.ocr.aliocrlibrary.common.EnumOcrFace;
import com.ocr.aliocrlibrary.utils.FileUtils;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OcrApi {

    private String appKey, appSecret;

    private static OcrApi instance = new OcrApi();

    public static OcrApi getInstance() {
        return instance;
    }

    public void setKey(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;

        //HTTP Client init
        HttpClientBuilderParams httpParam = new HttpClientBuilderParams();
        httpParam.setAppKey(appKey);
        httpParam.setAppSecret(appSecret);
        HttpApiClientDemo.getInstance().init(httpParam);


        //HTTPS Client init
        HttpClientBuilderParams httpsParam = new HttpClientBuilderParams();
        httpsParam.setAppKey(appKey);
        httpsParam.setAppSecret(appSecret);

        /**
         * HTTPS request use DO_NOT_VERIFY mode only for demo
         * Suggest verify for security
         */
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        httpsParam.setSslSocketFactory(sslContext.getSocketFactory());
        httpsParam.setX509TrustManager(xtm);
        httpsParam.setHostnameVerifier(DO_NOT_VERIFY);

        HttpsApiClientDemo.getInstance().init(httpsParam);
    }

    public void httpTest(File file, EnumOcrFace face, ApiCallback callback) {
        checkNotNull();
        String base64Img = FileUtils.fileToBase64(file);
        String body = "{\"inputs\":[{\"image\":{\"dataType\":50,\"dataValue\":\"" + base64Img + "\"},\"configure\":{\"dataType\":50,\"dataValue\":\"{\\\"side\\\":\\\"" + face.getSide() + "\\\"}\"}}]}";
        HttpApiClientDemo.getInstance().request(body.getBytes(SdkConstant.CLOUDAPI_ENCODING), callback);

    }

    public void httpsTest(File file, EnumOcrFace face, ApiCallback callback) {
        checkNotNull();
        String base64Img = FileUtils.fileToBase64(file);
        String body = "{\"inputs\":[{\"image\":{\"dataType\":50,\"dataValue\":\"" + base64Img + "\"},\"configure\":{\"dataType\":50,\"dataValue\":\"{\\\"side\\\":\\\"" + face.getSide() + "\\\"}\"}}]}";
        HttpsApiClientDemo.getInstance().request(body.getBytes(SdkConstant.CLOUDAPI_ENCODING), callback);
    }

    private void checkNotNull() {
        if (appKey == null) {
            throw new NullPointerException("appKey不能为空,请先调用setKey方法");
        }
        if (appSecret == null) {
            throw new NullPointerException("appSecret不能为空,请先调用setKey方法");
        }
    }


}
