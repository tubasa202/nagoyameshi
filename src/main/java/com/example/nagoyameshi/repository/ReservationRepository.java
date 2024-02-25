package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    // ユーザーIDに基づいて予約情報を検索するメソッド
    List<Reservation> findByNagoyameshiuserId(Integer userId);
    List<Reservation> findByRestaurantId(Integer restaurantId);
}
