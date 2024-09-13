package com.medhotel.services;

import com.medhotel.dao.HolidayRepository;
import com.medhotel.models.Holiday;

import java.util.List;

public class HolidayService {
    private HolidayRepository holidayRepository;

    public HolidayService() {
        this.holidayRepository = holidayRepository;
    }

    public List<Holiday> getAllHolidays() {
        return holidayRepository.getAllHolidays();
    }
}

