package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.nagoyameshi.entity.Company;
import com.example.nagoyameshi.repository.CompanyRepository;

@Controller
@RequestMapping("/companies")
public class CompanyController {
	private final CompanyRepository companyRepository;

	public CompanyController(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}

	@GetMapping
	public String index(Model model) {
		List<Company> companies = companyRepository.findAll(); // すべての会社情報を取得

		model.addAttribute("companies", companies); // companiesとしてリストをHTMLに渡す

		return "companies/index";
	}
}