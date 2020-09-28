//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jinjim.mybatis;

import java.io.Serializable;
import java.util.List;

public class PageSegment<T> implements Serializable {
    private static final ThreadLocal<PageSegment> PAGESEGMENTHOLDER = new ThreadLocal();
    private static final Integer MAX_PAGESIZE = 200;
    private static final Integer MIN_PAGENO = 1;
    private long totalRecords;
    private List<T> records;
    private long pageNo;
    private long pageSize;

    public PageSegment() {
    }

    private PageSegment(Integer pageNo, Integer pageSize) {
        this.pageNo = (long)pageNo < MIN_PAGENO ? MIN_PAGENO : pageNo;
        this.pageSize = (long)pageSize > MAX_PAGESIZE ? MAX_PAGESIZE : pageSize;
    }

    public static PageSegment build(Integer pageNo, Integer pageSize) {
        PageSegment pageSegment = new PageSegment(pageNo, pageSize);
        PAGESEGMENTHOLDER.set(pageSegment);
        return pageSegment;
    }

    public static PageSegment get() {
        return (PageSegment)PAGESEGMENTHOLDER.get();
    }

    public static void clear() {
        PAGESEGMENTHOLDER.remove();
    }

    public long getTotalRecords() {
        return this.totalRecords;
    }

    public long getTotalPages() {
        return (this.totalRecords + this.pageSize - 1L) / this.pageSize;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<T> getRecords() {
        return this.records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public long getTopPageNo() {
        return 1L;
    }

    public long getPreviousPageNo() {
        return this.pageNo <= 1L ? 1L : this.pageNo - 1L;
    }

    public long getNextPageNo() {
        if (this.pageNo >= this.getTotalPages()) {
            return this.getTotalPages() == 0L ? 1L : this.getTotalPages();
        } else {
            return this.pageNo + 1L;
        }
    }

    public long getBottomPageNo() {
        return this.getTotalPages() == 0L ? 1L : this.getTotalPages();
    }

    public long getOffset() {
        return (this.pageNo - 1L) * this.pageSize;
    }
}
