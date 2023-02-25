package com.example.Book_my_Show_Application.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String movieName;

    private LocalDate showDate;

    private LocalTime showTime;

    private int totalAmount;

    private String ticketId= UUID.randomUUID().toString();

    private String theaterName;

    //child wrt to UserEntity
    @ManyToOne
    @JoinColumn
    private UserEntity userEntity;


    //child wrt to showEntity
    @ManyToOne
    @JoinColumn
    private ShowEntity showEntity;
}