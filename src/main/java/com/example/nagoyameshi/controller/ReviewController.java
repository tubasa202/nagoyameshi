package com.example.nagoyameshi.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;

@Controller
@RequestMapping("/restaurants/{restaurantId}/reviews")
public class ReviewController {

	private final ReviewRepository reviewRepository;
	private final RestaurantRepository restaurantRepository;
	private final NagoyameshiuserRepository nagoyameshiuserRepository;

	public ReviewController(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository,
			NagoyameshiuserRepository nagoyameshiuserRepository) {
		this.reviewRepository = reviewRepository;
		this.restaurantRepository = restaurantRepository;
		this.nagoyameshiuserRepository = nagoyameshiuserRepository;
	}

	@GetMapping("/register")
	public String showReviewForm(@PathVariable("restaurantId") Integer restaurantId, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);

		if (restaurant == null) {
			return "redirect:/error";
		}

		model.addAttribute("reviewForm", new ReviewRegisterForm());//レビューに変更
		model.addAttribute("restaurant", restaurant);
		return "reviews/register"; // ファイルの配置に応じてパスを調整

	}

	@GetMapping("/edit/{reviewId}")
	public String showEditForm(@PathVariable("restaurantId") Integer restaurantId,
			@PathVariable("reviewId") Integer reviewId, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + reviewId));
		ReviewRegisterForm form = new ReviewRegisterForm();
		form.setScore(review.getScore());
		form.setContent(review.getContent());
		model.addAttribute("review", review);
		model.addAttribute("reviewForm", form);
		model.addAttribute("restaurantId", restaurantId);
		model.addAttribute("reviewId", reviewId);
		model.addAttribute("restaurant", restaurant);
		return "reviews/edit"; // 正しいビュー名を指定します。"reviews/edit"はビューファイルのパスです。
	}

	@PostMapping("/update/{reviewId}")
	public String updateReview(@PathVariable("restaurantId") Integer restaurantId,
			@PathVariable("reviewId") Integer reviewId, @ModelAttribute("reviewForm") ReviewRegisterForm form,
			BindingResult result, Principal principal, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("restaurantId", restaurantId);
			model.addAttribute("reviewId", reviewId);
			return "reviews/edit"; // エラーがある場合、編集フォームに戻ります。
		}

		String email = principal.getName();

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + reviewId));

		// レビューの所有者を確認
		if (!review.getNagoyameshiuser().getEmail().equals(email)) {
			// 現在のユーザーが所有者でない場合はエラーページにリダイレクト
			return "redirect:/error";
		}
		review.setScore(form.getScore());
		review.setContent(form.getContent());
		reviewRepository.save(review);

		return "redirect:/restaurants/" + restaurantId + "/reviews"; // 更新後、レビュー一覧にリダイレクトします。
	}

	@PostMapping("/create")
	public String createReview(@PathVariable("restaurantId") Integer restaurantId,
			@ModelAttribute("reviewForm") ReviewRegisterForm reviewForm,
			BindingResult result, Principal principal, Model model) {
		if (result.hasErrors()) {
			return showReviewForm(restaurantId, model);
		}

		Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
		if (restaurant == null) {
			return "redirect:/error";
		}

		String email = principal.getName();
		Nagoyameshiuser user = nagoyameshiuserRepository.findByEmail(email);

		Review review = new Review();
		review.setRestaurant(restaurant);
		review.setNagoyameshiuser(user);
		review.setScore(reviewForm.getScore());
		review.setContent(reviewForm.getContent());

		reviewRepository.save(review);
		return "redirect:/restaurants/" + restaurantId + "/reviews";
	}

	@PostMapping("/delete/{reviewId}")
	public String deleteReview(@PathVariable("restaurantId") Integer restaurantId,
			@PathVariable("reviewId") Integer reviewId, Principal principal) {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + reviewId));

		// レビューの所有者が現在のユーザーと一致するか確認
		String email = principal.getName();
		if (!review.getNagoyameshiuser().getEmail().equals(email)) {
			// 権限がない場合はエラーページへリダイレクト
			return "redirect:/error";
		}

		// レビューの削除
		reviewRepository.delete(review);
		return "redirect:/restaurants/" + restaurantId + "/reviews";
	}

	@GetMapping("")
	public String listReviews(@PathVariable("restaurantId") Integer restaurantId, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);

		model.addAttribute("reviewsPage", reviews); // テンプレート側でページネーションを処理するためにPageオブジェクトを渡す
		model.addAttribute("reviews", reviews);
		model.addAttribute("restaurantId", restaurantId);
		model.addAttribute("restaurant", restaurant);
		return "reviews/index"; // レビュー一覧を表示するビューの名前
	}

}
