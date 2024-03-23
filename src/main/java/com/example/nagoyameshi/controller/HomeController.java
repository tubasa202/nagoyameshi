package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.service.CategoryService;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
public class HomeController {

	private final RestaurantRepository restaurantRepository;

	private final CategoryRepository categoryRepository;
	private final RestaurantService restaurantService;

	public HomeController(RestaurantRepository restaurantRepository, CategoryService categoryService,
			CategoryRepository categoryRepository, RestaurantService restaurantService) {
		this.restaurantRepository = restaurantRepository;

		this.categoryRepository = categoryRepository;
		this.restaurantService = restaurantService;
	}

	@GetMapping("/")
	public String publicindex(Model model) {
		List<Restaurant> restaurants = restaurantRepository.findAll(); // この行を追加
		List<Restaurant> newRestaurants = restaurantRepository.findTop5ByOrderByCreatedAtDesc();
		List<Category> allCategories = categoryRepository.findAll();
		List<Category> topCategories = allCategories.stream().limit(5).collect(Collectors.toList());
		List<Category> otherCategories = allCategories.stream().skip(5).collect(Collectors.toList());
		List<Restaurant> sortedRestaurants = restaurantService.sortByRatingDesc(restaurants);
		List<Restaurant> highlyRatedRestaurants = sortedRestaurants.subList(0, Math.min(sortedRestaurants.size(), 5));

		model.addAttribute("highlyRatedRestaurants", highlyRatedRestaurants);
		model.addAttribute("newRestaurants", newRestaurants);
		model.addAttribute("topCategories", topCategories);
		model.addAttribute("otherCategories", otherCategories);

		return "/index";
	}

//	@GetMapping("/index")
//	public String index(Model model) {
//		List<Restaurant> restaurants = restaurantRepository.findAll(); // この行を追加
//		List<Restaurant> newRestaurants = restaurantRepository.findTop5ByOrderByCreatedAtDesc();
//		List<Category> allCategories = categoryRepository.findAll();
//		List<Category> topCategories = allCategories.stream().limit(5).collect(Collectors.toList());
//		List<Category> otherCategories = allCategories.stream().skip(5).collect(Collectors.toList());
//		List<Restaurant> sortedRestaurants = restaurantService.sortByRatingDesc(restaurants);
//		List<Restaurant> highlyRatedRestaurants = sortedRestaurants.subList(0, Math.min(sortedRestaurants.size(), 5));
//
//		model.addAttribute("topCategories", topCategories);
//		model.addAttribute("otherCategories", otherCategories);
//		model.addAttribute("newRestaurants", newRestaurants);
//		model.addAttribute("highlyRatedRestaurants", highlyRatedRestaurants);
//
//		return "index";
//	}

}
