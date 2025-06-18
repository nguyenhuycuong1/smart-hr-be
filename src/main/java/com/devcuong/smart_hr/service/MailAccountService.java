package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.MailAccount;
import com.devcuong.smart_hr.dto.MailAccountDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.MailAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MailAccountService extends SearchService<MailAccount> {
    @Autowired
    private MailAccountRepository repository;

    public MailAccountService(MailAccountRepository repository) {
        super(repository);
    }

    public List<MailAccount> getAllMailAccounts() {
        return repository.findAll();
    }

    public Page<MailAccount> searchMailAccounts(PageFilterInput<MailAccount> input) {
        try {
            Page<MailAccount> mailAccountPage = super.findAll(input);
            List<MailAccount> mailAccounts = mailAccountPage.getContent();
            return new PageImpl<>(mailAccounts, mailAccountPage.getPageable(), mailAccountPage.getTotalElements());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search mail accounts failed");
        }
    }

    public MailAccount getMailAccountById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "MailAccount not found"));
    }

    public MailAccount getMailAccountByUsername(String username) {
        MailAccount mailAccount = repository.findByUsername(username);
        if (mailAccount == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tài khoản" + username + " không tồn tại");
        }
        return mailAccount;
    }

    public MailAccount createMailAccount(MailAccountDTO dto) {
        if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tên tài khoản không được để trống");
        }
        if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Mật khẩu không được để trống");
        }
        if (repository.existsByUsername(dto.getUsername())) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Mail này đã tồn tại");
        }
        MailAccount mailAccount = new MailAccount();
        mailAccount.setUsername(dto.getUsername());
        mailAccount.setPassword(dto.getPassword());
        return repository.save(mailAccount);
    }

    public MailAccount updateMailAccount(Integer id, MailAccountDTO dto) {
        if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tên tài khoản không được để trống");
        }
        if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Mật khẩu không được để trống");
        }
        MailAccount mailAccount = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "MailAccount not found"));
        mailAccount.setUsername(dto.getUsername());
        mailAccount.setPassword(dto.getPassword());
        return repository.save(mailAccount);
    }

    public void deleteMailAccount(Integer id) {
        MailAccount mailAccount = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "MailAccount not found"));
        repository.delete(mailAccount);
    }
}

