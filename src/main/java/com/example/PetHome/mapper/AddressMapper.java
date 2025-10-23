package com.example.PetHome.mapper;

import com.example.PetHome.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AddressMapper {
    int insertAddress(Address address);
    int updateAddress(Address address);
    int deleteAddressById(@Param("id") Integer id, @Param("userId") Integer userId);
    Address findAddressById(@Param("id") Integer id, @Param("userId") Integer userId);
    List<Address> findAddressesByUserId(@Param("userId") Integer userId);
    int clearDefaultAddress(@Param("userId") Integer userId);
    int setDefaultAddress(@Param("id") Integer id, @Param("userId") Integer userId);
}