package com.example.Book_my_Show_Application.Services;

import com.example.Book_my_Show_Application.Convertors.TheaterConvertors;
import com.example.Book_my_Show_Application.Entities.TheaterEntity;
import com.example.Book_my_Show_Application.Entities.TheaterSeatEntity;
import com.example.Book_my_Show_Application.EntryDtos.TheaterEntryDto;
import com.example.Book_my_Show_Application.Enums.SeatType;
import com.example.Book_my_Show_Application.Repository.TheaterRepository;
import com.example.Book_my_Show_Application.Repository.TheaterSeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TheaterService {

    @Autowired
    TheaterSeatRepository theaterSeatRepository;


    @Autowired
    TheaterRepository theaterRepository;

    public String addTheater(TheaterEntryDto theaterEntryDto)throws Exception{


        //Do some validations:

        if(theaterEntryDto.getName()==null || theaterEntryDto.getLocation()==null){
            throw new Exception("Name and location should be valid");
        }

        /*
        1.create theaterSeats
        2.I need to save theater:I need theaterEntity
        3.Always set the attribute before saving.
         */

        TheaterEntity theaterEntity = TheaterConvertors.convertDtoToEntity(theaterEntryDto);

        List<TheaterSeatEntity>theaterSeatEntityList = createTheaterSeats(theaterEntryDto,theaterEntity);

        theaterEntity.setTheaterSeatEntityList(theaterSeatEntityList);

        theaterRepository.save(theaterEntity);

        return "Theater added successfully";
    }

    private List<TheaterSeatEntity>createTheaterSeats(TheaterEntryDto theaterEntryDto,TheaterEntity theaterEntity){


        int noClassicSeats = theaterEntryDto.getClassicSeatsCount();
        int noPremiumSeats = theaterEntryDto.getPremiumSeatsCount();

        List<TheaterSeatEntity>theaterSeatEntityList=new ArrayList<>();

        //Created the classic seats
        for(int count=1;count<=noClassicSeats;count++){

            //we need to make a new TheaterSeatEntity

            TheaterSeatEntity theaterSeatEntity = TheaterSeatEntity.builder()
                    .seatType(SeatType.CLASSIC).seatNo(count+"C").theaterEntity(theaterEntity).build();

            theaterSeatEntityList.add(theaterSeatEntity);
        }

        //create the premium seats
        for(int count=1;count<=noPremiumSeats;count++){

            TheaterSeatEntity theaterSeatEntity=TheaterSeatEntity.builder()
                    .seatType(SeatType.PREMIUM).seatNo(count+"P").theaterEntity(theaterEntity).build();

            theaterSeatEntityList.add(theaterSeatEntity);
        }



        return theaterSeatEntityList;

    }

}
