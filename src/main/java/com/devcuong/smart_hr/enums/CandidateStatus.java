package com.devcuong.smart_hr.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum CandidateStatus {
    KHOITAO("KHOITAO", "Khởi tạo"),
    DANGUNGTUYEN("DANGUNGTUYEN", "Đang ứng tuyển"),
    KHONGDAT("KHONGDAT", "Không đạt"),
    TRUNGTUYEN("TRUNGTUYEN", "Trúng tuyển"),
    NHANVIEC("NHANVIEC", "Nhậc việc");

    String value;
    String description;

     CandidateStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }


}
