package com.baban.androidgitbook.activity;

import android.os.Bundle;

import com.baban.androidgitbook.Constant;
import com.baban.androidgitbook.activity.BaseBrowserActivity;

public class BookDetailActivity extends BaseBrowserActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mHomeUrl = "file:///android_asset/book/linux_base/index.html";
        mHomeUrl = "file://" + getIntent().getStringExtra(Constant.SELECT_GITBOOK_DIRPATH);
    }
}
