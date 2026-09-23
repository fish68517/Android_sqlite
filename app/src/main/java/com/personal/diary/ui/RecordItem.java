package com.personal.diary.ui;

public class RecordItem {
    public long id;
    public long ownerId;
    public String type;
    public String title;
    public String body;
    public String meta;
    public byte[] imageData;
    public String primaryAction;
    public String secondaryAction;
    public boolean showEdit;
    public boolean showDelete;

    public RecordItem(long id, long ownerId, String type, String title, String body, String meta) {
        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.title = title;
        this.body = body;
        this.meta = meta;
    }
}
