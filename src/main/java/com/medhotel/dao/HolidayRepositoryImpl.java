package com.medhotel.dao;

import com.medhotel.db.DatabaseConnection;
import com.medhotel.models.Holiday;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HolidayRepositoryImpl implements HolidayRepository {

    @Override
    public List<Holiday> getAllHolidays() {
        List<Holiday> holidays = new ArrayList<>();
        String sql = "SELECT * FROM holidays";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Holiday holiday = new Holiday(
                        rs.getInt("id"),
                        rs.getDate("holiday_date").toLocalDate()
                );
                holidays.add(holiday);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidays;
    }
}

