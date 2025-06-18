package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.MailSender;
import com.devcuong.smart_hr.dto.MailSenderDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mail-senders")
public class MailSenderController {
    @Autowired
    private MailSenderService mailSenderService;

    @GetMapping
    public ApiResponse getAllMailSenders() {
        List<MailSender> senders = mailSenderService.getAllMailSenders();
        return ApiResponse.builder().data(senders).build().success();
    }

    @PostMapping("/search")
    public PageResponse searchMailSenders(@RequestBody PageFilterInput<MailSender> input) {
        Page<MailSender> page = mailSenderService.searchMailSenders(input);
        return PageResponse.builder().data(page.getContent()).dataCount(page.getTotalElements()).build().success();
    }

    @PostMapping("/create")
    public ApiResponse createMailSender(@RequestBody MailSenderDTO dto) {
        return ApiResponse.builder().data(mailSenderService.createMailSender(dto)).build().success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateMailSender(@PathVariable Integer id, @RequestBody MailSenderDTO dto) {
        return ApiResponse.builder().data(mailSenderService.updateMailSender(id, dto)).build().success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteMailSender(@PathVariable Integer id) {
        mailSenderService.deleteMailSender(id);
        return ApiResponse.builder().build().success();
    }
}

