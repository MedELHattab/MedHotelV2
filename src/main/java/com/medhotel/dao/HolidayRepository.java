package com.medhotel.dao;

import com.medhotel.models.Holiday;

import java.util.List;

public interface HolidayRepository {
    List<Holiday> getAllHolidays();
}

