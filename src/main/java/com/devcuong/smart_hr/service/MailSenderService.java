package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.MailSender;
import com.devcuong.smart_hr.dto.MailSenderDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.MailSenderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MailSenderService extends SearchService<MailSender> {
    @Autowired
    private MailSenderRepository repository;

    public MailSenderService(MailSenderRepository repository) {
        super(repository);
    }

    public List<MailSender> getAllMailSenders() {
        return repository.findAll();
    }

    public Page<MailSender> searchMailSenders(PageFilterInput<MailSender> input) {
        try {
            Page<MailSender> mailSenderPage = super.findAll(input);
            List<MailSender> mailSenders = mailSenderPage.getContent();
            return new PageImpl<>(mailSenders, mailSenderPage.getPageable(), mailSenderPage.getTotalElements());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search mail senders failed");
        }
    }

    public MailSender createMailSender(MailSenderDTO dto) {
        if (dto.getMailAccountId() == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tài khoản gửi không được để trống");
        }
        if (dto.getTo() == null || dto.getTo().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tài khoản nhận không được để trống");
        }
        if (dto.getSubject() == null || dto.getSubject().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Chủ đề không được để trống");
        }
        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Nội dung không được để trống");
        }

        MailSender mailSender = new MailSender();
        mailSender.setMailAccountId(dto.getMailAccountId());
        mailSender.setMailTemplateId(dto.getMailTemplateId());
        mailSender.setTo(dto.getTo());
        mailSender.setCc(dto.getCc());
        mailSender.setBcc(dto.getBcc());
        mailSender.setSubject(dto.getSubject());
        mailSender.setContent(dto.getContent());
        return repository.save(mailSender);
    }

    public MailSender updateMailSender(Integer id, MailSenderDTO dto) {
        if (dto.getMailAccountId() == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tài khoản gửi không được để trống");
        }
        if (dto.getTo() == null || dto.getTo().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tài khoản nhận không được để trống");
        }
        if (dto.getSubject() == null || dto.getSubject().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Chủ đề không được để trống");
        }
        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Nội dung không được để trống");
        }
        MailSender mailSender = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "MailSender not found"));
        mailSender.setMailAccountId(dto.getMailAccountId());
        mailSender.setMailTemplateId(dto.getMailTemplateId());
        mailSender.setTo(dto.getTo());
        mailSender.setCc(dto.getCc());
        mailSender.setBcc(dto.getBcc());
        mailSender.setSubject(dto.getSubject());
        mailSender.setContent(dto.getContent());
        return repository.save(mailSender);
    }

    public void deleteMailSender(Integer id) {
        MailSender mailSender = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "MailSender not found"));
        repository.delete(mailSender);
    }
}

