package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.MailSender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MailSenderRepository extends JpaRepository<MailSender, Integer>, JpaSpecificationExecutor<MailSender> {
    // You can add custom query methods here if needed
}

