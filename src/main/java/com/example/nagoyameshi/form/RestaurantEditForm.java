package com.example.nagoyameshi.form;

import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestaurantEditForm {
	@NotNull
	private Integer id;
	
	@NotBlank(message = "店舗名を入力してください。")
	private String name;
	
	private MultipartFile imageFile;
	
	@NotBlank(message = "説明を入力してください。")
	private String description;
	
	@Min(value = 1, message = "最低金額は1円以上に設定してください。")
    private Integer lowestPrice;

    @Max(value = 100000, message = "最高金額は100,000円以下に設定してください。")
    private Integer highestPrice;
	
	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;
	
	@NotBlank(message = "住所を入力してください。")
	private String address;
	
	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;
	
	@NotNull(message = "営業開始時間を入力してください。")
	@DateTimeFormat(pattern = "HH:mm:ss")
	private LocalTime openingTime;

	@NotNull(message = "営業終了時間を入力してください。")
	@DateTimeFormat(pattern = "HH:mm:ss")
	private LocalTime closingTime;
	
	@Min(value = 1,message = "総座席数を1席以上で入力してください。")
	private Integer seatingCapacity;
	
	@NotNull(message = "カテゴリーを選択してください。")
    private Integer categoryId;
	
	 // 休日情報のIDのリスト
	@NotNull(message = "営業日を選択してください。")
    private List<Integer> holidayIds;

}
