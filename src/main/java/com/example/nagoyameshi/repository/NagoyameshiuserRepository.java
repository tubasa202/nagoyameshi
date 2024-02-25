package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Nagoyameshiuser;

public interface NagoyameshiuserRepository extends JpaRepository<Nagoyameshiuser, Integer> {
	public Nagoyameshiuser findByEmail(String email);
	public Nagoyameshiuser findByName(String Name);


	public Page<Nagoyameshiuser> findByNameLikeOrKanaLike(String nameKeyword, String kanaKeyword, Pageable pageable);

	long count(); // 総会員数を取得するためのメソッド

	

}
