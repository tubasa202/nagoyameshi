package com.example.nagoyameshi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.repository.ReservationRepository;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;


	@Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
	}
	
	
    public Reservation createReservation(Reservation reservation) {
        // 予約作成ロジック
        return reservationRepository.save(reservation);
    }
    
    

    public void cancelReservation(Integer reservationId) {
    	
    	 Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
    	    if (!reservationOpt.isPresent()) {
    	        // 予約が見つからない場合の例外処理
    	        throw new IllegalArgumentException("予約が見つかりません。ID: " + reservationId);
    	    }

    	    Reservation reservation = reservationOpt.get();
    	    LocalDateTime now = LocalDateTime.now();

    	    // 予約日時が現在時刻よりも未来かどうかを確認
    	    if (reservation.getReservedDatetime().isAfter(now)) {
    	        // 未来の予約であればキャンセル処理
    	        reservationRepository.deleteById(reservationId);
    	    } else {
    	        // 過去の予約の場合はキャンセル不可として例外をスロー
    	        throw new IllegalStateException("予約日時が過ぎているため、キャンセルできません。ID: " + reservationId);
    	    }
       
    }
    
    // IDによって予約を検索するメソッド
    public Optional<Reservation> findById(Integer reservationId) {
        return reservationRepository.findById(reservationId);
    }
    
 // レストランIDによって予約を検索するメソッド
    public List<Reservation> findAllByRestaurantId(Integer restaurantId) {
        return reservationRepository.findByRestaurantId(restaurantId);
    }
    public List<Reservation> findAllByUserId(Integer userId) {
        return reservationRepository.findByNagoyameshiuserId(userId);
    }
    
    
}