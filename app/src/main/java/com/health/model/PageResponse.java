package com.Health.model;

import java.util.ArrayList;
import java.util.List;

public class PageResponse<T> {

    private List<T> records;
    private long total;
    private int page;
    private int pageSize;

    public List<T> getRecords() {
        return records == null ? new ArrayList<>() : records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
