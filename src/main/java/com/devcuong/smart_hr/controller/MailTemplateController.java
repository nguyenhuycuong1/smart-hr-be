package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.MailTemplate;
import com.devcuong.smart_hr.dto.MailTemplateDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.MailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mail-templates")
public class MailTemplateController {
    @Autowired
    private MailTemplateService mailTemplateService;

    @GetMapping
    public ApiResponse getAllMailTemplates() {
        List<MailTemplate> templates = mailTemplateService.getAllMailTemplates();
        return ApiResponse.builder().data(templates).build().success();
    }

    @PostMapping("/search")
    public PageResponse searchMailTemplates(@RequestBody PageFilterInput<MailTemplate> input) {
        Page<MailTemplate> page = mailTemplateService.searchMailTemplates(input);
        return PageResponse.builder().data(page.getContent()).dataCount(page.getTotalElements()).build().success();
    }

    @PostMapping("/create")
    public ApiResponse createMailTemplate(@RequestBody MailTemplateDTO dto) {
        return ApiResponse.builder().data(mailTemplateService.createMailTemplate(dto)).build().success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateMailTemplate(@PathVariable Integer id, @RequestBody MailTemplateDTO dto) {
        return ApiResponse.builder().data(mailTemplateService.updateMailTemplate(id, dto)).build().success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteMailTemplate(@PathVariable Integer id) {
        mailTemplateService.deleteMailTemplate(id);
        return ApiResponse.builder().build().success();
    }
}

