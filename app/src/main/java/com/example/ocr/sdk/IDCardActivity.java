/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.example.ocr.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.cloudapi.sdk.model.ApiCallback;
import com.alibaba.cloudapi.sdk.model.ApiRequest;
import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.example.ocr.R;
import com.example.ocr.sdk.utils.FileUtil;
import com.ocr.aliocrlibrary.camera.CameraActivity;
import com.ocr.aliocrlibrary.common.EnumOcrFace;
import com.ocr.aliocrlibrary.http.OcrApi;
import com.ocr.aliocrlibrary.utils.HttpResult;

import java.io.File;

/**
 * 身份证列表项，正反面选择
 */
public class IDCardActivity extends AppCompatActivity {

    private static final String TAG = "IDCardActivity";

    private static final int REQUEST_CODE_CAMERA = 102;

    private TextView infoTextView;
    private ImageView ivCard;

    private boolean checkGalleryPermission() {
        int ret = ActivityCompat.checkSelfPermission(IDCardActivity.this, Manifest.permission
                .READ_EXTERNAL_STORAGE);
        if (ret != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(IDCardActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);

        infoTextView = findViewById(R.id.info_text_view);
        ivCard = findViewById(R.id.iv_card);

        // 身份证正面拍照
        findViewById(R.id.id_card_front_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IDCardActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });

        // 身份证反面拍照
        findViewById(R.id.id_card_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IDCardActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    File file = new File(filePath);
                    ivCard.setImageBitmap(BitmapFactory.decodeFile(filePath));
                    Log.i(TAG,"截取的照片地址:" + filePath);

                    switch (contentType) {
                        case CameraActivity.CONTENT_TYPE_ID_CARD_FRONT:
                            getOcrResult(file, EnumOcrFace.FACE);
                            break;
                        case CameraActivity.CONTENT_TYPE_ID_CARD_BACK:
                            getOcrResult(file, EnumOcrFace.BACK);
                            break;
                    }
                }
            }
        }
    }

    private void getOcrResult(File file, EnumOcrFace face) {
        OcrApi.getInstance().httpTest(file, face, new ApiCallback() {
            @Override
            public void onFailure(ApiRequest apiRequest, Exception e) {
            }

            @Override
            public void onResponse(ApiRequest apiRequest, final ApiResponse apiResponse) {
                final String result = HttpResult.getResultString(apiResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoTextView.setText(result);
                    }
                });

            }
        });
    }


}
