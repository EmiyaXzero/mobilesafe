package com.example.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by abc on 2016/2/15.
 * 进程信息的业务bean
 */
public class TaskInfo {
    private Drawable icon;
    private String name;
    private String packname;
    private Long memsize;
    private boolean userTask;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public Long getMemsize() {
        return memsize;
    }

    public void setMemsize(Long memsize) {
        this.memsize = memsize;
    }

    public boolean isUserTask() {
        return userTask;
    }

    public void setUserTask(boolean userTask) {
        this.userTask = userTask;
    }
}
