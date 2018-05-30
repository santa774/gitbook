package com.baban.androidgitbook.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BookHistory {
    @Id(autoincrement = true)
    private Long id;
    private String bookPath;
    private Boolean isFile;
    private Boolean isShowAllPath;
    @Generated(hash = 1092549143)
    public BookHistory(Long id, String bookPath, Boolean isFile,
            Boolean isShowAllPath) {
        this.id = id;
        this.bookPath = bookPath;
        this.isFile = isFile;
        this.isShowAllPath = isShowAllPath;
    }
    @Generated(hash = 1404467852)
    public BookHistory() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBookPath() {
        return this.bookPath;
    }
    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }
    public Boolean getIsFile() {
        return this.isFile;
    }
    public void setIsFile(Boolean isFile) {
        this.isFile = isFile;
    }

    @Override
    public String toString() {
        return "BookHistory{" +
                "id=" + id +
                ", bookPath='" + bookPath + '\'' +
                ", isFile=" + isFile +
                '}';
    }
    public Boolean getIsShowAllPath() {
        return this.isShowAllPath;
    }
    public void setIsShowAllPath(Boolean isShowAllPath) {
        this.isShowAllPath = isShowAllPath;
    }
}
