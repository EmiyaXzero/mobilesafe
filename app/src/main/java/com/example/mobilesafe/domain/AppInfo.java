package com.example.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by abc on 2016/2/14.
 * 应用程序信息的业务bean
 */
public class AppInfo {
    private Drawable icon;
    private String   name ;
    private String   packname;
    private boolean inRom;   //手机内存还是sd卡
    private boolean userApp; //系统应用 用户应用
    private int      uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
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

    @Override
    public String toString() {
        return "AppInfo [name=" + name + ", packname=" + packname + ", inRom="
                + inRom + ", userApp=" + userApp + "]";
    }
}
