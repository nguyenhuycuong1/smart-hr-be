package com.devcuong.smart_hr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSenderDTO {
    @JsonProperty("mail_account_id")
    private Integer mailAccountId;

    @JsonProperty("mail_template_id")
    private Integer mailTemplateId;

    @JsonProperty("to")
    private String to;

    @JsonProperty("cc")
    private String cc;

    @JsonProperty("bcc")
    private String bcc;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("content")
    private String content;
}

