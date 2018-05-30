package com.baban.androidgitbook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PropertiesUtil {

    private static final String fileName = "living_prop";

    private SharedPreferences sp;
    private Editor editor;

    private static PropertiesUtil INSTANCE = null;

    public static PropertiesUtil getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new PropertiesUtil(context);
        }
        return INSTANCE;
    }

    private PropertiesUtil(Context context) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void remove(SpKey e) {
        editor.remove(e.getText());
        editor.commit();
    }

    public void removeAll() {
        editor.clear();
        editor.commit();
    }

    public void setString(SpKey e, String value) {
        editor.putString(e.getText(), value);
        editor.commit();
    }

    public String getString(SpKey e, String defValue) {
        return sp.getString(e.getText(), defValue);
    }

    public void setString(String e, String value) {
        editor.putString(e, value);
        editor.commit();
    }

    public String getString(String e, String defValue) {
        return sp.getString(e, defValue);
    }

    public void setInt(SpKey e, int value) {
        editor.putInt(e.getText(), value);
        editor.commit();
    }

    public int getInt(SpKey e, int defValue) {
        return sp.getInt(e.getText(), defValue);
    }

    public void setLong(SpKey e, long value) {
        editor.putLong(e.getText(), value);
        editor.commit();
    }

    public long getLong(SpKey e, long defValue) {
        return sp.getLong(e.getText(), defValue);
    }

    public void setLong(String e, long value) {
        editor.putLong(e, value);
        editor.commit();
    }

    public long getLong(String e, long defValue) {
        return sp.getLong(e, defValue);
    }

    public void setBoolean(SpKey e, boolean value) {
        editor.putBoolean(e.getText(), value);
        editor.commit();
    }

    public boolean getBoolean(SpKey e, boolean defValue) {
        return sp.getBoolean(e.getText(), defValue);
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }


    //	public void setString(String key,String value){
//		editor.putString(key, value);
//		editor.commit();
//	}
//	
//	public String getString(String key,String defValue){
//		return sp.getString(key, defValue);
//	}
    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }


    /**
     * 设置敏感词数据库版本
     *
     * @return
     */
    public void setDBversion(int version) {
        sp.edit().putInt("keywordsdbversion", version).commit();
    }

    /**
     * 返回敏感词数据库版本
     *
     * @return
     */
    public int getDBversion() {
        return sp.getInt("keywordsdbversion", 1);
    }

    /**
     * 保存敏感词数据库更新内容
     *
     * @return
     */
    public void setDBcontent(String dBcontent) {
        if (dBcontent != null) {
            sp.edit().putString("keywordscontent", dBcontent).commit();
        }
    }

    /**
     * 获得敏感词数据库更新内容
     *
     * @return
     */
    public String getDBcontent() {
        return sp.getString("keywordscontent", null);
    }


    /**
     * 保存黑名单
     *
     * @return
     */
    public void setBlackList(String blacklist) {
        if (blacklist != null) {
            sp.edit().putString("blacklist", blacklist).commit();
        }
    }

    /**
     * 获得黑名单
     *
     * @return
     */
    public String getBlackList() {
        return sp.getString("blacklist", null);
    }

    /**
     * 设置设备编号
     *
     * @return
     */
    public void setDevice_token(String dBcontent) {
        if (dBcontent != null) {
            sp.edit().putString("device_token", dBcontent).commit();
        }
    }

    /**
     * 获得设备编号
     *
     * @return
     */
    public String getDevice_token() {
        return sp.getString("device_token", "");
    }

    public void clear() {
//        editor.remove(SpKey.MYUSID.getText());
        editor.commit();
    }


    public static enum SpKey {
        SELECT_PATH_HISTROY("select_path_histroy"),
        LAST_SELECT_PATH("last_select_path"),;
        String text;

        private SpKey(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }
}


