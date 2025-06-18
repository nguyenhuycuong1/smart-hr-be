package com.devcuong.smart_hr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailAccountDTO {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;
}

