package com.example.nagoyameshi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final NagoyameshiuserRepository nagoyameshiuserRepository;
	private final RestaurantRepository restaurantRepository;


	public FavoriteService(FavoriteRepository favoriteRepository, NagoyameshiuserRepository nagoyameshiuserRepository,
			RestaurantRepository restaurantRepository) {
		this.favoriteRepository = favoriteRepository;
		this.nagoyameshiuserRepository = nagoyameshiuserRepository;
		this.restaurantRepository = restaurantRepository;
	}

	// サービスの他のメソッドやロジックを追加

	@Transactional
	public Favorite addFavorite(Integer userId, Integer restaurantId) {
		Nagoyameshiuser nagoyameshiuser = nagoyameshiuserRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new RuntimeException("Restaurant not found"));

		Favorite favorite = new Favorite();
		favorite.setNagoyameshiuser(nagoyameshiuser);
		favorite.setRestaurant(restaurant);
		return favoriteRepository.save(favorite);
	}

	public void removeFavorite(Integer id) {
		favoriteRepository.deleteById(id);
	}

	//追加
	public List<Favorite> findFavoritesByUserId(Integer userId) {
		Nagoyameshiuser user = nagoyameshiuserRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		return favoriteRepository.findByNagoyameshiuser(user);
	}
}