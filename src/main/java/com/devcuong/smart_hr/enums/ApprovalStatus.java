package com.devcuong.smart_hr.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ApprovalStatus {
    DANGCHO("DANGCHO"),
    PHEDUYET("PHEDUYET"),
    TUCHOI("TUCHOI");

    String status;

    ApprovalStatus(String status) {
        this.status = status;
    }


}
