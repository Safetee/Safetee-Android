package com.getsafetee.model;

import java.util.ArrayList;

public class GetRecords {
    private String title;
    private String thumbnailUrl;
    private String created;
    private String remark;
    private ArrayList<String> category;
    private String audio;

    public GetRecords() {
    }

    public GetRecords(String name, String thumbnailUrl, String created, String remark,
                 ArrayList<String> category, String audio) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
        this.created = created;
        this.remark = remark;
        this.category = category;
        this.audio = audio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public String getAudio(){ return audio;}

    public void setAudio(String audio){ this.audio = audio; }

}
