package com.baban.androidgitbook.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baban.androidgitbook.Constant;
import com.baban.androidgitbook.R;
import com.baban.androidgitbook.adater.SwipeRecyclerViewAdapter;
import com.baban.androidgitbook.bean.BookHistory;
import com.baban.androidgitbook.utils.FileUtil;
import com.baban.androidgitbook.utils.GreenDaoUtil;
import com.baban.androidgitbook.utils.PropertiesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;


public class MainActivity extends BaseActivity implements BGAOnRVItemClickListener, BGAOnItemChildClickListener {

    @Bind(R.id.button)
    ImageView button;
    @Bind(R.id.dir_path)
    EditText dir_path;
    @Bind(R.id.history_path)
    TextView history_path;
    @Bind(R.id.icon_folder)
    ImageView icon_folder;
    @Bind(R.id.history_recyclerview)
    RecyclerView history_recyclerview;

    SwipeRecyclerViewAdapter fileListAdapter;
    private static final int FILE_SELECT_CODE = 110;
    private static final int FOLDER_SELECT_CODE = 111;
    private List<BookHistory> history_path_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        setSwipeBackEnable(false);  //禁用侧滑
    }

    private void initView() {
        fileListAdapter = new SwipeRecyclerViewAdapter(history_recyclerview);
        fileListAdapter.setData(history_path_list);
        fileListAdapter.setOnRVItemClickListener(this);
        fileListAdapter.setOnItemChildClickListener(this);
        history_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    fileListAdapter.closeOpenedSwipeItemLayoutWithAnim();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        history_recyclerview.setLayoutManager(layoutManager);
        history_recyclerview.setAdapter(fileListAdapter);
    }

    private void initData() {
        history_path_list = GreenDaoUtil.queryAll();
        fileListAdapter.setData(history_path_list);
    }

    private void showSystemFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @OnClick({R.id.button, R.id.history_path, R.id.icon_folder})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                String path = dir_path.getText().toString();
                if (TextUtils.isEmpty(path)) {
                    Toast.makeText(MainActivity.this, "路径无效", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!new File(path).isFile() || !path.contains("index.html")) {
                    Toast.makeText(MainActivity.this, "只能读取index.html", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 保存这次选择的路径
                PropertiesUtil.getInstance(MainActivity.this).setString(PropertiesUtil.SpKey.LAST_SELECT_PATH, dir_path.getText() + "");
                // 保存路径到历史记录
                GreenDaoUtil.add(path);

                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra(Constant.SELECT_GITBOOK_DIRPATH, path);
                startActivity(intent);
                break;

            case R.id.icon_folder:
                startActivityForResult(new Intent(MainActivity.this, FileDirManagerMainActivity.class), FOLDER_SELECT_CODE);
                break;

            case R.id.history_path:
                setDirPath(history_path.getText().toString());
                break;
        }
    }

    private void setDirPath(String path) {
        dir_path.setText(path);
        dir_path.setSelection(path.length());//将光标移至文字末尾
    }

    private static final String TAG = "ChooseFile";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FOLDER_SELECT_CODE:
                if (resultCode == Constant.FILE_SELECT_CODE_RESULT) {
                    String chooseDirPath = data.getExtras().getString("file");
                    setDirPath(chooseDirPath);
                    Log.d(TAG, chooseDirPath);
                }
                break;

            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = FileUtil.getPath(this, uri);
                    Log.d(TAG, "File Path: " + path);
                    setDirPath(path);
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getFileDir(String filePath) {
        File f = new File(filePath);
        File[] files = f.listFiles();
        //判断当前下是否有文件夹
        if (files.length <= 0) {
            Toast.makeText(MainActivity.this, "目录为空", Toast.LENGTH_SHORT).show();
            return;
        }
        history_path_list.clear();
        for (int i = 0; i < files.length; i++) {
            //过滤一遍
            //1.是否为文件夹
            //2.是否可访问
            if (files[i].isDirectory() && files[i].listFiles() != null) {
//                history_path_list.add(files[i].getPath());
            }
        }
//        setListAdapter(new FileDirListAdapter(this, history_path_list));
    }


    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
        intent.putExtra(Constant.SELECT_GITBOOK_DIRPATH, history_path_list.get(position).getBookPath());
        startActivity(intent);
    }

//    @Override
//    public boolean onRVItemLongClick(ViewGroup parent, View itemView, int position) {
//        Log.e("MainActivity", "onRVItemLongClick");
//        if (history_path_list != null) {
//            history_path_list.remove(position);
//            fileListAdapter.notifyDataSetChanged();
//        }
//        return false;
//    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (childView.getId() == R.id.tv_item_swipe_delete) {
            //删除历史记录
            GreenDaoUtil.delete(history_path_list.get(position).getBookPath());
            //删除RecycleView Item
            fileListAdapter.removeItem(position);
        }
    }
}
