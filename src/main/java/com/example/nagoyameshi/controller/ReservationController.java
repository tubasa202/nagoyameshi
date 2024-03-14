package com.example.nagoyameshi.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.event.ReservationCompleteEventPublish;
import com.example.nagoyameshi.form.ReservationForm;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.service.ReservationService;
import com.example.nagoyameshi.service.RestaurantService;

import jakarta.servlet.http.HttpServletRequest;

@Controller // @RestControllerを@Controllerに変更
@RequestMapping("/restaurants/{restaurantId}/reservations")
public class ReservationController {

	private static final String ROLE_FREE_MEMBER = "ROLE_FREE_MEMBER";
	private static final String SUBSCRIPTION_MESSAGE = "この機能を利用するには有料プランへの登録が必要です。";
	private static final String SUBSCRIPTION_URL = "/subscription/register";

	private final RestaurantRepository restaurantRepository;
	private final ReservationService reservationService;
	private final RestaurantService restaurantService;
	private final NagoyameshiuserRepository nagoyameshiuserRepository;
	private final ReservationCompleteEventPublish eventPublisher;

	public ReservationController(RestaurantRepository restaurantRepository,
			ReservationService reservationService,
			RestaurantService restaurantService,
			NagoyameshiuserRepository nagoyameshiuserRepository, ReservationCompleteEventPublish eventPublisher) {
		this.restaurantRepository = restaurantRepository;
		this.reservationService = reservationService;
		this.restaurantService = restaurantService;
		this.nagoyameshiuserRepository = nagoyameshiuserRepository;
		this.eventPublisher = eventPublisher;
	}

	@PostMapping("/create")
	public String create(@PathVariable("restaurantId") Integer restaurantId,
			@ModelAttribute("reservationForm") ReservationForm reservationForm,
			RedirectAttributes redirectAttributes, HttpServletRequest request) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// フリーメンバーの場合、有料プラン登録ページへリダイレクト
		boolean isFreeMember = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_FREE_MEMBER));
		if (isFreeMember) {
			redirectAttributes.addFlashAttribute("subscriptionMessage", SUBSCRIPTION_MESSAGE);
			return "redirect:" + SUBSCRIPTION_URL;
		}

		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String email = userDetails.getUsername(); // メールアドレスを取得
			Nagoyameshiuser user = nagoyameshiuserRepository.findByEmail(email); // メールアドレスを使ってユーザーエンティティを取得

			if (user == null) {
				// ユーザーが見つからない場合
				redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが見つかりません。");
				return "redirect:/restaurants";
			}

			try {
				// フォームデータからReservationエンティティを生成
				Reservation reservation = new Reservation();

				// 予約日と予約時間を組み合わせてLocalDateTimeを生成
				LocalDate reservationDate = reservationForm.getReservationDate();
				LocalTime reservationTime = LocalTime.parse(reservationForm.getReservationTime(),
						DateTimeFormatter.ofPattern("HH:mm"));
				LocalDateTime reservedDateTime = LocalDateTime.of(reservationDate, reservationTime);

				// ステップ 2: 現在時刻と予約時刻との差を計算
				long hoursBetween = ChronoUnit.HOURS.between(LocalDateTime.now(), reservedDateTime);

				// ステップ 3: 条件を確認
				if (hoursBetween < 2) {
					// 予約時刻が現在時刻から2時間未満の場合
					redirectAttributes.addFlashAttribute("errorMessage", "予約は少なくとも2時間前に行ってください。");
					return "redirect:/restaurants/{restaurantId}/reservations/register";
				}

				reservation.setReservedDatetime(reservedDateTime); // ここでLocalDateTimeを設定
				reservation.setNumberOfPeople(reservationForm.getNumberOfPeople());

				// 予約対象のレストランを設定
				Restaurant restaurant = restaurantService.findById(restaurantId)
						.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id: " + restaurantId));
				reservation.setRestaurant(restaurant);

				// 予約エンティティにユーザーを設定
				reservation.setNagoyameshiuser(user); // ここでユーザーエンティティを設定

				// 保存処理
				reservationService.createReservation(reservation);

				// 予約完了イベントの発行
				eventPublisher.completeReservation(reservation);

				// 予約完了後に予約リストページへリダイレクト
				return "redirect:/reservations/index";
			} catch (Exception e) {
				e.printStackTrace(); // サーバのログに例外のスタックトレースを出力
				redirectAttributes.addFlashAttribute("errorMessage", "予約に失敗しました。エラー: " + e.getMessage());
				return "redirect:/restaurants/" + restaurantId + "/reservations/register";
			}
		} else {
			// 認証情報がUserDetailsインスタンスではない場合
			redirectAttributes.addFlashAttribute("errorMessage", "認証情報が不正です。");
			return "redirect:/restaurants";
		}
	}

	// 予約ページを表示するためのメソッドを追加
	@GetMapping("/register")
	public String register(@PathVariable("restaurantId") Integer restaurantId, Model model) {
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + restaurantId));
		List<String> availableTimes = restaurantService.generateAvailableTimes(restaurantId);
		List<Integer> availablePeopleOptions = restaurantService.generateAvailablePeopleOptions(restaurantId);

		model.addAttribute("availablePeopleOptions", availablePeopleOptions);
		model.addAttribute("availableTimes", availableTimes);
		model.addAttribute("restaurant", restaurant);
		model.addAttribute("reservationForm", new ReservationForm());
		return "reservations/register"; // resources/templates/reservations/index.htmlへのパス
	}

}
