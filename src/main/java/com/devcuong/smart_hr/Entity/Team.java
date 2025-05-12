package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    Integer id;
    @JsonProperty("team_code")
    @Column(nullable = false, name = "team_code")
    String teamCode;
    @JsonProperty("team_name")
    @Column(nullable = false, name = "team_name")
    String teamName;
    String description;
    @JsonProperty("department_code")
    @Column(nullable = false, name = "department_code")
    String departmentCode;
}
