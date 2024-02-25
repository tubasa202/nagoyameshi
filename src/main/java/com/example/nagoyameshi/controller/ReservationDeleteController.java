package com.example.nagoyameshi.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Nagoyameshiuser;
import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.repository.NagoyameshiuserRepository;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.service.ReservationService;

@Controller
@RequestMapping("/reservations")
public class ReservationDeleteController {
	
	private final ReservationRepository reservationRepository;
	private final ReservationService reservationService;
	private final NagoyameshiuserRepository nagoyameshiuserRepository;

	@Autowired
	public ReservationDeleteController(ReservationRepository reservationRepository, ReservationService reservationService, NagoyameshiuserRepository nagoyameshiuserRepository) {
		this.reservationRepository = reservationRepository;
		this.reservationService = reservationService;
		this.nagoyameshiuserRepository = nagoyameshiuserRepository;
	}

	@GetMapping("/index")
	public String index(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (!(authentication.getPrincipal() instanceof UserDetails)) {
	        // 認証情報がUserDetailsインスタンスではない場合
	        return "redirect:/login"; // ログインページにリダイレクト
	    }

	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    Nagoyameshiuser user = nagoyameshiuserRepository.findByEmail(userDetails.getUsername());
	    
	    if (user == null) {
	        // ユーザーが見つからない場合
	        return "redirect:/login"; // ログインページにリダイレクト
	    }
	    
	    List<Reservation> userReservations = reservationService.findAllByUserId(user.getId());
	    model.addAttribute("reservations", userReservations);
	    

	    // 現在の日時をモデルに追加
	    model.addAttribute("now", LocalDateTime.now());
	    
	    return "reservations/index";
	}

	@PostMapping("{reservationId}/delete")
	public String delete(@PathVariable("reservationId") Integer reservationId,
			RedirectAttributes redirectAttributes) {	
		Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
						
		if(reservation == null) {
			return "redirect:/error";
		}
		
		 try {
		        // キャンセル処理をReservationServiceを通じて行う
		        reservationService.cancelReservation(reservationId);
		        redirectAttributes.addFlashAttribute("successMessage", "予約をキャンセルしました。");
		    } catch (IllegalArgumentException | IllegalStateException e) {
		        // 予約が見つからない場合やキャンセル不可の場合のエラーハンドリング
		        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		        return "redirect:/error";
		    }
		
		return "redirect:/reservations/index";
	}
}
