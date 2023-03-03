package com.example.Book_my_Show_Application.Services;


import com.example.Book_my_Show_Application.Convertors.TicketConvertors;
import com.example.Book_my_Show_Application.Entities.ShowEntity;
import com.example.Book_my_Show_Application.Entities.ShowSeatEntity;
import com.example.Book_my_Show_Application.Entities.TicketEntity;
import com.example.Book_my_Show_Application.Entities.UserEntity;
import com.example.Book_my_Show_Application.EntryDtos.TicketEntryDto;
import com.example.Book_my_Show_Application.Repository.ShowRepository;
import com.example.Book_my_Show_Application.Repository.TicketRepository;
import com.example.Book_my_Show_Application.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ShowRepository showRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender;

    public String addTicket(TicketEntryDto ticketEntryDto)throws Exception{

        //1.Create TicketEntity from entryDto:Convert DTO----->Entity

        TicketEntity ticketEntity = TicketConvertors.convertEntryToEntity(ticketEntryDto);


        //Validation:Check if the requested seats are available or not?
        boolean isValidRequest = checkValidityofRequestedSeats(ticketEntryDto);

        if(isValidRequest==false){
            throw new Exception("Requested seats are not available");
        }

        //We assume that the requestSeats are valid

        //calculate the total amount:
        ShowEntity showEntity = showRepository.findById(ticketEntryDto.getShowId()).get();
        List<ShowSeatEntity>seatEntityList = showEntity.getListOfShowSeats();
        List<String>requestedSeats = ticketEntryDto.getRequestedSeats();

        int totalAmount = 0;
        for(ShowSeatEntity showSeatEntity:seatEntityList){

            if(requestedSeats.contains(showSeatEntity.getSeatNo())){
                totalAmount = totalAmount + showSeatEntity.getPrice();
                showSeatEntity.setBooked(true);
                showSeatEntity.setBookedAt(new Date());
            }
        }

        ticketEntity.setTotalAmount(totalAmount);


        //setting the other attributes for the ticketEntity
        ticketEntity.setMovieName(showEntity.getMovieEntity().getMovieName());
        ticketEntity.setShowDate(showEntity.getShowDate());
        ticketEntity.setShowTime(showEntity.getShowTime());
        ticketEntity.setTheaterName(showEntity.getTheaterEntity().getName());

        //We need to set that string that talked about requested seats
        String allotedSeats = getAllotedSeatsfromShowSeats(requestedSeats);
        ticketEntity.setBookedSeats(allotedSeats);

        //setting the foreign key attributes
        UserEntity userEntity = userRepository.findById(ticketEntryDto.getUserId()).get();

        ticketEntity.setUserEntity(userEntity);
        ticketEntity.setShowEntity(showEntity);

        //save the parent
        ticketEntity = ticketRepository.save(ticketEntity);

        List<TicketEntity>ticketEntityList = showEntity.getListOfBookedTickets();
        ticketEntityList.add(ticketEntity);
        showEntity.setListOfBookedTickets(ticketEntityList);

        showRepository.save(showEntity);

        List<TicketEntity>ticketEntityList1 = userEntity.getBookedTickets();
        ticketEntityList1.add(ticketEntity);
        userEntity.setBookedTickets(ticketEntityList1);

        userRepository.save(userEntity);


        String body ="Hi this is to confirm your booking for seat No"+allotedSeats +"for the movie: " + ticketEntity.getMovieName();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("shivtoshmishra005@gmail.com");
        mimeMessageHelper.setTo(userEntity.getEmail());
        mimeMessageHelper.setText(body);
        mimeMessageHelper.setSubject("Confirming your booked Ticket..");

        javaMailSender.send(mimeMessage);

        return "Ticket has successfully been added";

    }

    private String getAllotedSeatsfromShowSeats(List<String>requestedSeats){

        String result ="";

        for(String seat:requestedSeats){

            result = result + seat+", ";
        }

        return result;

    }

    private boolean checkValidityofRequestedSeats(TicketEntryDto ticketEntryDto){

        int showId = ticketEntryDto.getShowId();

        List<String>requestedSeats = ticketEntryDto.getRequestedSeats();

        ShowEntity showEntity = showRepository.findById(showId).get();

        List<ShowSeatEntity>listOfSeats = showEntity.getListOfShowSeats();

        //Iterating over the list ofSeats for that particular show

        for(ShowSeatEntity showSeatEntity:listOfSeats){

            String seatNo=showSeatEntity.getSeatNo();

            if(requestedSeats.contains(seatNo)){

                if(showSeatEntity.isBooked()==true){

                    return false; //Since this seat cant be occupied : returning false.
                }

            }
        }
        //All the seats requested were available
        return true;
    }
}