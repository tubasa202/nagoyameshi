package com.example.nagoyameshi.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationForm {
	
	@NotNull(message = "予約日を選択してください")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @NotNull(message = "時間を選択してください")
    @DateTimeFormat(pattern = "HH:mm")
    private  String reservationTime;

    @NotNull(message = "人数を選択してください")
    @Min(value = 1, message = "人数は1人以上を選択してください")
    private Integer numberOfPeople;

}
