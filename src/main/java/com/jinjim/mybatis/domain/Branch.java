package com.jinjim.mybatis.domain;

import java.io.Serializable;

/**
 * @author jinruhua
 * @date 28/9/2020 13:07
 * @since V1.0
 */
public class Branch implements Serializable {

    private static final long serialVersionUID = -953229990504952619L;

    private Integer id;

    private String branchNo;

    private String branchName;

    private Integer mainBranId;
}
