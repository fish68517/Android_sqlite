package com.personal.diary.model;

public class AdminRecord {
    public long id;
    public long userId;
    public String title;
    public String body;
    public String meta;
    public String extra;

    public AdminRecord(long id, long userId, String title, String body, String meta, String extra) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.meta = meta;
        this.extra = extra;
    }
}
