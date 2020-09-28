package com.jinjim.mybatis;

import com.jinjim.mybatis.domain.Branch;
import com.jinjim.mybatis.domain.BranchSearchBo;
import java.util.List;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

/**
 * @author jinruhua
 * @date 28/9/2020 13:10
 * @since V1.0
 */
public interface BranchMapper {


    @Select({
            "<script>",
            "    SELECT *",
            "    FROM branch",
            "    <where>",
            "        <if test='mainBranchIds !=null and mainBranchIds.size > 0 '>",
            "            main_branch_id IN",
            "            <foreach collection='mainBranchIds' index='index' item='item' open='(' close=')' separator=','>",
            "                #{item}",
            "            </foreach>",
            "        </if>",
            "        <if test='branchName !=null'>",
            "            AND `name` = #{branchName}",
            "        </if> <if test='nameLike !=null'> AND `name`  like  CONCAT(#{nameLike},'%')   </if>",
            "    </where>",
            "</script>"})
    List<Branch> listBankBranch(BranchSearchBo branchSearchBo);

}
