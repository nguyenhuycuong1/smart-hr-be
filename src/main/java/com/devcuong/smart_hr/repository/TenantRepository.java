package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {

    @Query(value = "SELECT current_schema()", nativeQuery = true)
    String getCurrentSchema();

    @Query(value = "SELECT current_database()", nativeQuery = true)
    String getCurrentDatabase();
}
