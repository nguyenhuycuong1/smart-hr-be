package com.devcuong.smart_hr.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AttendanceStatus {

    BINHTHUONG("BINHTHUONG"),
    VANG("VANG"),
    MUON("MUON"),
    VESOM("VESOM"),
    THEMGIO("THEMGIO");

    String status;

    AttendanceStatus(String status) {
        this.status = status;
    }
}
