/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.ubt.alphaqrlib.R;
import com.umeng.analytics.MobclickAgent;

import java.util.Collection;
import java.util.Map;

public final class CaptureActivity extends Activity implements
        SurfaceHolder.Callback {

    // yuyong
    public final static int BindingRequestCode = 1001;
    public final static String Result_text = "Result_text";
    public final static String Result_other_way = "__Result_other_way__";

    private Button btn_back;
    private TextView txt_other_way;

    private static final String TAG = CaptureActivity.class.getSimpleName();

    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private Result lastResult;
    private boolean hasSurface;
    private IntentSource source;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;

    ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            setContentView(R.layout.this_lib_capture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        // yuyong
        txt_other_way = (TextView) findViewById(R.id.txt_other_way);
        txt_other_way.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent data = new Intent();
                data.putExtra(Result_text, Result_other_way);
                setResult(BindingRequestCode, data);
                finish();
            }
        });
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                CaptureActivity.this.finish();
            }
        });

        // yuyong
        Display mDisplay = getWindowManager().getDefaultDisplay();
        int w = mDisplay.getWidth();
        viewfinderView.setCustomerRect(w / 2 - w / 4 - 50, w / 2 + w / 4 + 50,
                w / 2 - w / 4 - 70, w / 2 + w / 4 + 30);

        viewfinderView.setCameraManager(cameraManager);

        handler = null;
        lastResult = null;

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {

            initCamera(surfaceHolder);
        } else {

            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        Intent intent = getIntent();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;

        if (intent != null) {

            String action = intent.getAction();

            if (Intents.Scan.ACTION.equals(action)) {

                source = IntentSource.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH)
                        && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }

                if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
                    int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID,
                            -1);
                    if (cameraId >= 0) {
                        cameraManager.setManualCameraId(cameraId);
                    }
                }

            }

            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

        }
    }

    @Override
    protected void onPause() {

        // yuyong
        // �˴�����ǿ���˳��Ĵ���

        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {

        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG,
                    "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        lastResult = rawResult;

        // yuyong
        // ���ؽ��------------for activity
        Intent data = new Intent();
        data.putExtra(Result_text, lastResult.getText());
        setResult(BindingRequestCode, data);
        finish();

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG,
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);

            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
