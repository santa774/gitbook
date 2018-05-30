package com.baban.androidgitbook.utils;

import android.util.Log;

import com.baban.androidgitbook.MyApplication;
import com.baban.androidgitbook.bean.BookHistory;
import com.baban.androidgitbook.bean.BookHistoryDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.List;

public class GreenDaoUtil {
    public static final String TAG = "GreenDaoUtil";

    public static void add(String path) {
        List<BookHistory> book_list = queryAll();
        if (book_list != null && book_list.size() > 0) {
            boolean isNeedAdd = true;
            for (BookHistory book : book_list) {
                if (path.equals(book.getBookPath())) {
                    // 去重判断
                    isNeedAdd = false;
                }
            }
            if (isNeedAdd)
                MyApplication.getInstances().getDaoSession().insert(new BookHistory(null, path, new File(path).isFile(), true));
        } else {
            MyApplication.getInstances().getDaoSession().insert(new BookHistory(null, path, new File(path).isFile(), true));
        }

        Log.i(TAG, "添加成功");
    }

    public static void delete(String path) {
        BookHistoryDao bookHistoryDao = MyApplication.getInstances().getDaoSession().getBookHistoryDao();
        List<BookHistory> list = bookHistoryDao.queryBuilder().list();

        if (list == null || list.isEmpty()) {
            return;
        }
        for (int j = 0; j < list.size(); j++) {
            BookHistory bookHistory = list.get(j);
            if (path.equals(bookHistory.getBookPath())) {
                bookHistoryDao.delete(bookHistory);
                Log.i(TAG, "正在删除" + path);
            }
        }
        Log.i(TAG, "删除成功");
    }

    public static void update(BookHistory bookHistory) {

        BookHistoryDao userDao = MyApplication.getInstances().getDaoSession().getBookHistoryDao();
        QueryBuilder<BookHistory> builder = userDao.queryBuilder();
        List<BookHistory> zhangsan = builder.where(BookHistoryDao.Properties.BookPath.eq(bookHistory.getBookPath())).list();
        if (zhangsan == null || zhangsan.isEmpty()) {
            return;
        }
        for (int i = 0; i < zhangsan.size(); i++) {
            BookHistory history = zhangsan.get(i);
            history.setIsFile(bookHistory.getIsFile());
            MyApplication.getInstances().getDaoSession().update(history);
            Log.i(TAG, "修改成功");
        }
        List<BookHistory> list = MyApplication.getInstances().getDaoSession().getBookHistoryDao().queryBuilder().list();

        if (list == null || list.isEmpty()) {
            Log.i(TAG, "修改方法的暂无数据");
            return;
        }
        for (int j = 0; j < list.size(); j++) {
            BookHistory book = list.get(j);
            Log.i(TAG, book.toString());
        }
    }


    public static List<BookHistory> queryAll() {
        BookHistoryDao userDao = MyApplication.getInstances().getDaoSession().getBookHistoryDao();
        QueryBuilder<BookHistory> builder = userDao.queryBuilder();
        List<BookHistory> list = builder.list();
        if (list == null || list.isEmpty()) {
            Log.i(TAG, "查询所有数据为空");
            return null;
        } else {
            for (int i = 0; i < list.size(); i++) {
                Log.i(TAG, "查询allName : " + list.get(i).toString());
            }
            return list;
        }
    }
}
