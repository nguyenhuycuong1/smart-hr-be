package com.devcuong.smart_hr.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum RecruitmentRequestStatus {

    KHOITAO("KHOITAO", "Khởi tạo"),
    DANGDUYET("DANGDUYET", "Đang duyệt"),
    PHEDUYET("PHEDUYET", "Phê duyệt"),
    TUCHOI("TUCHOI", "Từ chối"),
    DANGBAI("DANGBAI", "Đăng bài"),
    HOANTHANH("HOANTHANH", "Hoàn thành");

    String value;
    String description;

    RecruitmentRequestStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
