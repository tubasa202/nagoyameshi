package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	private final NagoyameshiuserRepository nagoyameshiuserRepository;
	private final UserService userService;
	private final FavoriteService favoriteService;//追加

	public UserController(NagoyameshiuserRepository nagoyameshiuserRepository, UserService userService,
			FavoriteService favoriteService) {
		this.nagoyameshiuserRepository = nagoyameshiuserRepository;
		this.userService = userService;
		this.favoriteService = favoriteService; //追加
	}

	@GetMapping("/{userId}/favorites")
	public String showFavorites(@PathVariable Integer userId, Model model) {
		// userIdを使用してお気に入りを検索
		List<Favorite> favorites = favoriteService.findFavoritesByUserId(userId);
		// モデルにお気に入りを追加
		model.addAttribute("favorites", favorites);
		// お気に入り一覧ビューを表示
		return "favorites/index"; // お気に入り一覧を表示するビューの名前
	}

	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		Nagoyameshiuser nagoyameshiuser = nagoyameshiuserRepository.getReferenceById(userDetailsImpl.getUser().getId());

		model.addAttribute("nagoyameshiuser", nagoyameshiuser);

		return "user/index";
	}

	@GetMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		Nagoyameshiuser nagoyameshiuser = nagoyameshiuserRepository.getReferenceById(userDetailsImpl.getUser().getId());
		UserEditForm userEditForm = new UserEditForm(nagoyameshiuser.getId(), nagoyameshiuser.getName(),
				nagoyameshiuser.getKana(), nagoyameshiuser.getPostalCode(), nagoyameshiuser.getAddress(),
				nagoyameshiuser.getPhoneNumber(), nagoyameshiuser.getEmail(), nagoyameshiuser.getBirthday(),
				nagoyameshiuser.getOccupation());

		model.addAttribute("userEditForm", userEditForm);

		return "user/edit";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		// メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "user/edit";
		}

		userService.update(userEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");

		return "redirect:/user";
	}
}
