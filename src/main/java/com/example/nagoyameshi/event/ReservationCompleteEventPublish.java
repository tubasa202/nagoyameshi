package com.example.nagoyameshi.event;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Reservation;

@Service
public class ReservationCompleteEventPublish {
	
	 private final ApplicationEventPublisher eventPublisher;


	    public ReservationCompleteEventPublish(ApplicationEventPublisher eventPublisher) {
	        this.eventPublisher = eventPublisher;
	    }

	    public void completeReservation(Reservation reservation) {
	        // 予約完了処理（データベースへの保存等）

	        // 予約完了イベントの発行
	        eventPublisher.publishEvent(new ReservationCompleteEvent(this, reservation));
	    }

}
