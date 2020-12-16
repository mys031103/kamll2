package com.kgc.kmall.user.mapper;

import com.kgc.kmall.bean.Member_Receive_Address;
import com.kgc.kmall.bean.Member_Receive_AddressExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface Member_Receive_AddressMapper {
    int countByExample(Member_Receive_AddressExample example);

    int deleteByExample(Member_Receive_AddressExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Member_Receive_Address record);

    int insertSelective(Member_Receive_Address record);

    List<Member_Receive_Address> selectByExample(Member_Receive_AddressExample example);

    Member_Receive_Address selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Member_Receive_Address record, @Param("example") Member_Receive_AddressExample example);

    int updateByExample(@Param("record") Member_Receive_Address record, @Param("example") Member_Receive_AddressExample example);

    int updateByPrimaryKeySelective(Member_Receive_Address record);

    int updateByPrimaryKey(Member_Receive_Address record);
}