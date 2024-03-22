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

	long count(); // 総会員数を取得するためのメソッド

	public List<Restaurant> findTop5ByOrderByCreatedAtDesc();

	public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);

	List<Restaurant> findByLowestPriceLessThanEqual(Integer price);
	
	// カテゴリ名に基づいてレストランを検索
		// クエリの修正: 'CategoryRestaurant' を 'Restaurant' に変更し、適切な関連を使用
		@Query("SELECT r FROM Restaurant r WHERE r.category.name LIKE %:categoryName%")
		public Page<Restaurant> findByCategoryNameLike(@Param("categoryName") String categoryName, Pageable pageable);

	// 平均評価を求める
	@Query("SELECT AVG(r.score) FROM Review r")
	Double findAverageScore();

	// 標準偏差を求める
	@Query("SELECT STDDEV(r.score) FROM Review r")
	Double findStandardDeviation();

	//    @Query(value = "SELECT r.* FROM restaurants r LEFT JOIN category_restaurant cr ON r.id = cr.restaurant_id LEFT JOIN categories c ON cr.category_id = c.id WHERE r.name LIKE :keyword OR r.address LIKE :keyword OR c.name LIKE :keyword GROUP BY r.id",
	//    countQuery = "SELECT count(*) FROM (SELECT r.* FROM restaurants r LEFT JOIN category_restaurant cr ON r.id = cr.restaurant_id LEFT JOIN categories c ON cr.category_id = c.id WHERE r.name LIKE :keyword OR r.address LIKE :keyword OR c.name LIKE :keyword GROUP BY r.id) AS countQuery",
	//    nativeQuery = true)
	//    List<Restaurant> findByNameLikeOrAddressLikeOrCategoryNameLike(String keyword);

//	@Query(value = "SELECT r.* FROM restaurants r " +
//			"INNER JOIN categories c ON r.category_id = c.id " +
//			"WHERE r.name LIKE %:keyword% AND r.address LIKE %:keyword% AND c.name LIKE %:keyword%", nativeQuery = true)
//	List<Restaurant> findByKeywordInNameAddressAndCategory(@Param("keyword") String keyword);
	
	 @Query(value = "SELECT r.* FROM restaurants r LEFT JOIN category_restaurant cr ON r.id = cr.restaurant_id LEFT JOIN categories c ON cr.category_id = c.id WHERE r.name LIKE :keyword OR r.address LIKE :keyword OR c.name LIKE :keyword GROUP BY r.id",
			    countQuery = "SELECT count(*) FROM (SELECT r.* FROM restaurants r LEFT JOIN category_restaurant cr ON r.id = cr.restaurant_id LEFT JOIN categories c ON cr.category_id = c.id WHERE r.name LIKE :keyword OR r.address LIKE :keyword OR c.name LIKE :keyword GROUP BY r.id) AS countQuery",
			    nativeQuery = true)
			    List<Restaurant> findByNameLikeOrAddressLikeOrCategoryNameLike(String keyword);

}
