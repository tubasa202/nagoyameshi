package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
	private final NagoyameshiuserRepository nagoyameshiuserRepository;

	public AdminUserController(NagoyameshiuserRepository naogyameshiuserRepository) {
		this.nagoyameshiuserRepository = naogyameshiuserRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<Nagoyameshiuser> nagoyameshiuserPage;

		if (keyword != null && !keyword.isEmpty()) {
			nagoyameshiuserPage = nagoyameshiuserRepository.findByNameLikeOrKanaLike("%" + keyword + "%",
					"%" + keyword + "%", pageable);
		} else {
			nagoyameshiuserPage = nagoyameshiuserRepository.findAll(pageable);
		}

		model.addAttribute("nagoyameshiuserPage", nagoyameshiuserPage);
		model.addAttribute("keyword", keyword);

		return "admin/users/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		Nagoyameshiuser nagoyameshiuser = nagoyameshiuserRepository.getReferenceById(id);

		model.addAttribute("nagoyameshiuser", nagoyameshiuser);

		return "admin/users/show";
	}
}
