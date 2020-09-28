package com.jinjim.mybatis.domain;

public class Pagination {

    private Integer pageNo = 1;

    private Integer pageSize = 20;

    public Integer startIndex() {
        return (getPageNo() - 1) * getPageSize();
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
