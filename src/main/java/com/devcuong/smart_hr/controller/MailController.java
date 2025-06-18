package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.dto.MailSenderDTO;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.Result;
import com.devcuong.smart_hr.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/send")
    public ApiResponse sendMail(@RequestBody MailSenderDTO mailSenderDTO) {
        try {
            mailService.sendMail(mailSenderDTO);
            return ApiResponse.builder().build().success();
        } catch (Exception e) {
            return ApiResponse.builder().result(
                    Result.builder()
                            .build().errorCode(500)
                            .message("Failed to send email: " + e.getMessage())
            ).build();
        }
    }

}
