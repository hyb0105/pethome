package com.example.PetHome.service;

import com.example.PetHome.entity.Address;
import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.AddressMapper;
import com.example.PetHome.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserMapper userMapper; // 需要 UserMapper 来根据用户名获取 userId

    // 获取当前用户的地址列表
    public List<Address> getUserAddresses(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null; // 或者返回空列表
        }
        return addressMapper.findAddressesByUserId(user.getId());
    }

    // 添加新地址
    @Transactional
    public Address addAddress(String username, Address address) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        address.setUserId(user.getId());

        // 如果新地址设为默认，先清除旧的默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.clearDefaultAddress(user.getId());
        } else {
            // 确保 isDefault 不是 null，如果前端没传，默认为 0
            address.setIsDefault(0);
        }

        addressMapper.insertAddress(address);
        return address; // 返回包含生成 ID 的地址对象
    }

    // 更新地址
    @Transactional
    public Address updateAddress(String username, Integer addressId, Address addressUpdates) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        // 验证该地址是否属于当前用户
        Address existingAddress = addressMapper.findAddressById(addressId, user.getId());
        if (existingAddress == null) {
            return null; // 地址不存在或不属于该用户
        }

        addressUpdates.setId(addressId);
        addressUpdates.setUserId(user.getId()); // 确保 userId 不被篡改

        // 处理默认地址逻辑
        if (addressUpdates.getIsDefault() != null && addressUpdates.getIsDefault() == 1) {
            // 如果将此地址设为默认，先清除其他默认地址
            addressMapper.clearDefaultAddress(user.getId());
        } else {
            // 如果更新后不是默认，确保 isDefault 为 0
            addressUpdates.setIsDefault(0);
            // 注意：如果原本是默认地址，更新后变成非默认，需要确保用户至少有一个默认地址（如果业务需要）
            // 这里简化处理，允许用户没有默认地址
        }


        addressMapper.updateAddress(addressUpdates);
        return addressUpdates;
    }

    // 删除地址
    public boolean deleteAddress(String username, Integer addressId) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return false;
        }
        int affectedRows = addressMapper.deleteAddressById(addressId, user.getId());
        return affectedRows > 0;
    }

    // 设置默认地址
    @Transactional
    public boolean setDefaultAddress(String username, Integer addressId) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return false;
        }
        // 1. 清除当前用户的所有默认地址标志
        addressMapper.clearDefaultAddress(user.getId());
        // 2. 将指定地址设为默认
        int affectedRows = addressMapper.setDefaultAddress(addressId, user.getId());
        return affectedRows > 0;
    }
}