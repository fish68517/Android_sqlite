package com.hakimi.model;

import java.util.List;

/**
 * 分页结果通用模型，对应后端返回的 Page 对象
 *
 * @author hakimi
 */
public class PageResult<T> {

    private List<T> records;
    private long total;
    private long size;
    private long current;

    public List<T> getRecords() {
        return records;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }
}


