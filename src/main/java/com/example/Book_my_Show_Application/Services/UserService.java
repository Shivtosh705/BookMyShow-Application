package com.example.Book_my_Show_Application.Services;


import com.example.Book_my_Show_Application.Entities.UserEntity;
import com.example.Book_my_Show_Application.EntryDtos.UserEntryDto;
import com.example.Book_my_Show_Application.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void addUser(UserEntryDto userEntryDto){

        //Here we need to convert and save.

        /*
        old method: create an object and set attributes
         */

        //new method to create objects using builder()

        UserEntity userEntity = UserEntity.builder().age(userEntryDto.getAge()).name(userEntryDto.getName())
                .address(userEntryDto.getAddress()).email(userEntryDto.getEmail()).mobNo(userEntryDto.getMobNo()).build();

        userRepository.save(userEntity);
        //This is set all of the attribute in one go.
    }
}
