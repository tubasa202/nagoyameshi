package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/restaurants")

public class RestaurantController {
	private final RestaurantRepository restaurantRepository;
	private final RestaurantService restaurantService;

	public RestaurantController(RestaurantRepository restaurantRepository, RestaurantService restaurantService) {
		this.restaurantRepository = restaurantRepository;
		this.restaurantService = restaurantService;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "category", required = false) String category, // カテゴリパラメータを追加
			@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
			Model model) {
		Page<Restaurant> restaurantPage;

		if (keyword != null && !keyword.isEmpty()) {
			restaurantPage = restaurantRepository.findByNameLikeOrAddressLike("%" + keyword + "%", "%" + keyword + "%",
					pageable);
		} else if (area != null && !area.isEmpty()) {
			restaurantPage = restaurantRepository.findByAddressLike("%" + area + "%", pageable);
		} else if (price != null) {
			restaurantPage = restaurantRepository.findByLowestPriceLessThanEqual(price, pageable);
		} else if (category != null && !category.isEmpty()) { // カテゴリによる検索条件を追加
			restaurantPage = restaurantRepository.findByCategoryNameLike("%" + category + "%", pageable);
		} else {
			restaurantPage = restaurantRepository.findAll(pageable);
		}

		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("category", category); // モデルにカテゴリを追加

		return "restaurants/index"; // Viewのパス
	}

	// 新着順のリストを表示するためのエンドポイント
	@GetMapping("/newest")
	public String listNewest(
			@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
			Model model) {
		Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);
		model.addAttribute("restaurantPage", restaurantPage);
		return "restaurants/index"; // 新着順のレストランを表示するテンプレートへのパス
	}

	@GetMapping("/rating")
	public String listBestRating(@PageableDefault(page = 0, size = 10) Pageable pageable, Model model) {
		List<Restaurant> allRestaurants = restaurantRepository.findAll();
		List<Restaurant> sortedRestaurants = restaurantService.sortByRatingDesc(allRestaurants);

		// Pageableを考慮してリストをページネーションする
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), sortedRestaurants.size());
		List<Restaurant> pagedRestaurants = sortedRestaurants.subList(start, end);

		// PageImplを使用してページ化されたリストを作成
		Page<Restaurant> restaurantPage = new PageImpl<>(pagedRestaurants, pageable, sortedRestaurants.size());
		model.addAttribute("restaurantPage", restaurantPage);

		return "restaurants/index"; // 評価の高い順のレストランを表示するテンプレートへのパス
	}

	@GetMapping("/{id}")
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
