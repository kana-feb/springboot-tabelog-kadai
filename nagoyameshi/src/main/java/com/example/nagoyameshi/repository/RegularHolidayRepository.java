package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.RegularHoliday;

public interface RegularHolidayRepository extends JpaRepository<RegularHoliday, Integer> {
    // 独自メソッドは不要（JpaRepositoryの基本メソッドを利用する）
}
