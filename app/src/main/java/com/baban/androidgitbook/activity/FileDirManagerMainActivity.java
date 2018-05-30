package com.baban.androidgitbook.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baban.androidgitbook.Constant;
import com.baban.androidgitbook.R;
import com.baban.androidgitbook.adater.FileListAdapter;
import com.baban.androidgitbook.bean.FileType;
import com.baban.androidgitbook.utils.FileUtil;
import com.baban.androidgitbook.utils.PropertiesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * @author ethan
 * @version 创建时间:  2018/5/28.
 */

@RuntimePermissions
public class FileDirManagerMainActivity extends BaseActivity implements BGAOnRVItemClickListener {
    @Bind(R.id.mPath)
    TextView mPath;
    @Bind(R.id.buttonConfirm)
    FloatingActionButton buttonConfirm;
    @Bind(R.id.file_list)
    RecyclerView file_list;

    FileListAdapter fileListAdapter;
    private List<FileType> paths = new ArrayList<>();
    private String rootPath = FileUtil.getPathFavourSdcard(this);
    private String curPath = FileUtil.getPathFavourSdcard(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager_main);
        ButterKnife.bind(this);
        initView();
        mPath.setEnabled(true);
        // 获取上次打开的路径
        curPath = PropertiesUtil.getInstance(FileDirManagerMainActivity.this).getString(PropertiesUtil.SpKey.LAST_SELECT_PATH, rootPath);
        FileDirManagerMainActivityPermissionsDispatcher.getFileDirByPermissionWithCheck(FileDirManagerMainActivity.this, curPath);
    }

    private void initView() {
        fileListAdapter = new FileListAdapter(file_list, this);
        fileListAdapter.setData(paths);
        fileListAdapter.setOnRVItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        file_list.setLayoutManager(layoutManager);
        file_list.setAdapter(fileListAdapter);
    }

    @OnClick({R.id.mPath, R.id.buttonConfirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mPath:
                FileDirManagerMainActivityPermissionsDispatcher.getFileDirByPermissionWithCheck(FileDirManagerMainActivity.this, new File(curPath).getParent() + File.separator);
                break;
            case R.id.buttonConfirm:
                Intent data = new Intent(FileDirManagerMainActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("file", curPath);
                data.putExtras(bundle);
                setResult(Constant.FILE_SELECT_CODE_RESULT, data);
                finish();
                break;
        }
    }


    private void getFileDir(String filePath) {
        mPath.setText(curPath = filePath);
        //设置向上是否可用
        if (filePath.equals(rootPath))
            mPath.setEnabled(false);
        else
            mPath.setEnabled(true);
        File f = new File(filePath);
        File[] files = f.listFiles();
        if (files == null) return;
        //判断当前下是否有文件夹
        if (files.length <= 0) {
            Toast.makeText(FileDirManagerMainActivity.this, "目录为空", Toast.LENGTH_SHORT).show();
            return;
        }
        paths.clear();
        // 按照文件夹 文件，文件名 排序
        Collections.sort(Arrays.asList(files), new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isDirectory() && f2.isDirectory()) {// 都是目录

                    return f1.getName().compareToIgnoreCase(f2.getName());//都是目录时按照名字排序
                } else if (f1.isDirectory() && f2.isFile()) {//目录与文件.目录在前
                    return -1;
                } else if (f2.isDirectory() && f1.isFile()) {//文件与目录
                    return 1;
                } else {
                    return f1.getName().compareToIgnoreCase(f2.getName());//都是文件
                }
            }
        });
        for (File file : files) {
            paths.add(new FileType(file.isFile(), file.getPath()));
        }
        fileListAdapter.notifyDataSetChanged();
//        setListAdapter(new FileDirListAdapter(this, paths));
    }


    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        if (paths.get(position).isFile()) {
            mPath.setText(curPath = paths.get(position).getPath());
            return;
        }
        this.getFileDir(paths.get(position).getPath());
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void getFileDirByPermission(String filePath) {
        getFileDir(filePath);
    }

    @OnShowRationale({Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForReadState(PermissionRequest request) {

    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onReadStateDenied() {
        Toast.makeText(this, "您已经拒绝申请权限", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onReadStateNeverAskAgain() {
        Toast.makeText(this, "不再询问", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FileDirManagerMainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}