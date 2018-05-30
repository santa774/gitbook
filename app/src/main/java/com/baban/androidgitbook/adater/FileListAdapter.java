package com.baban.androidgitbook.adater;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.baban.androidgitbook.R;
import com.baban.androidgitbook.bean.FileType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * Created by Administrator on 2018/3/8 0008.
 */

public class FileListAdapter extends BGARecyclerViewAdapter<FileType> {
    Activity activity;

    public FileListAdapter(RecyclerView recyclerView, Activity activity) {
        super(recyclerView, R.layout.file_row);
        this.activity = activity;
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, FileType fileType) {
        if (fileType.isShowAllPath()) {
            helper.setText(R.id.text, fileType.getPath());
        } else {
            helper.setText(R.id.text, new File(fileType.getPath()).getName());
        }

        if (fileType.isFile()) {
            helper.setImageResource(R.id.icon, R.mipmap.icon_file);
        } else {
            helper.setImageResource(R.id.icon, R.mipmap.icon_folder);
        }
    }

    @Override
    protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
        super.setItemChildListener(helper, viewType);
    }
}
