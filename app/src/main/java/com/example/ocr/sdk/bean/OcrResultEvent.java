package com.example.ocr.sdk.bean;

/**
 * @author xuzeyang
 * @date 2018/7/5
 */
public class OcrResultEvent {

    private String result;

    public OcrResultEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
