package com.example.nagoyameshi.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.Reservation;

@Component
public class ReservationCompleteEventListener {

	 private final JavaMailSender javaMailSender;
	    private final Logger logger = LoggerFactory.getLogger(ReservationCompleteEventListener.class);

	    @Autowired
	    public ReservationCompleteEventListener(JavaMailSender mailSender) {
	        this.javaMailSender = mailSender;
	    }

	    @EventListener
	    public void onReservationComplete(ReservationCompleteEvent event) {
	        try {
	            Reservation reservation = event.getReservation();
	            String to = reservation.getNagoyameshiuser().getEmail();
	            String subject = "予約完了のお知らせ";
	            
	            // メールの本文を構築
	            String text = String.format(
	                "予約が完了しました。\n予約日時: %s\n予約者名: %s\nレストラン名: %s",
	                reservation.getReservedDatetime().toString(),
	                reservation.getNagoyameshiuser().getName(),
	                reservation.getRestaurant().getName() // レストラン名を含める
	            );

	            SimpleMailMessage message = new SimpleMailMessage();
	            message.setTo(to);
	            message.setSubject(subject);
	            message.setText(text);
	            javaMailSender.send(message);
	            logger.info("{}へのメール送信成功", to);
	        } catch (Exception e) {
	            logger.error("予約完了メール送信中にエラー発生", e);
	            throw e;
	        }
	    }
	}