package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mail_template")
public class MailTemplate {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Integer id;
    @Column(nullable = false, unique = true, name = "mail_template_name")
    @JsonProperty("mail_template_name")
    String mailTemplateName;
    @Column(nullable = false, name = "content_template")
    @JsonProperty("content_template")
    String contentTemplate;
    @Column(nullable = false, name = "subject_template")
    @JsonProperty("subject_template")
    String subjectTemplate;
    @Column(name = "sending_object")
    @JsonProperty("sending_object")
    String sendingObject;
    String description;
}
