package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailTemplateDTO {
    @JsonProperty("mail_template_name")
    private String mailTemplateName;

    @JsonProperty("content_template")
    private String contentTemplate;

    @JsonProperty("subject_template")
    private String subjectTemplate;

    @JsonProperty("sending_object")
    private String sendingObject;

    private String description;
}

