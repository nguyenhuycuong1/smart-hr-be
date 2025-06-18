package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplate, Integer>, JpaSpecificationExecutor<MailTemplate> {
    boolean existsByMailTemplateName(String mailTemplateName);
    MailTemplate findByMailTemplateName(String mailTemplateName);
}

