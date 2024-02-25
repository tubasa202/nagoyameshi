package com.example.nagoyameshi.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reserved_datetime")
    private LocalDateTime reservedDatetime;

    @Column(name = "number_of_people")
    private Integer numberOfPeople;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Nagoyameshiuser nagoyameshiuser;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    
    private Restaurant restaurant;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Getters and Setters
    
    public String getFormattedReservedDateTime() {
        return this.reservedDatetime != null ? this.reservedDatetime.format(DateTimeFormatter.ofPattern("HH:mm")) : null;
    }
}
