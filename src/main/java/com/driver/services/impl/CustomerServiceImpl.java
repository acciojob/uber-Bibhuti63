package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.driver.model.TripBooking;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

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
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function


		Customer customer=customerRepository2.findById(customerId).get();
		//when a customer is removed we have to Cancel all his Confirmed tripbookings.
		List<TripBooking>list=customer.getTripBookingList();

		for(TripBooking tripBooking : list){
			//we can not cancel the completed booking, so only cancel confirmed booking.
			if(tripBooking.getStatus()==TripStatus.CONFIRMED){
				tripBooking.setStatus(TripStatus.CANCELED);
			}
		}

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
			if(currentDriver.getCab().getAvailable()){
				if(driver==null || currentDriver.getDriverId()<driver.getDriverId()){
					driver=currentDriver;
				}
			}
		}
		if(driver==null){
			throw new Exception("No cab available!");
		}

		TripBooking tripBooking=new TripBooking();
		Customer customer=customerRepository2.findById(customerId).get();

		tripBooking.setCustomer(customer);
		tripBooking.setDriver(driver);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		int rate=driver.getCab().getPerKmRate();
		tripBooking.setBill(rate*distanceInKm);

		driver.getCab().setAvailable(false);

		//adding the tripBooking to customer
		customer.getTripBookingList().add(tripBooking);
		customerRepository2.save(customer);

		//adding the tripBooking to driver
		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);

		//tripBookingRepository will autosaved by cascading

		return tripBooking;



	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking booking=tripBookingRepository2.findById(tripId).get();
		booking.setStatus(TripStatus.CANCELED);
		booking.setBill(0);
		booking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(booking);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBookingRepository2.save(tripBooking);

	}
}
