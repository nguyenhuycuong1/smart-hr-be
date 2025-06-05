package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Tenant;
import com.devcuong.smart_hr.dto.TenantDTO;
import com.devcuong.smart_hr.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;

    public Tenant getTenant() {
        return tenantRepository.findAll().getFirst();
    }

    public Tenant updateTenant(TenantDTO tenantdto) {
        Tenant tenant = getTenant();
        tenant.setBusinessName(tenantdto.getBusiness_name());
        tenant.setBusinessEmail(tenantdto.getBusiness_email());
        tenant.setBusinessPhone(tenantdto.getBusiness_phone());
        tenant.setPrimaryColor(tenantdto.getPrimary_color());
        return tenantRepository.save(tenant);
    }

}
