package com.example.exercisefloatingwidgetapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.exercisefloatingwidgetapp.Class.FloatWidgetService;
import com.example.exercisefloatingwidgetapp.R;

public class MainActivity extends AppCompatActivity {

    private static final int APP_PERMISSION_REQUEST = 101;  //Any int used for getting permissions, and this value is passed back to us, so we check it against this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //If our build version is more or equal to Marshmallow (23), we need to get permission to draw overlays
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //Create an intent for what activity we want to start (Getting permission from the user)
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            //Start the intent, passing an int so when it returns through onActivityResult, we know what activity has just finished
            startActivityForResult(intent, APP_PERMISSION_REQUEST);
        }
        //Else start up the app
        else {
            initializeView();
        }
    }

    //Start up the app
    private void initializeView() {
        //Find the button for creating the floating widget
        Button mButton= findViewById(R.id.create_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the floating widget
                startService(new Intent(MainActivity.this, FloatWidgetService.class));
                //Close down the normal app
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If the returned activity was for getting permission, and it was allowed
        if (requestCode == APP_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            initializeView();
        }
        else {
            Toast.makeText(this, "Draw over other app permission not enable.", Toast.LENGTH_SHORT).show();
        }
    }
}
