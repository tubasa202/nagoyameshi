package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Holidays;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.HolidayRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {
	private final RestaurantRepository restaurantRepository;
	private final RestaurantService restaurantService;
	private final CategoryRepository categoryRepository;
	private final HolidayRepository holidayRepository;

	public AdminRestaurantController(RestaurantRepository restaurantRepository, RestaurantService restaurantService,
			CategoryRepository categoryRepository, HolidayRepository holidayRepository) {
		this.restaurantRepository = restaurantRepository;
		this.restaurantService = restaurantService;
		this.categoryRepository = categoryRepository;
		this.holidayRepository = holidayRepository;
	}
	
	@GetMapping("/restaurants")
	public String searchRestaurants(Model model,
	                                @RequestParam(required = false) String keyword,
	                                @RequestParam(required = false) String area,
	                                @RequestParam(required = false) String category) {
	    Page<Restaurant> restaurants = restaurantService.searchRestaurants(keyword, area, category, PageRequest.of(0, 10));
	    model.addAttribute("restaurants", restaurants.getContent());
	    return "restaurants/index";
	}
	
	@GetMapping("/index")
	 public String index(Model model) {
       List<Restaurant> newRestaurants = restaurantRepository.findTop5ByOrderByCreatedAtDesc();
       model.addAttribute("newRestaurants", newRestaurants);  
		return "/admin/index";
	}

	@GetMapping
	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "category", required = false) String category) {

		Page<Restaurant> restaurantPage;

		if (category != null && !category.isEmpty()) {
			restaurantPage = restaurantRepository.findByCategoryNameLike("%" + category + "%", pageable);
		} else if (keyword != null && !keyword.isEmpty()) {
			restaurantPage = restaurantRepository.findByNameLikeOrAddressLike("%" + keyword + "%", "%" + keyword + "%",
					pageable);
		} else {
			restaurantPage = restaurantRepository.findAll(pageable);
		}

		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", category);

		return "admin/restaurants/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);

		model.addAttribute("restaurant", restaurant);

		return "admin/restaurants/show";
	}

	@GetMapping("/register")
	public String register(Model model) {
		List<Category> categories = categoryRepository.findAll();
	    model.addAttribute("categories", categories);
		model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());

		// 休日情報の取得
		List<Holidays> holidaysList = holidayRepository.findAll();
		model.addAttribute("holidaysList", holidaysList);

		return "admin/restaurants/register";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute @Validated RestaurantRegisterForm restaurantRegisterForm,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/restaurants/register";
		}

		restaurantService.create(restaurantRegisterForm, restaurantRegisterForm.getHolidayIds());
		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を登録しました。");

		return "redirect:/admin/restaurants";
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		String image = restaurant.getImage();

		Integer categoryId = restaurant.getCategory() != null ? restaurant.getCategory().getId() : null;

		// `Restaurant`エンティティから休日情報のIDリストを取得
		List<Integer> holidayIds = restaurant.getHolidays().stream()
				.map(Holidays::getId)
				.collect(Collectors.toList());

		// categoryIdを取得してRestaurantEditFormに渡す
		RestaurantEditForm restaurantEditForm = new RestaurantEditForm(
				restaurant.getId(),
				restaurant.getName(),
				null, // imageFileはフォームから再アップロードする必要がある場合に使用
				restaurant.getDescription(),
				restaurant.getLowestPrice(),
				restaurant.getHighestPrice(),
				restaurant.getPostalCode(),
				restaurant.getAddress(),
				restaurant.getPhoneNumber(),
				restaurant.getOpeningTime().toLocalTime(),
				restaurant.getClosingTime().toLocalTime(),
				categoryId, // ここでcategoryIdを渡す
				restaurant.getSeatingCapacity(),
				holidayIds);

		List<com.example.nagoyameshi.entity.Category> categories = categoryRepository.findAll();
		model.addAttribute("categories", categories);

		// 休日情報の取得とモデルへの追加
		List<Holidays> holidaysList = holidayRepository.findAll();
		List<Integer> assignedHolidays = restaurant.getHolidays().stream()
				.map(Holidays::getId)
				.collect(Collectors.toList());
		model.addAttribute("holidaysList", holidaysList);
		model.addAttribute("assignedHolidays", assignedHolidays);

		model.addAttribute("image", image);
		model.addAttribute("restaurantEditForm", restaurantEditForm);

		return "admin/restaurants/edit";
	}

	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated RestaurantEditForm restaurantEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/restaurants/edit";
		}

		restaurantService.update(restaurantEditForm, restaurantEditForm.getHolidayIds());
		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を編集しました。");

		return "redirect:/admin/restaurants";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		restaurantRepository.deleteById(id);

		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を削除しました");

		return "redirect:/admin/restaurants";
	}
}
