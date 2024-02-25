package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.nagoyameshi.dto.RestaurantRatingDTO;
import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.service.CategoryService;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
public class RestaurantController {

	private final RestaurantRepository restaurantRepository;
	//	private final CategoryService categoryService;
	private final CategoryRepository categoryRepository;
	private final RestaurantService restaurantService;

	public RestaurantController(RestaurantRepository restaurantRepository, CategoryService categoryService,
			CategoryRepository categoryRepository, RestaurantService restaurantService) {
		this.restaurantRepository = restaurantRepository;
		//		this.categoryService = categoryService;
		this.categoryRepository = categoryRepository;
		this.restaurantService = restaurantService;
	}

	@GetMapping("/")
	public String publicindex(Model model) {

		List<Restaurant> newRestaurants = restaurantRepository.findTop5ByOrderByCreatedAtDesc();
		//		List<com.example.nagoyameshi.entity.Category> categories = categoryService.findAllCategories();
		List<Category> allCategories = categoryRepository.findAll();
		List<Category> topCategories = allCategories.stream().limit(5).collect(Collectors.toList());
		List<Category> otherCategories = allCategories.stream().skip(5).collect(Collectors.toList());
		List<RestaurantRatingDTO> topRatedRestaurants = restaurantService.getTopRatedRestaurants();//top5評価順

		model.addAttribute("newRestaurants", newRestaurants);
		model.addAttribute("topCategories", topCategories);
		model.addAttribute("otherCategories", otherCategories);
		model.addAttribute("topRatedRestaurants", topRatedRestaurants);
		//		model.addAttribute("categories", categories);
		return "index";
	}

	@GetMapping("/index")
	public String index(Model model) {
		List<RestaurantRatingDTO> topRatedRestaurants = restaurantService.getTopRatedRestaurants();//top5評価順
		List<Restaurant> newRestaurants = restaurantRepository.findTop5ByOrderByCreatedAtDesc();
		List<Category> allCategories = categoryRepository.findAll();
		List<Category> topCategories = allCategories.stream().limit(5).collect(Collectors.toList());
		List<Category> otherCategories = allCategories.stream().skip(5).collect(Collectors.toList());

		model.addAttribute("topCategories", topCategories);
		model.addAttribute("otherCategories", otherCategories);
		model.addAttribute("newRestaurants", newRestaurants);
		model.addAttribute("topRatedRestaurants", topRatedRestaurants);
		return "index";
	}

	@GetMapping("/restaurants/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);

		if (restaurantOptional.isPresent()) {
			Restaurant restaurant = restaurantOptional.get();
			model.addAttribute("restaurant", restaurant);
			return "restaurants/show";
		} else {
			// レストランが見つからない場合の処理
			// 例えば、カスタムエラーメッセージを設定してエラーページにリダイレクトする
			String errorMessage = "Requested restaurant with ID " + id + " not found.";
			model.addAttribute("errorMessage", errorMessage);
			return "error/404"; // 適切なエラーページのビュー名に置き換えてください
		}
	}

}
