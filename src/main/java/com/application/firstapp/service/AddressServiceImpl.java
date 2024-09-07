package com.application.firstapp.service;

import com.application.firstapp.exception.APIException;
import com.application.firstapp.exception.ResourceNotFoundException;
import com.application.firstapp.model.Address;
import com.application.firstapp.model.User;
import com.application.firstapp.payload.AddressDTO;
import com.application.firstapp.repository.AddressRepository;
import com.application.firstapp.repository.UserRepository;
import com.application.firstapp.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    AuthUtil authUtill;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        User loggedInUser = authUtill.loggedInUser();
        Address address = modelMapper.map(addressDTO,Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addressList = addressRepository.findAll();
        List<AddressDTO> addressDTOList= addressList.stream().map(address-> modelMapper
                .map(address,AddressDTO.class))
                .collect(Collectors.toList());
        return addressDTOList;
    }

    @Override
    public AddressDTO getAddressByAddressId(Long addressId) {

        Address address = addressRepository.findById(addressId).
            orElseThrow(()->new ResourceNotFoundException("address","addressId",addressId));
            return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getLoggedInUserAddress(User user){

        List<Address>  addressList  =  user.getAddresses();
        List<AddressDTO> addressDTOList = addressList.stream().map(
                address-> modelMapper.map(address,AddressDTO.class)
        ).toList();

        return  addressDTOList;
    }


    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDB = addressRepository.findById(addressId).orElseThrow(
                ()->new ResourceNotFoundException("address","addressId",addressId)
        );
        addressFromDB.setCity(addressDTO.getCity());
        addressFromDB.setBuildingName(addressDTO.getBuildingName());
        addressFromDB.setCountry(addressDTO.getCountry());
        addressFromDB.setStreet(addressDTO.getStreet());
        addressFromDB.setState(addressDTO.getState());
        addressFromDB.setPincode(addressDTO.getPincode());
        Address updatedAddress = addressRepository.save(addressFromDB);

        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(addressFromDB,AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(()->new APIException("Address not found to delete"));

        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDB);


        return "Address Deleted Successfully with address Id  "+addressId;
    }
}
