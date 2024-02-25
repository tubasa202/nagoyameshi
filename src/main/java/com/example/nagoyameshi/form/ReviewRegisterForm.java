package com.example.nagoyameshi.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewRegisterForm {

    private Integer id; // レビューのIDを追加

    @NotNull(message = "星の数を入力してください。")
    @Min(value = 1, message = "星の数は1以上である必要があります。")
    @Max(value = 5, message = "星の数は5以下である必要があります。")
    private Integer score;

    @NotBlank(message = "コメントを空にすることはできません。")
    @Size(max = 500, message = "コメントは500文字を超えることはできません。")
    private String content;
}