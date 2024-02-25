package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {
	 private final FavoriteService favoriteService;

	    public FavoriteController(FavoriteService favoriteService) {
	        this.favoriteService = favoriteService;
	    }

	@PostMapping("/remove-favorite/{id}")
	public String removeFavorite(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			favoriteService.removeFavorite(id);
			redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
		} catch (Exception e) {
			// 例外処理（例：データが見つからない、データベースエラーなど）
			redirectAttributes.addFlashAttribute("errorMessage", "お気に入りの解除に失敗しました。");
		}
		return "redirect:/favorites/index"; // 解除後のリダイレクト先（お気に入り一覧など）
	}
	@GetMapping("/index")
	public String favoritesIndex(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
		if (userDetails != null) {
			Integer userId = userDetails.getUser().getId(); // ログインユーザーのIDを取得
			List<Favorite> favorites = favoriteService.findFavoritesByUserId(userId); // お気に入りデータを取得
			model.addAttribute("favorites", favorites); // モデルにお気に入りデータをセット
		}
		return "favorites/index"; // テンプレート名を返す
	}
}