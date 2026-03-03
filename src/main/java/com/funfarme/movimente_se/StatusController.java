package com.funfarme.movimente_se;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;

@RestController
public class StatusController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/status")
    public Status status() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return new Status("OK");
        } catch (Exception e) {
            return new Status("FAIL");
        }
    }
}
