package com.example.nagoyameshi.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Holidays;

public interface HolidayRepository extends JpaRepository<Holidays, Integer> {
    // ここに必要なクエリメソッドを定義します
}