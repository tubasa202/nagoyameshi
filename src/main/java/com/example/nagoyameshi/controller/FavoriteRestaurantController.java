package com.example.nagoyameshi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.repository.NagoyameshiuserRepository;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/restaurants")
public class FavoriteRestaurantController {

	private static final String ROLE_FREE_MEMBER = "ROLE_FREE_MEMBER";
	private static final String SUBSCRIPTION_MESSAGE = "この機能を利用するには有料プランへの登録が必要です。";
	private static final String SUBSCRIPTION_URL = "/subscription/register";

	private final FavoriteService favoriteService;
	private final UserService userService;
	private final NagoyameshiuserRepository nagoyameshiuserRepository;

	public FavoriteRestaurantController(FavoriteService favoriteService, UserService userService,
			NagoyameshiuserRepository nagoyameshiuserRepository) {
		this.favoriteService = favoriteService;
		this.userService = userService;
		this.nagoyameshiuserRepository = nagoyameshiuserRepository;
	}

	@PostMapping("/add-favorite")
	public String addFavorite(@RequestParam("restaurantId") Integer restaurantId,
			RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isFreeMember = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_FREE_MEMBER));

		if (isFreeMember) {
			// フリーメンバーの場合、有料会員登録ページへリダイレクト
			redirectAttributes.addFlashAttribute("subscriptionMessage", SUBSCRIPTION_MESSAGE);
			return "redirect:" + SUBSCRIPTION_URL;
		}

		Integer userId = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String email = userDetails.getUsername(); // メールアドレスを取得
			userId = userService.findUserIdByEmail(email); // メールアドレスを使ってユーザーIDを取得
		} else {
			// 認証情報がUserDetailsのインスタンスではない場合
			redirectAttributes.addFlashAttribute("errorMessage", "認証情報が不正です。");
			return "redirect:/restaurants";
		}

		if (userId != null && restaurantId != null) {
			try {
				favoriteService.addFavorite(userId, restaurantId);
				redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");
			} catch (Exception e) {
				// 例外が発生した場合（例：データベースエラーなど）
				redirectAttributes.addFlashAttribute("errorMessage", "お気に入りの追加に失敗しました。");
			}
		} else {
			// userIdまたはrestaurantIdがnullの場合
			redirectAttributes.addFlashAttribute("errorMessage", "お気に入りの追加に失敗しました。");
		}

		return "redirect:/restaurants/" + restaurantId;
	}

}
