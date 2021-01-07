package com.leng.hiddencamera.zipFiles;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.DCPubic;

/**
 * Created by DuoNuo on 2017/1/17.
 */





public class AlertActivity extends Activity {

    private TextView showMessage;
    private Button okButton;
    public static String MESSAGE="?洢?????????";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_activity);
        this.setFinishOnTouchOutside(false);  //?????????????????
        showMessage= (TextView) findViewById(R.id.show_message);
        showMessage.setText(MESSAGE);
        okButton= (Button) findViewById(R.id.btn_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DCPubic.RECORD_DIALOG=1;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}