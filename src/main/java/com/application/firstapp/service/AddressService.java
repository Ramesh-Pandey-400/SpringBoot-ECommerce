package com.application.firstapp.service;

import com.application.firstapp.model.User;
import com.application.firstapp.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressByAddressId(Long addressId);

    List<AddressDTO> getLoggedInUserAddress(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
