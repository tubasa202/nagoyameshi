package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Nagoyameshiuser;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByNagoyameshiuser(Nagoyameshiuser nagoyameshiuser);
   
}
