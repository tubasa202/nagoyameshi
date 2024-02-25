package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.dto.RestaurantRatingDTO;
import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.service.NagoyameshiuserService;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
public class AdminController {

	private final RestaurantService restaurantService;
	private final NagoyameshiuserService userService;
	 private final CategoryRepository categoryRepository;
	 

	public AdminController(RestaurantService restaurantService, NagoyameshiuserService userService, CategoryRepository categoryRepository) {
		this.restaurantService = restaurantService;
		this.userService = userService;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping("/admin/index")
	public String dashboard(Model model) {
		List<Restaurant> newRestaurants = restaurantService.findNewRestaurants(5); // 例えば最新の5件
		List<Category> allCategories = categoryRepository.findAll();
        List<Category> topCategories = allCategories.stream().limit(5).collect(Collectors.toList());
        List<Category> otherCategories = allCategories.stream().skip(5).collect(Collectors.toList());
        List<RestaurantRatingDTO> topRatedRestaurants = restaurantService.getTopRatedRestaurants();//top5評価順
        

		
		model.addAttribute("topCategories", topCategories);
        model.addAttribute("otherCategories", otherCategories);
		model.addAttribute("newRestaurants", newRestaurants);
		model.addAttribute("totalRestaurants", restaurantService.getTotalNumberOfRestaurants());
		model.addAttribute("totalUsers", userService.getTotalNumberOfUsers());
		model.addAttribute("topRatedRestaurants", topRatedRestaurants);
		
		return "/admin/index";
	}
	
	

}