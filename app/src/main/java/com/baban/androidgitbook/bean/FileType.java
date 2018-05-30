package com.baban.androidgitbook.bean;

/**
 * @author ethan
 * @version 创建时间:  2018/5/28.
 */

public class FileType {
    public FileType(boolean isFile, String path) {
        this.isFile = isFile;
        this.path = path;
    }

    public FileType(boolean isFile, String path, boolean isShowAllPath) {
        this.isFile = isFile;
        this.path = path;
        this.isShowAllPath = isShowAllPath;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    boolean isFile;

    public boolean isShowAllPath() {
        return isShowAllPath;
    }

    public void setShowAllPath(boolean showAllPath) {
        isShowAllPath = showAllPath;
    }

    boolean isShowAllPath;
    String path;

}
