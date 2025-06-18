package com.devcuong.smart_hr.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table(name = "mail_sender")
public class MailSender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "mail_account_id", nullable = false)
    Integer mailAccountId;
    @Column(name = "mail_template_id")
    Integer mailTemplateId;
    @Column(name = "to", nullable = false)
    String to;
    @Column(name = "cc")
    String cc;
    @Column(name = "bcc")
    String bcc;
    @Column(name = "subject", nullable = false)
    String subject;
    @Column(name = "content", nullable = false)
    String content;
}
