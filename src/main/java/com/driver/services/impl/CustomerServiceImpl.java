package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.driver.model.TripBooking;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
//		if(customerRepository2.findById(customer.getCustomerId()).isPresent()){
//			//do nothing
//			return;
//		}
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function


		Customer customer=customerRepository2.findById(customerId).get();
		//when a customer is removed we have to set
//		List<TripBooking>list=customer.getTripBookingList();
//
//		for(TripBooking tripBooking : list){
//			Driver driver=tripBooking.getDriver();
//			Cab cab=driver.getCab();
//			cab.setAvailable(true);
//			driverRepository2.save(driver);
//			tripBooking.setStatus(TripStatus.CANCELED);
//		}

		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE).
		// If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver>driverList=driverRepository2.findAll();
		Driver driver=null;
		for(Driver currentDriver: driverList){
			if(currentDriver.getCab().isAvailable()){
				if(driver==null || currentDriver.getDriverId()<driver.getDriverId()){
					driver=currentDriver;
				}
			}
		}
		if(driver==null){
			throw new Exception("No cab available!");
		}

		TripBooking tripBooking=new TripBooking();
		tripBooking.setCustomer(customerRepository2.findById(customerId).get());
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setDriver(driver);
		int rate=driver.getCab().getPerKmRate();
		tripBooking.setBill(rate*distanceInKm);

		driver.getCab().setAvailable(false);
		driverRepository2.save(driver);

		Customer customer=customerRepository2.findById(customerId).get();
		customer.getTripBookingList().add(tripBooking);
		customerRepository2.save(customer);

//		tripBookingRepository2.save(tripBooking);
		return tripBooking;



	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking booking=tripBookingRepository2.findById(tripId).get();
		booking.setBill(0);
		booking.setStatus(TripStatus.CANCELED);
		booking.getDriver().getCab().setAvailable(false);
		tripBookingRepository2.save(booking);


	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking booking=tripBookingRepository2.findById(tripId).get();
		booking.setStatus(TripStatus.COMPLETED);
		booking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(booking);

	}
}
