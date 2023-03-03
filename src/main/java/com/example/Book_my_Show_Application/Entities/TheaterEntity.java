package com.example.Book_my_Show_Application.Entities;


import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theaters")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TheaterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String location;

    //This is the parent wrt to theatreSeats
    @OneToMany(mappedBy = "theaterEntity",cascade = CascadeType.ALL)
    private List<TheaterSeatEntity>theaterSeatEntityList = new ArrayList<>();

    //parent wrt to showEntity
    @OneToMany(mappedBy = "theaterEntity",cascade = CascadeType.ALL)
    private List<ShowEntity>showEntityList=new ArrayList<>();

}
