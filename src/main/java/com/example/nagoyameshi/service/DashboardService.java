package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.repository.NagoyameshiuserRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class DashboardService {
	private final NagoyameshiuserRepository nagoyameshiuserRepository;
	private final RestaurantRepository restaurantRepository;
	
	public DashboardService(NagoyameshiuserRepository nagoyameshiuserRepository, RestaurantRepository restaurantRepository) {
	    this.nagoyameshiuserRepository = nagoyameshiuserRepository;
	    this.restaurantRepository = restaurantRepository;
	  }

	  public long getTotalNumberOfUsers() {
	    return nagoyameshiuserRepository.count();
	  }

	  public long getTotalNumberOfRestaurants() {
	    return restaurantRepository.count();
	  }
	  
//	  ここに、他の集計するもの書いていく

}
