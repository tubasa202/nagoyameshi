package com.example.nagoyameshi.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final NagoyameshiuserRepository nagoyameshiuserRepository;
	
	public UserDetailsServiceImpl(NagoyameshiuserRepository nagoyameshiuserRepository) {
		this.nagoyameshiuserRepository = nagoyameshiuserRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			Nagoyameshiuser nagoyameshiuser = nagoyameshiuserRepository.findByEmail(email);
			String nagoyameshiuserRoleName = nagoyameshiuser.getRole().getName();
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(nagoyameshiuserRoleName));
			return new UserDetailsImpl(nagoyameshiuser, authorities);
		} catch (Exception e) {
			throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
		}
	}
}