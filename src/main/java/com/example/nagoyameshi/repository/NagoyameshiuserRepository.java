package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.nagoyameshi.entity.Nagoyameshiuser;

public interface NagoyameshiuserRepository extends JpaRepository<Nagoyameshiuser, Integer> {
	public Nagoyameshiuser findByEmail(String email);
	public Nagoyameshiuser findByName(String Name);


	public Page<Nagoyameshiuser> findByNameLikeOrKanaLike(String nameKeyword, String kanaKeyword, Pageable pageable);

	long count(); // 総会員数を取得するためのメソッド

	@Query("SELECT COUNT(u) FROM Nagoyameshiuser u WHERE u.role.name IN ('ROLE_FREE_MEMBER', 'ROLE_PAID_MEMBER')")
    long countByRoleNameInRoleFreeMemberOrRolePaidMember();    

    long countByRole_Name(String roleName);
    
    @Query("SELECT u FROM Nagoyameshiuser u WHERE u.role.name IN ('ROLE_FREE_MEMBER', 'ROLE_PAID_MEMBER')")
    List<Nagoyameshiuser> findByRoleNameInRoleFreeMemberOrRolePaidMember(); 

}
