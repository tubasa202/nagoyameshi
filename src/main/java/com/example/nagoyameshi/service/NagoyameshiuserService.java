package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;

@Service
public class NagoyameshiuserService {

	private final NagoyameshiuserRepository userRepository;

	public NagoyameshiuserService(NagoyameshiuserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public long getTotalNumberOfUsers() {
		return userRepository.count();
	}

	public Nagoyameshiuser findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
}