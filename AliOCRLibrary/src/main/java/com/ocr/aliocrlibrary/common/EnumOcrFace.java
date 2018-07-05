package com.ocr.aliocrlibrary.common;

/**
 * 身份证正反面
 *
 * @author xuzeyang
 * @date 2018/7/5
 */
public enum EnumOcrFace {
    /**
     * 身份证正面
     */
    FACE("face"),
    /**
     * 身份证反面
     */
    BACK("back");

    private String side;

    private EnumOcrFace(String side) {
        this.side = side;
    }

    public String getSide() {
        return side;
    }
}
