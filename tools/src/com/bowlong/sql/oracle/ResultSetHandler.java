package com.bowlong.sql.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler {
    public <T> T handle(ResultSet rs) throws SQLException;
}