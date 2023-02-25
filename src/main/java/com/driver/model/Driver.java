package com.driver.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Driver{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int driverId;
    private String mobile;
    private String password;

    //it is child w.r.t. Cab
    @OneToOne
    @JoinColumn
    private Cab cab;

    //it is parent w.r.t TripBooking
    @OneToMany
    private List<TripBooking>tripBookingList=new ArrayList<>();


    public Driver() {
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Cab getCab() {
        return cab;
    }

    public void setCab(Cab cab) {
        this.cab = cab;
    }

    public List<TripBooking> getTripBookingList() {
        return tripBookingList;
    }

    public void setTripBookingList(List<TripBooking> tripBookingList) {
        this.tripBookingList = tripBookingList;
    }
}