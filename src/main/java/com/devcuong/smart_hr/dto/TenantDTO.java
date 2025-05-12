package com.devcuong.smart_hr.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantDTO {

    String tenant_name;
    String schema_name;
    String business_name;
    String business_email;
    String business_phone;
    String business_logo;
    String business_theme;
    String primary_color;

    TenantDTO(){}

    public TenantDTO(String tenant_name, String schema_name, String business_name, String business_email, String business_phone, String business_logo, String business_theme, String primary_color) {
        this.tenant_name = tenant_name;
        this.schema_name = schema_name;
        this.business_name = business_name;
        this.business_email = business_email;
        this.business_phone = business_phone;
        this.business_logo = business_logo;
        this.business_theme = business_theme;
        this.primary_color = primary_color;
    }
}
