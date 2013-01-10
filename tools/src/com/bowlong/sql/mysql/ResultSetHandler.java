package com.bowlong.sql.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler {
    public <T> T handle(ResultSet rs) throws SQLException;
}