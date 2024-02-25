package com.example.nagoyameshi.entity;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "restaurants")
@Data
public class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "image")
	private String image;
	
	@Column(name = "description")
	private String description; 
	
	@Column(name = "lowest_price")
	private Integer lowestPrice;
	
	@Column(name = "highest_price")
	private Integer highestPrice;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "opening_time")
	private Time openingTime;

	@Column(name = "closing_time")
	private Time closingTime;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
    
    @Column(name = "seating_capacity")
	private Integer seatingCapacity;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    
    @ManyToMany
    @JoinTable(
        name = "regular_holiday_restaurant",
        joinColumns = @JoinColumn(name = "restaurant_id"),
        inverseJoinColumns = @JoinColumn(name = "holiday_id")
    )
    private List<Holidays> holidays;
    
    @OneToMany(mappedBy = "restaurant")
    private List<Review> reviews;
    
    public String getFormattedOpeningTime() {
        return formatTime(openingTime);
    }

    public String getFormattedClosingTime() {
        return formatTime(closingTime);
    }

    private String formatTime(Time time) {
        if (time == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(time);
   }
    
    @Override
    public String toString() {
        return "Restaurant{" +
               "id=" + id +
               ", name='" + name + '\'' +
               // Categoryの循環参照を避ける
               ", categoryId=" + (category != null ? category.getId() : null) +
               '}';
    }
}
