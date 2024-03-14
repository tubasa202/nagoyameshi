package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);

	public Page<Restaurant> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable);

	public Page<Restaurant> findByAddressLike(String area, Pageable pageable);

	public Page<Restaurant> findByLowestPriceLessThanEqual(Integer price, Pageable pageable);

	// カテゴリ名に基づいてレストランを検索
	// クエリの修正: 'CategoryRestaurant' を 'Restaurant' に変更し、適切な関連を使用
	@Query("SELECT r FROM Restaurant r WHERE r.category.name LIKE %:categoryName%")
	public Page<Restaurant> findByCategoryNameLike(@Param("categoryName") String categoryName, Pageable pageable);

	long count(); // 総会員数を取得するためのメソッド

	public List<Restaurant> findTop5ByOrderByCreatedAtDesc();

	public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
	
	// 平均評価を求める
    @Query("SELECT AVG(r.score) FROM Review r")
    Double findAverageScore();

    // 標準偏差を求める
    @Query("SELECT STDDEV(r.score) FROM Review r")
    Double findStandardDeviation();      
    
    @Query(value = "SELECT r.* FROM restaurants r LEFT JOIN category_restaurant cr ON r.id = cr.restaurant_id LEFT JOIN categories c ON cr.category_id = c.id WHERE r.name LIKE :keyword OR r.address LIKE :keyword OR c.name LIKE :keyword GROUP BY r.id",
    countQuery = "SELECT count(*) FROM (SELECT r.* FROM restaurants r LEFT JOIN category_restaurant cr ON r.id = cr.restaurant_id LEFT JOIN categories c ON cr.category_id = c.id WHERE r.name LIKE :keyword OR r.address LIKE :keyword OR c.name LIKE :keyword GROUP BY r.id) AS countQuery",
    nativeQuery = true)
    List<Restaurant> findByNameLikeOrAddressLikeOrCategoryNameLike(String keyword);
    
//    @Query("SELECT r FROM Restaurant r JOIN r.categoryRestaurants cr WHERE cr.category.id = :categoryId")
//    List<Restaurant> findByCategoryId(@Param("categoryId") Integer categoryId);
    
    List<Restaurant> findByLowestPriceLessThanEqual(Integer price);
    
  
	
	//いったん使用禁止
	
//
//	@Query("SELECT new com.example.nagoyameshi.dto.RestaurantRatingDTO(" +
//			"r.id AS restaurantId, AVG(re.score) AS averageRating, COUNT(re) AS reviewCount, " +
//			"r.image AS image, r.name AS name, r.description AS description, r.postalCode AS postalCode, " +
//			"r.address AS address, r.lowestPrice AS lowestPrice, r.highestPrice AS highestPrice) " +
//			"FROM Restaurant r JOIN r.reviews re GROUP BY r.id " +
//			"ORDER BY AVG(re.score) DESC")
//	Page<RestaurantRatingDTO> findRestaurantsWithRatings(Pageable pageable);
//
//	@Query("SELECT COUNT(DISTINCT r.id) FROM Restaurant r JOIN r.reviews re")
//	long countRestaurantsWithRatings();
//
//	//評価順並べ替えのクエリを参考にしている
//	@Query("SELECT new com.example.nagoyameshi.dto.RestaurantRatingDTO(" +
//			"r.id AS restaurantId, AVG(re.score) AS averageRating, COUNT(re) AS reviewCount, " +
//			"r.image AS image, r.name AS name, r.description AS description, r.postalCode AS postalCode, " +
//			"r.address AS address, r.lowestPrice AS lowestPrice, r.highestPrice AS highestPrice) " +
//			"FROM Restaurant r JOIN r.reviews re GROUP BY r.id " +
//			"ORDER BY AVG(re.score) DESC")
//	List<RestaurantRatingDTO> findTopRatedRestaurants(Pageable pageable);

	//    public List<Restaurant> findTop10ByOrderByCreatedAtDesc();
	//    public List<Restaurant> findTop10ByOrderByCreatedAtDesc();
}
