package com.example.nagoyameshi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor // デフォルトコンストラクタを追加
@AllArgsConstructor
public class RestaurantRatingDTO {
	
	private Integer restaurantId;
	
	private Double averageRating;
	
	private Long reviewCount;
	
	private Double weightedRating;
	
	private String image;
	
	private String name; // レストランの名前を表すプロパティを追加
	
	private String description; // レストランの説明
	
	private String postalCode;
	
	private String address;
	
	private Integer lowestPrice;
	
	private Integer highestPrice;

	// restaurantId を返す getId メソッドを追加
	public Integer getId() {
		return restaurantId;
	}
	 // averageRating を返す getAverageRating メソッド
    public Double getAverageRating() {
        return averageRating;
    }

    // reviewCount を返す getReviewCount メソッド
    public Long getReviewCount() {
        return reviewCount;
    }

    // weightedRating を返す getWeightedRating メソッド
    public Double getWeightedRating() {
        return weightedRating;
    }

    // image を返す getImage メソッド
    public String getImage() {
        return image;
    }

    // name を返す getName メソッド
    public String getName() {
        return name;
    }

    // description を返す getDescription メソッド
    public String getDescription() {
        return description;
    }

    // postalCode を返す getPostalCode メソッド
    public String getPostalCode() {
        return postalCode;
    }

    // address を返す getAddress メソッド
    public String getAddress() {
        return address;
    }

    // lowestPrice を返す getLowestPrice メソッド
    public Integer getLowestPrice() {
        return lowestPrice;
    }

    // highestPrice を返す getHighestPrice メソッド
    public Integer getHighestPrice() {
        return highestPrice;
    }

}
