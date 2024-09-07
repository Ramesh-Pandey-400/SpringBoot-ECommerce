package com.application.firstapp.controller;

import com.application.firstapp.model.Address;
import com.application.firstapp.model.User;
import com.application.firstapp.payload.AddressDTO;
import com.application.firstapp.service.AddressService;
import com.application.firstapp.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid  @RequestBody AddressDTO addressDTO){
        User user = authUtil.loggedInUser();
        AddressDTO savedAdressDTO = addressService.createAddress(addressDTO,user);
        return  new ResponseEntity<>(savedAdressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> createAddress(){
        List<AddressDTO> addressDTOList = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOList,HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressByAddressId(@PathVariable Long addressId){
        AddressDTO addressDTO = addressService.getAddressByAddressId(addressId);
        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getLoggedInUserAddress(){
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTOList = addressService.getLoggedInUserAddress(user);
        return new ResponseEntity<>(addressDTOList,HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId,@RequestBody AddressDTO addressDTO){
        AddressDTO updatedAddress = addressService.updateAddress(addressId,addressDTO);
        return new ResponseEntity<>(updatedAddress,HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status= addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }
}
