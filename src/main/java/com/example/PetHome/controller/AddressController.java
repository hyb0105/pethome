package com.example.PetHome.controller;

import com.example.PetHome.entity.Address;
import com.example.PetHome.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
@PreAuthorize("isAuthenticated()") // 所有地址操作都需要登录
public class AddressController {

    @Autowired
    private AddressService addressService;

    // 获取当前用户的所有地址
    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses(Principal principal) {
        List<Address> addresses = addressService.getUserAddresses(principal.getName());
        return ResponseEntity.ok(addresses);
    }

    // 添加新地址
    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody Address address, Principal principal) {
        Address createdAddress = addressService.addAddress(principal.getName(), address);
        if (createdAddress != null) {
            return ResponseEntity.ok(createdAddress);
        }
        // 一般来说，只要 principal 有效，user 就能找到，除非用户被异常删除
        return ResponseEntity.status(500).build();
    }

    // 更新指定ID的地址
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Integer id, @RequestBody Address addressUpdates, Principal principal) {
        Address updatedAddress = addressService.updateAddress(principal.getName(), id, addressUpdates);
        if (updatedAddress != null) {
            return ResponseEntity.ok(updatedAddress);
        }
        return ResponseEntity.notFound().build(); // 地址不存在或不属于该用户
    }

    // 删除指定ID的地址
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Integer id, Principal principal) {
        boolean success = addressService.deleteAddress(principal.getName(), id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "地址删除成功"));
        }
        return ResponseEntity.notFound().build(); // 地址不存在或不属于该用户
    }

    // 设置指定ID的地址为默认地址
    @PutMapping("/{id}/default")
    public ResponseEntity<?> setDefaultAddress(@PathVariable Integer id, Principal principal) {
        boolean success = addressService.setDefaultAddress(principal.getName(), id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "默认地址设置成功"));
        }
        // 如果地址不存在或不属于该用户，setDefaultAddress 会返回 false
        return ResponseEntity.notFound().build();
    }
}