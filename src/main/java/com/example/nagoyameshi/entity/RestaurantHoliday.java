package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "regular_holiday_restaurant")
@Data
public class RestaurantHoliday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "restaurant_id", nullable = false)
	private Integer restaurantId;

	@Column(name = "holiday_id", nullable = false)
	private Integer holidayId;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	@ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "holiday_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Holidays holiday; // クラス名を修正
}
