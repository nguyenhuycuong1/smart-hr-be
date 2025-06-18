package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.MailAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MailAccountRepository extends JpaRepository<MailAccount, Integer>, JpaSpecificationExecutor<MailAccount> {
    MailAccount findByUsername(String username);

    boolean existsByUsername(String username);
}

