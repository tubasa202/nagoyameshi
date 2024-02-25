package com.example.nagoyameshi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/restaurants")
public class FavoriteRestaurantController {

	private final FavoriteService favoriteService;
    private final UserService userService;

    public FavoriteRestaurantController(FavoriteService favoriteService, UserService userService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    @PostMapping("/add-favorite")
    public String addFavorite(@RequestParam("restaurantId") Integer restaurantId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername(); // メールアドレスを取得
            userId = userService.findUserIdByEmail(email); // メールアドレスを使ってユーザーIDを取得
        } else {
            // 認証情報がUserDetailsのインスタンスではない場合 ここで会員登録の画面にリダイレクトすればいいんじゃない
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

//    @PostMapping("/remove-favorite/{id}")
//    public String removeFavorite(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
//        try {
//            favoriteService.removeFavorite(id);
//            redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
//        } catch (Exception e) {
//            // 例外処理（例：データが見つからない、データベースエラーなど）
//            redirectAttributes.addFlashAttribute("errorMessage", "お気に入りの解除に失敗しました。");
//        }
//        return "redirect:/restaurants"; // 解除後のリダイレクト先（お気に入り一覧など）
//    }
//    
//    
   
}
