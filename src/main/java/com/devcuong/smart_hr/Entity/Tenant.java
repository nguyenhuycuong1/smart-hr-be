package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tenant")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "tenant_id")
    Integer tenantId;

    @Column(name = "tenant_name", nullable = false)
    @JsonProperty("tenant_name")
    String tenantName;

    @Column(name = "schema_name", nullable = false)
    @JsonProperty("schema_name")
    String schemaName;

    @Column(name = "business_name", nullable = false)
    @JsonProperty("business_name")
    String businessName;

    @Column(name = "business_email", nullable = false)
    @JsonProperty("business_email")
    String businessEmail;

    @Column(name = "business_phone", nullable = false)
    @JsonProperty("business_phone")
    String businessPhone;

    @Column(name = "business_logo")
    @JsonProperty("business_logo")
    String businessLogo;

    @Column(name = "business_theme")
    @JsonProperty("business_theme")
    String businessTheme;

    @Column(name = "primary_color")
    @JsonProperty("primary_color")
    String primaryColor;

    public Tenant() {}

    public Tenant(Integer tenantId, String tenantName, String schemaName, String businessName, String businessEmail, String businessPhone, String businessLogo, String businessTheme, String primaryColor) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.schemaName = schemaName;
        this.businessName = businessName;
        this.businessEmail = businessEmail;
        this.businessPhone = businessPhone;
        this.businessLogo = businessLogo;
        this.businessTheme = businessTheme;
        this.primaryColor = primaryColor;
    }
}
