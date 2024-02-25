package com.example.nagoyameshi.event;

import org.springframework.context.ApplicationEvent;

import com.example.nagoyameshi.entity.Reservation;

public class ReservationCompleteEvent extends ApplicationEvent {
	private final Reservation reservation;

	public ReservationCompleteEvent(Object source, Reservation reservation) {
		super(source);
		this.reservation = reservation;
	}

	public Reservation getReservation() {
		return reservation;
	}
}