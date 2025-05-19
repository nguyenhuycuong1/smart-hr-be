package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "holiday")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @JsonProperty("holiday_name")
    @Column(nullable = false, name = "holiday_name")
    String holidayName;

    @JsonProperty("holiday_date")
    @Column(nullable = false, name = "holiday_date")
    LocalDate holidayDate;

    @JsonProperty("is_annual")
    @Column(name = "is_annual")
    Boolean isAnnual;

    @JsonProperty("is_paid")
    @Column(name = "is_paid")
    Boolean isPaid;
}
