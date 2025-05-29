package com.devcuong.smart_hr.service.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SearchSpecification<T> implements Specification<T> {
    private final T filter;
    private final String common;

    public SearchSpecification(T filter, String common) {
        this.filter = filter;
        this.common = common;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if(filter != null) {
            for (Field field : filter.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(filter);
                    if(value != null) {
                        // Xử lý theo kiểu dữ liệu
                        if (value instanceof String) {
                            // Đối với String, sử dụng LIKE
                            predicates.add(cb.like(cb.lower(root.get(field.getName())),
                                    "%" + ((String) value).toLowerCase() + "%"));
                        } else if (value instanceof LocalDate) {
                            // Đối với LocalDate, sử dụng equal
                            predicates.add(cb.equal(root.get(field.getName()), value));
                        } else if (value instanceof LocalTime) {
                            // Đối với LocalTime, chỉ tìm kiếm theo giờ, bỏ qua phút và giây
                            LocalTime timeValue = (LocalTime) value;
                            LocalTime startTime = LocalTime.of(timeValue.getHour(), 0, 0);
                            LocalTime endTime = LocalTime.of(timeValue.getHour(), 59, 59);

                            // Tạo điều kiện: field >= startTime AND field <= endTime
                            predicates.add(cb.and(
                                    cb.greaterThanOrEqualTo(root.get(field.getName()), startTime),
                                    cb.lessThanOrEqualTo(root.get(field.getName()), endTime)
                            ));
                        } else if (value instanceof Number) {
                            // Đối với Number (Integer, Long, Double...), sử dụng equal
                            predicates.add(cb.equal(root.get(field.getName()), value));
                        } else if (value instanceof Boolean) {
                            // Đối với Boolean, sử dụng equal
                            predicates.add(cb.equal(root.get(field.getName()), value));
                        } else {
                            // Đối với các kiểu khác, mặc định sử dụng equal
                            predicates.add(cb.equal(root.get(field.getName()), value));
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    // Bỏ qua các trường không thể truy cập hoặc không tồn tại
                }
            }
        }

        if(common != null && !common.isBlank()) {  // Sửa lại điều kiện này
            List<Predicate> commonPredicates = new ArrayList<Predicate>();
            for (Field field : root.getJavaType().getDeclaredFields()) {
                if (field.getType().equals(String.class)) {
                    try {
                        commonPredicates.add(cb.like(cb.lower(root.get(field.getName())),
                                "%" + common.toLowerCase() + "%"));
                    } catch (IllegalArgumentException e) {
                        // Bỏ qua các trường không thể truy cập
                    }
                }
            }
            if(!commonPredicates.isEmpty()) {
                predicates.add(cb.or(commonPredicates.toArray(new Predicate[0])));
            }
        }

        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    }
}