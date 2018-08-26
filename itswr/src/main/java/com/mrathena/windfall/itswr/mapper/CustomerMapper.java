package com.mrathena.windfall.itswr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mrathena.windfall.itswr.entity.Customer;

@Mapper
public interface CustomerMapper {

	/** itswr_customer */
	int deleteByPrimaryKey(Long id);

	/** itswr_customer */
	int insert(Customer record);

	/** itswr_customer */
	int insertSelective(Customer record);

	/** itswr_customer */
	Customer selectByPrimaryKey(Long id);

	/** itswr_customer */
	int updateByPrimaryKeySelective(Customer record);

	/** itswr_customer */
	int updateByPrimaryKey(Customer record);

	List<Customer> selectByStartNo(@Param("startNo") String startNo);

	Customer selectByNo(@Param("no") String no);

	List<Customer> selectByStartNoAndCount(@Param("startNo") String startNo, @Param("count") int count);

}