package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leave_type")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @JsonProperty("leave_type_name")
    @Column(nullable = false, name = "leave_type_name")
    String leaveTypeName;

    @Column
    String description;

    @JsonProperty("is_paid")
    @Column(name = "is_paid")
    Boolean isPaid;

    @JsonProperty("max_days_per_year")
    @Column(name = "max_days_per_year")
    Integer maxDaysPerYear;
}
