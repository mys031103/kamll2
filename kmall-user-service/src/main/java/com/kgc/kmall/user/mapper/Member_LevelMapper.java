package com.kgc.kmall.user.mapper;

import com.kgc.kmall.bean.Member_Level;
import com.kgc.kmall.bean.Member_LevelExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface Member_LevelMapper {
    int countByExample(Member_LevelExample example);

    int deleteByExample(Member_LevelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Member_Level record);

    int insertSelective(Member_Level record);

    List<Member_Level> selectByExample(Member_LevelExample example);

    Member_Level selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Member_Level record, @Param("example") Member_LevelExample example);

    int updateByExample(@Param("record") Member_Level record, @Param("example") Member_LevelExample example);

    int updateByPrimaryKeySelective(Member_Level record);

    int updateByPrimaryKey(Member_Level record);
}