package com.example.zobaer53.floatingwindow;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    String[] packageName;
    String[] appName;
    Drawable[] img;
    String[] newAppName;
    String[] newPackageName;
    Drawable[] newImg;
    ScrollView scrollView;
    Button button;
    WindowManager windowManager;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = preferences.edit();
        Log.i("Ftag", "OnStart = " + preferences.getInt("first_launch", -1));
        installedApps();
        requestPermission();

        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            showWindowManager();
        });
    }

    @SuppressLint("LongLogTag")
    private void installedApps() {
        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packageInfoList = getPackageManager().getInstalledPackages(0);

        appName = new String[packageInfoList.size()];
        packageName = new String[packageInfoList.size()];
        img = new Drawable[packageInfoList.size()];

        for (int i = 0; i < packageInfoList.size(); i++) {

            PackageInfo packageInfo = packageInfoList.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {


                appName[i] = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                packageName[i] = packageInfo.packageName;

                try {

                    img[i] = getApplicationContext().getPackageManager().getApplicationIcon(packageName[i]);

                } catch (PackageManager.NameNotFoundException ne) {

                }
            }
        }

        NewAppName();
        NewPackageName();
        NewImg();

        Log.i("1AppName + PackageName + icon", Arrays.toString(newAppName) +
                "---" + Arrays.toString(newPackageName) + "---" + Arrays.toString(newImg));

    }

    private void NewImg() {
        int count = 0;
        for (Drawable i : img) {
            if (i != null) {
                count++;
            }
        }


        newImg = new Drawable[count];


        int index = 0;
        for (Drawable i : img) {
            if (i != null) {
                newImg[index++] = i;
            }
        }

    }

    private void NewPackageName() {
        int count = 0;
        for (String i : packageName) {
            if (i != null) {
                count++;
            }
        }


        newPackageName = new String[count];


        int index = 0;
        for (String i : packageName) {
            if (i != null) {
                newPackageName[index++] = i;
            }
        }

    }

    private void NewAppName() {
        int count = 0;
        for (String i : appName) {
            if (i != null) {
                count++;
            }
        }


        newAppName = new String[count];


        int index = 0;
        for (String i : appName) {
            if (i != null) {
                newAppName[index++] = i;
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"ResourceAsColor", "SetTextI18n", "ClickableViewAccessibility", "WrongConstant"})
    public void showWindowManager() {
        if (requestPermission()) {
            return;
        }

        WindowManager.LayoutParams p =
                new WindowManager.LayoutParams(200, 2500,
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.O
                                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                : WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.START;

        WindowManager.LayoutParams p1 =
                new WindowManager.LayoutParams(90, 90,
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.O
                                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                : WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);


        p1.gravity = Gravity.END | Gravity.RIGHT;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LinearLayout popupView = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        popupView.setBackgroundColor(Color.argb(66, 255, 0, 0));
        popupView.setOrientation(LinearLayout.VERTICAL);
        popupView.setLayoutParams(layoutParams);

        if (newAppName.length == newImg.length) {

            for (int i = 0; i < newAppName.length; i++) {

                ImageView imageView = new ImageView(this);
                ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(
                        200,
                        100
                );

                imageView.setImageDrawable(newImg[i]);
                imageView.setLayoutParams(imageParams);
                int finalI = i;
                imageView.setOnClickListener(v -> {

                    if (newPackageName[finalI].equals("com.example.zobaer53.floatingwindow")) {

                        windowManager.removeView(scrollView);
                        windowManager.removeView(button);


                        Intent intent = getPackageManager().getLaunchIntentForPackage(newPackageName[finalI]);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        ActivityOptions ao = ActivityOptions.makeBasic();
                        Rect rect = new Rect(0, 0, 0, 100);
                        ActivityOptions bounds = ao.setLaunchBounds(rect);
                        startActivity(intent, bounds.toBundle());
                        finish();
                    } else {
                        Intent intent = getPackageManager().getLaunchIntentForPackage(newPackageName[finalI]);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        ActivityOptions ao = ActivityOptions.makeBasic();
                        Rect rect = new Rect(0, 0, 0, 100);
                        ActivityOptions bounds = ao.setLaunchBounds(rect);
                        startActivity(intent, bounds.toBundle());
                        finish();


                    }

                    windowManager.removeView(scrollView);
                    button.setVisibility(View.VISIBLE);
                    button.setBackgroundResource(R.drawable.power1);

                    button.setOnTouchListener(new View.OnTouchListener() {
                        private int initialX;
                        private int initialY;
                        private float initialTouchX;
                        private float initialTouchY;


                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            button.setBackgroundResource(R.drawable.power);
                            if (event.getEventTime() - event.getDownTime() < 100 && event.getActionMasked() == MotionEvent.ACTION_UP) {
                                v.performClick();
                                windowManager.removeView(button);
                                showWindowManager();

                                return false;
                            }


                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    button.setBackgroundResource(R.drawable.power);
                                    initialX = p1.x;
                                    initialY = p1.y;
                                    initialTouchX = event.getRawX();
                                    initialTouchY = event.getRawY();
                                    break;

                                case MotionEvent.ACTION_UP:
                                    button.setBackgroundResource(R.drawable.power);
                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    button.setBackgroundResource(R.drawable.power1);
                                    p1.x = initialX + (int) (event.getRawX() - initialTouchX);
                                    p1.y = initialY + (int) (event.getRawY() - initialTouchY);
                                    windowManager.updateViewLayout(button, p1);
                                    return true;

                            }

                            return false;
                        }

                    });
                });
                popupView.addView(imageView);

                TextView textView = new TextView(this);
                ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                textView.setGravity(Gravity.CENTER);
                textView.setText(newAppName[i]);
                textView.setTextSize(14);
                textView.setHintTextColor(R.color.black);
                textView.setLayoutParams(textParams);
                popupView.addView(textView);
            }
        }
        scrollView = new ScrollView(this);
        scrollView.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
            return false;
        });
        scrollView.addView(popupView);
        button = new Button(this);
        button.setLayoutParams(p1);
        // button.setText("On");
        button.setBackgroundResource(R.drawable.power);
        button.setVisibility(View.INVISIBLE);

        windowManager.addView(scrollView, p);
        windowManager.addView(button, p1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putInt("first_launch", 2);
        editor.commit();
        Log.i("Ftag", String.valueOf(preferences.getInt("first_launch", -1)));

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                showWindowManager();
            }
        }
    }

    public boolean requestPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            return true;
        }
        return false;
    }

}






