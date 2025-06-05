package com.devcuong.smart_hr.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ContractStatus {
    NHAP("NHAP"),
    DAHUY("DAHUY"),
    DANGHOATDONG("DANGHOATDONG"),
    SAPHETHAN("SAPHETHAN"),
    HETHAN("HETHAN");

    String status;

    ContractStatus(String status) {
        this.status = status;
    }
}
