package com.example.ocr.sdk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.cloudapi.sdk.model.ApiCallback;
import com.alibaba.cloudapi.sdk.model.ApiRequest;
import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.example.ocr.BuildConfig;
import com.example.ocr.R;
import com.example.ocr.sdk.utils.FileUtil;
import com.example.ocr.sdk.utils.LuBanUtils;
import com.ocr.aliocrlibrary.common.EnumOcrFace;
import com.ocr.aliocrlibrary.http.OcrApi;
import com.ocr.aliocrlibrary.utils.HttpResult;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author xuzeyang
 * @date 2018/7/5
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String DEFAULT_DISK_CACHE_DIR = "luban_disk_cache";
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private final int REQUEST_CODE_TAKE_PHOTO = 1001;
    private final int FACE_CROP_IMG_CODE = 1002;
    private final int MY_PERMISSIONS_REQUEST = 10023;
    private Uri fileUri = null;
    private String mCurrentPhotoPath;
    private ImageView mIV;
    private ImageView mCropIV;
    private TextView mOcrTv;
    private EnumOcrFace face = EnumOcrFace.FACE;

    private String[] allPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //需要初始化阿里身份证识别的key
        OcrApi.getInstance().setKey("你的appKey", "你的AppSecret");



        mIV = findViewById(R.id.mIV);
        mCropIV = findViewById(R.id.mCropIV);
        mOcrTv = findViewById(R.id.mOcrTv);
        Button mBtnFront = findViewById(R.id.mBtnFront);
        Button mBtn = findViewById(R.id.mBtn);
        Button mIdCardBtn = findViewById(R.id.mIdCardBtn);
        mBtnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                face = EnumOcrFace.FACE;
                checkAllPermission();
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                face = EnumOcrFace.BACK;
                checkAllPermission();
            }
        });

        mIdCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, IDCardActivity.class));
            }
        });
    }

    /**
     * 6.0以上检查权限
     */
    private void checkAllPermission() {
        boolean needApply = false;
        for (String permission : allPermissions) {
            //如果有未申请的权限
            if (applyPermission(permission)) {
                needApply = true;
            }
        }
        if (needApply) {
            ActivityCompat.requestPermissions(this,
                    allPermissions,
                    MY_PERMISSIONS_REQUEST);
        } else {
            openCamera();
        }
    }

    private boolean applyPermission(String permission) {
        // Here, thisActivity is the current activity
        return ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED;
    }

    private void checkPermission(String permission) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        MY_PERMISSIONS_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            //do something
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, com.ocr.aliocrlibrary.R.string.camera_permission_required, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 调用开启Camera
     */
    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        if (intent.resolveActivity(getPackageManager()) != null) {
            String filename = "original_"
                    + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA).format(new Date())
                    + ".png";
//            File file = new File(Environment.getExternalStorageDirectory(), filename);
            // Android/data/com.example.ocr/cache/luban_disk_cache/
            File file = new File(FileUtil.getImageCacheDir(this, DEFAULT_DISK_CACHE_DIR), filename);
            mCurrentPhotoPath = file.getAbsolutePath();
            Log.i(TAG, "原始照片地址:" + mCurrentPhotoPath);

            //Android7.0以上uri不能直接去获取
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            } else {
                fileUri = Uri.fromFile(file);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            Log.i(TAG, "开启拍照");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    onResultCamera();
                }
                break;
            case FACE_CROP_IMG_CODE:
                onResultCrop(data);
                break;
        }

    }

    private void onResultCamera() {
        Log.i(TAG, "获取到拍照后的回调");

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mIV.setImageBitmap(bitmap);

        try {
            //裁剪图片
            File imageCacheDir = FileUtil.getImageCacheDir(this, DEFAULT_DISK_CACHE_DIR);
            File tempFile = File.createTempFile("crop_", ".jpg", imageCacheDir);
            Log.i(TAG, "裁剪图片地址:" + tempFile.getAbsolutePath());
            Uri outputUri = Uri.fromFile(tempFile);
            Crop.of(fileUri, outputUri).withAspect(18, 11).start(this, FACE_CROP_IMG_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onResultCrop(final Intent data) {
        Log.i(TAG, "获取到裁剪后的回调");

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        //压缩图片
                        LuBanUtils.compressImg(MainActivity.this, Crop.getOutput(data), new LuBanUtils.OnMyCompressListener() {
                            @Override
                            public void onSuccess(File file) {
                                Log.i(TAG, "裁剪 success");
                                String absolutePath = file.getAbsolutePath();
                                mCropIV.setImageBitmap(BitmapFactory.decodeFile(absolutePath));
                                Log.i(TAG, "压缩图片地址:" + absolutePath);
                                getOcrResult(file, face);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG, "裁剪 failed");
                            }
                        });
                    }
                }
        ).start();
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
                        mOcrTv.setText(result);
                        System.out.println(result);
                    }
                });

            }
        });
    }


}
