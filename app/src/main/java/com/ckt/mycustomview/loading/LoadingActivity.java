package com.ckt.mycustomview.loading;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ckt.mycustomview.R;

/**
 * Created by D22434 on 2017/12/28.
 */

public class LoadingActivity extends AppCompatActivity {

    ShapeLoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loadingDialog = new ShapeLoadingDialog.Builder(this)
                .loadText("加载中...")
                .build();

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
            }
        });
    }
}
