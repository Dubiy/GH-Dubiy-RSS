package ua.ck.geekhub.android.dubiy.rssreader.entity;

import java.util.ArrayList;

import ua.ck.geekhub.android.dubiy.rssreader.utils.BaseClass;

public class HabraPost extends BaseClass {
    private String title;
    private String link;
    private String publishDate;
    private String shortContent;
    private String content;

    public HabraPost() {
        this.title = "HabraReader";
        this.link = "http://garik.pp.ua";
        this.publishDate = "";
        this.shortContent = "";
        this.content = "";
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getShortContent() {
        return shortContent;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public void setShortContent(String shortContent) {
        this.shortContent = shortContent;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
