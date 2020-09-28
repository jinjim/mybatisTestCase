package com.jinjim.mybatis.domain;

import java.io.Serializable;
import java.util.List;


/**
 * Created on 2020/4/9.
 *
 * @author jinruhua
 */
public class BranchSearchBo extends Pagination implements Serializable {

    private static final long serialVersionUID = 1436304135289522890L;

    private String branchNo;

    private String branchName;

    private List<Integer> mainBranchIds;

    private String nameLike;

    private String matchName;


    public String getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(String branchNo) {
        this.branchNo = branchNo;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public List<Integer> getMainBranchIds() {
        return mainBranchIds;
    }

    public void setMainBranchIds(List<Integer> mainBranchIds) {
        this.mainBranchIds = mainBranchIds;
    }

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }
}

