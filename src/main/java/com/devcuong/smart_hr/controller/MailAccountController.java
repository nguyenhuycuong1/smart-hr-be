package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.MailAccount;
import com.devcuong.smart_hr.dto.MailAccountDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.MailAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mail-accounts")
public class MailAccountController {
    @Autowired
    private MailAccountService mailAccountService;

    @GetMapping
    public ApiResponse getAllMailAccounts() {
        List<MailAccount> accounts = mailAccountService.getAllMailAccounts();
        return ApiResponse.builder().data(accounts).build().success();
    }

    @GetMapping("/get-by-username/{username}")
    public ApiResponse getMailAccountByUsername(@PathVariable String username) {
        MailAccount account = mailAccountService.getMailAccountByUsername(username);
        return ApiResponse.builder().data(account).build().success();
    }

    @PostMapping("/search")
    public PageResponse searchMailAccounts(@RequestBody PageFilterInput<MailAccount> input) {
        Page<MailAccount> mailAccounts = mailAccountService.searchMailAccounts(input);
        return PageResponse.builder().data(mailAccounts.getContent()).dataCount(mailAccounts.getTotalElements()).build().success();
    }

    @PostMapping("/create")
    public ApiResponse createMailAccount(@RequestBody MailAccountDTO dto) {
        return ApiResponse.builder().data(mailAccountService.createMailAccount(dto)).build().success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateMailAccount(@PathVariable Integer id, @RequestBody MailAccountDTO dto) {
        return ApiResponse.builder().data(mailAccountService.updateMailAccount(id, dto)).build().success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteMailAccount(@PathVariable Integer id) {
        mailAccountService.deleteMailAccount(id);
        return ApiResponse.builder().build().success();
    }
}

