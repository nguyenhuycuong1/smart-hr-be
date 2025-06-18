package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.MailTemplate;
import com.devcuong.smart_hr.dto.MailTemplateDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.MailTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MailTemplateService extends SearchService<MailTemplate> {
    @Autowired
    private MailTemplateRepository repository;

    public MailTemplateService(MailTemplateRepository repository) {
        super(repository);
    }

    public List<MailTemplate> getAllMailTemplates() {
        return repository.findAll();
    }

    public Page<MailTemplate> searchMailTemplates(PageFilterInput<MailTemplate> input) {
        try {
            Page<MailTemplate> mailTemplatePage = super.findAll(input);
            List<MailTemplate> mailTemplates = mailTemplatePage.getContent();
            return new PageImpl<>(mailTemplates, mailTemplatePage.getPageable(), mailTemplatePage.getTotalElements());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search mail templates failed");
        }
    }

    public MailTemplate createMailTemplate(MailTemplateDTO dto) {
        if(dto.getMailTemplateName() == null || dto.getMailTemplateName().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tên template không được để trống");
        }
        if (repository.existsByMailTemplateName(dto.getMailTemplateName())) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tên template đã tồn tại");
        }
        MailTemplate mailTemplate = new MailTemplate();
        mailTemplate.setMailTemplateName(dto.getMailTemplateName());
        mailTemplate.setContentTemplate(dto.getContentTemplate());
        mailTemplate.setSubjectTemplate(dto.getSubjectTemplate());
        mailTemplate.setSendingObject(dto.getSendingObject());
        mailTemplate.setDescription(dto.getDescription());
        return repository.save(mailTemplate);
    }

    public MailTemplate updateMailTemplate(Integer id, MailTemplateDTO dto) {
        if(dto.getMailTemplateName() == null || dto.getMailTemplateName().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tên template không được để trống");
        }
        MailTemplate mailTemplate = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "MailTemplate not found"));
        mailTemplate.setMailTemplateName(dto.getMailTemplateName());
        mailTemplate.setContentTemplate(dto.getContentTemplate());
        mailTemplate.setSubjectTemplate(dto.getSubjectTemplate());
        mailTemplate.setSendingObject(dto.getSendingObject());
        mailTemplate.setDescription(dto.getDescription());
        return repository.save(mailTemplate);
    }

    public void deleteMailTemplate(Integer id) {
        MailTemplate mailTemplate = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "MailTemplate not found"));
        repository.delete(mailTemplate);
    }
}

