package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Holidays;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.HolidayRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {
	private final RestaurantRepository restaurantRepository;
	private final HolidayRepository holidayRepository;

	public RestaurantService(RestaurantRepository restaurantRepository, HolidayRepository holidayRepository) {
		this.restaurantRepository = restaurantRepository;
		this.holidayRepository = holidayRepository;
	}

	public Optional<Restaurant> findById(Integer id) {
		return restaurantRepository.findById(id);
	}

	public List<Integer> generateAvailablePeopleOptions(Integer restaurantId) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id: " + restaurantId));
		int capacity = restaurant.getSeatingCapacity();

		List<Integer> availablePeopleOptions = new ArrayList<>();
		for (int i = 1; i <= capacity; i++) {
			availablePeopleOptions.add(i);
		}
		return availablePeopleOptions;
	}

	// レストランの営業時間に基づいて選択可能な時間のリストを生成
	public List<String> generateAvailableTimes(Integer restaurantId) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id: " + restaurantId));
		List<String> availableTimes = new ArrayList<>();

		// java.sql.Timeをjava.time.LocalTimeに変換
		LocalTime openingTime = restaurant.getOpeningTime().toLocalTime();
		LocalTime closingTime = restaurant.getClosingTime().toLocalTime();

		// 例: 30分間隔で時間を追加
		for (LocalTime time = openingTime; time.isBefore(closingTime); time = time.plusMinutes(30)) {
			availableTimes.add(time.toString());
		}

		return availableTimes;
	}

	// 価格（最低価格と最高価格の平均）が安い順に並べ替える
	public List<Restaurant> sortByPriceAsc(List<Restaurant> restaurants) {
		restaurants.sort(Comparator.comparingDouble(r -> (r.getLowestPrice() + r.getHighestPrice()) / 2.0));

		return restaurants;
	}

	// 評価が高い順に並べ替える（平均評価や標準偏差を使った独自のアルゴリズム）
	public List<Restaurant> sortByRatingDesc(List<Restaurant> restaurants) {
		double averageScore = restaurantRepository.findAverageScore();
		double standardDeviation = restaurantRepository.findStandardDeviation();

		restaurants.sort((r1, r2) -> {
			double rating1 = ((r1.getAverageScore() - averageScore) * 10 / standardDeviation) * r1.getReviews().size();
			double rating2 = ((r2.getAverageScore() - averageScore) * 10 / standardDeviation) * r2.getReviews().size();
			return Double.compare(rating2, rating1);
		});

		return restaurants;

	}

	// RestaurantService.java
	public Page<Restaurant> searchRestaurants(String keyword, String area, String category, Pageable pageable) {
		if (keyword != null && !keyword.isEmpty()) {
			return restaurantRepository.findByNameLike("%" + keyword + "%", pageable);
		} else if (area != null && !area.isEmpty()) {
			return restaurantRepository.findByAddressLike("%" + area + "%", pageable);
		} else if (category != null && !category.isEmpty()) {
			// カテゴリ名での検索を正確に行う
			return restaurantRepository.findByCategoryNameLike("%" + category + "%", pageable);
		} else {
			return restaurantRepository.findAll(pageable);
		}
	}

	// 最近追加されたレストランを取得するメソッド
	public List<Restaurant> findNewRestaurants(int limit) {
		return restaurantRepository.findTop5ByOrderByCreatedAtDesc();
	}

	// レストランの総数を取得するメソッド
	public long getTotalNumberOfRestaurants() {
		return restaurantRepository.count();
	}

	// 休日情報を保存するメソッド
	@Transactional
	public void addHolidaysToRestaurant(Integer restaurantId, List<Integer> holidayIds) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + restaurantId));
		List<Holidays> holidays = holidayRepository.findAllById(holidayIds);
		restaurant.setHolidays(holidays); // これでエラーは発生しないはずです
		restaurantRepository.save(restaurant);
	}

	// 休日情報を更新するメソッド
	@Transactional
	public void updateHolidaysOfRestaurant(Integer restaurantId, List<Integer> newHolidayIds) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + restaurantId));
		// 現在の休日リストをクリアする
		restaurant.getHolidays().clear(); // エラーは発生しないはずです
		// 新しい休日リストを追加する
		List<Holidays> newHolidays = holidayRepository.findAllById(newHolidayIds);
		restaurant.getHolidays().addAll(newHolidays); // これでエラーは発生しないはずです
		restaurantRepository.save(restaurant);
	}

	@Transactional
	public void create(RestaurantRegisterForm restaurantRegisterForm, List<Integer> newHolidayIds) {
		Restaurant restaurant = new Restaurant();
		MultipartFile imageFile = restaurantRegisterForm.getImageFile();

		// カテゴリの設定
		Category category = new Category();
		category.setId(restaurantRegisterForm.getCategoryId());
		restaurant.setCategory(category);

		// LocalTimeからjava.sql.Timeへの変換
		if (restaurantRegisterForm.getOpeningTime() != null) {
			LocalTime openingTime = restaurantRegisterForm.getOpeningTime();
			restaurant.setOpeningTime(Time.valueOf(openingTime));
		}

		if (restaurantRegisterForm.getClosingTime() != null) {
			LocalTime closingTime = restaurantRegisterForm.getClosingTime();
			restaurant.setClosingTime(Time.valueOf(closingTime));
		}

		if (!imageFile.isEmpty()) {
//			String image = imageFile.getOriginalFilename();
//			String hashedImage = generateNewFileName(image);
//			java.nio.file.Path filePath = Paths.get("src/main/resources/static/storage/restaurant/" + hashedImage);
//			copyImageFile(imageFile, filePath);
//			restaurant.setImage(hashedImage);
			 String originalFileName = imageFile.getOriginalFilename(); // オリジナルのファイル名を取得
			    Path filePath = Paths.get("src/main/resources/static/storage/restaurant/" + originalFileName);
			    copyImageFile(imageFile, filePath);
			    restaurant.setImage(originalFileName); // DBにはオリジナルのファイル名を保存
		}

		restaurant.setName(restaurantRegisterForm.getName());
		restaurant.setDescription(restaurantRegisterForm.getDescription());
		restaurant.setLowestPrice(restaurantRegisterForm.getLowestPrice());
		restaurant.setHighestPrice(restaurantRegisterForm.getHighestPrice());
		restaurant.setPostalCode(restaurantRegisterForm.getPostalCode());
		restaurant.setAddress(restaurantRegisterForm.getAddress());
		restaurant.setPhoneNumber(restaurantRegisterForm.getPhoneNumber());
		restaurant.setSeatingCapacity(restaurantRegisterForm.getSeatingCapacity());

		List<Holidays> holidays = holidayRepository.findAllById(newHolidayIds);
		restaurant.setHolidays(holidays);

		restaurantRepository.save(restaurant);
	}

	@Transactional
	public void update(RestaurantEditForm restaurantEditForm, List<Integer> newHolidayIds) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantEditForm.getId());
		MultipartFile imageFile = restaurantEditForm.getImageFile();

		// カテゴリの設定
		if (restaurantEditForm.getCategoryId() != null) {
			Category category = new Category();
			category.setId(restaurantEditForm.getCategoryId());
			restaurant.setCategory(category);
		}

		if (!imageFile.isEmpty()) {
//			String image = imageFile.getOriginalFilename();
//			String hashedImage = generateNewFileName(image);
//			Path filePath = Paths.get("src/main/resources/static/storage/restaurant/" + hashedImage);
//			copyImageFile(imageFile, filePath);
//			restaurant.setImage(hashedImage);
			
			 String originalFileName = imageFile.getOriginalFilename(); // オリジナルのファイル名を取得
			    Path filePath = Paths.get("src/main/resources/static/storage/restaurant/" + originalFileName);
			    copyImageFile(imageFile, filePath);
			    restaurant.setImage(originalFileName); // DBにはオリジナルのファイル名を保存
		}
		restaurant.setName(restaurantEditForm.getName());
		restaurant.setDescription(restaurantEditForm.getDescription());
		restaurant.setLowestPrice(restaurantEditForm.getLowestPrice());
		restaurant.setHighestPrice(restaurantEditForm.getHighestPrice());
		restaurant.setPostalCode(restaurantEditForm.getPostalCode());
		restaurant.setAddress(restaurantEditForm.getAddress());
		restaurant.setPhoneNumber(restaurantEditForm.getPhoneNumber());
		restaurant.setSeatingCapacity(restaurantEditForm.getSeatingCapacity());

		List<Holidays> holidays = holidayRepository.findAllById(newHolidayIds);
		restaurant.setHolidays(holidays);

		restaurantRepository.save(restaurant);
	}

	// UUIDを使って生成したファイル名を返す
	public String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();
		}
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;
	}

	// 画像ファイルを指定したファイルにコピーする
	public void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			Files.copy(imageFile.getInputStream(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}