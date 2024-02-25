package com.example.nagoyameshi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reviews")
@Data
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Lob
	@Column(name = "content")
	private String content; // データ型を `text` から `String` に変更

	@Column(name = "score")
	private Integer score; // スコアのデータ型を明示

	@ManyToOne
	@JoinColumn(name = "user_id") // これは外部キーの実際のカラム名です
	private Nagoyameshiuser nagoyameshiuser;

	@ManyToOne
	@JoinColumn(name = "restaurant_id") // これも外部キーの実際のカラム名です
	private Restaurant restaurant;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@PrePersist
	protected void onCreate() {
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		this.createdAt = currentTimestamp;
		this.updatedAt = currentTimestamp;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = new Timestamp(System.currentTimeMillis());
	}

}
