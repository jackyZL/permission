package com.zl.dao;

import com.zl.beans.PageQuery;
import com.zl.dto.SearchLogDto;
import com.zl.model.SysLog;
import com.zl.model.SysLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jacky
 * @date 2017/10/26
 */
public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    int countBySearchDto(@Param("dto") SearchLogDto dto);

    List<SysLogWithBLOBs> getPageListBySearchDto(@Param("dto") SearchLogDto dto, @Param("page") PageQuery page);
}