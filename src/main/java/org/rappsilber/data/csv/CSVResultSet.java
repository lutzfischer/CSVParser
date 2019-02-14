/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * minimal implementation of a ResultSet on top of the CSV-Parser
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public class CSVResultSet implements ResultSet {
    CsvParser inner;
    CSVRandomAccess innerRA;

    public CSVResultSet(CsvParser inner) {
        this.inner = inner;
        if (inner instanceof CSVRandomAccess)
            this.innerRA = innerRA;
        else
            this.innerRA = null;
            
    }
    
    
    
    @Override
    public boolean next() throws SQLException {
        try {
            return inner.next();
        } catch (IOException ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void close() throws SQLException {
        try {
            inner.close();
        } catch (IOException ex) {
            throw new SQLException(ex);
        }
    }


    @Override
    public String getString(int field) throws SQLException {
        return inner.getValue(field);
    }

    @Override
    public boolean getBoolean(int field) throws SQLException {
        return inner.getBool(field);
    }

    @Override
    public byte getByte(int field) throws SQLException {
        return Byte.valueOf(inner.getValue(field));
    }

    @Override
    public short getShort(int field) throws SQLException {
        return Short.valueOf(inner.getValue(field));
    }

    @Override
    public int getInt(int field) throws SQLException {
        return inner.getInteger(field);
    }

    @Override
    public long getLong(int field) throws SQLException {
        return inner.getInteger(field);
    }

    @Override
    public float getFloat(int field) throws SQLException {
        return (float) inner.getDouble(field);
    }

    @Override
    public double getDouble(int field) throws SQLException {
        return inner.getDouble(field);
    }

    
    @Override
    public BigDecimal getBigDecimal(int field, int arg1) throws SQLException {
        return BigDecimal.valueOf(inner.getDouble(field));
    }

    @Override
    public byte[] getBytes(int field) throws SQLException {
        return inner.getValue(field).getBytes();
    }

    @Override
    public Date getDate(int field) throws SQLException {
        return Date.valueOf(inner.getValue(field));
    }

    @Override
    public Time getTime(int field) throws SQLException {
        return Time.valueOf(inner.getValue(field));
    }

    @Override
    public Timestamp getTimestamp(int field) throws SQLException {
        return Timestamp.valueOf(inner.getValue(field));
    }


    @Override
    public String getString(String field) throws SQLException {
        return inner.getValue(field);
    }

    @Override
    public boolean getBoolean(String field) throws SQLException {
        return inner.getBool(field);
    }

    @Override
    public byte getByte(String field) throws SQLException {
        return Byte.valueOf(inner.getValue(field));
    }

    @Override
    public short getShort(String field) throws SQLException {
        return Short.valueOf(inner.getValue(field));
    }

    @Override
    public int getInt(String field) throws SQLException {
        return (int) inner.getInteger(field);
    }

    @Override
    public long getLong(String field) throws SQLException {
        return Long.valueOf(inner.getValue(field));
    }

    @Override
    public float getFloat(String field) throws SQLException {
        return (float)inner.getDouble(field);
    }

    @Override
    public double getDouble(String field) throws SQLException {
        return inner.getDouble(field);
    }

    @Override
    public BigDecimal getBigDecimal(String field, int arg1) throws SQLException {
        return BigDecimal.valueOf(inner.getDouble(field));
    }

    @Override
    public byte[] getBytes(String field) throws SQLException {
        return inner.getValue(field).getBytes();
    }

    @Override
    public Date getDate(String field) throws SQLException {
        return Date.valueOf(inner.getValue(field));
    }

    @Override
    public Time getTime(String field) throws SQLException {
        return Time.valueOf(inner.getValue(field));
    }

    @Override
    public Timestamp getTimestamp(String field) throws SQLException {
        return Timestamp.valueOf(inner.getValue(field));
    }

    @Override
    public Object getObject(int field) throws SQLException {
        return inner.getValue(field);
    }

    @Override
    public Object getObject(String field) throws SQLException {
        return inner.getValue(field);
    }

    @Override
    public int findColumn(String field) throws SQLException {
        return inner.getColumn(field);
    }

    @Override
    public BigDecimal getBigDecimal(int field) throws SQLException {
        return BigDecimal.valueOf(inner.getDouble(field));
    }

    @Override
    public BigDecimal getBigDecimal(String field) throws SQLException {
        return BigDecimal.valueOf(inner.getDouble(field));
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        if (innerRA != null) {
            return innerRA.getRow() == -1;
        } else {
            throw new UnsupportedOperationException("Only supported with random acces Parser."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        if (innerRA != null) {
            return innerRA.getRow() >= innerRA.m_data.size();
        } else {
            throw new UnsupportedOperationException("Only supported with random acces Parser."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean isFirst() throws SQLException {
        if (innerRA != null) {
            return innerRA.getRow() == 0;
        } else {
            throw new UnsupportedOperationException("Only supported with random acces Parser."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean isLast() throws SQLException {
        if (innerRA != null) {
            return innerRA.getRow() == innerRA.m_data.size()-1;
        } else {
            throw new UnsupportedOperationException("Only supported with random acces Parser."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void beforeFirst() throws SQLException {
        if (innerRA != null) {
            innerRA.setRow(-1);
        } else {
            throw new UnsupportedOperationException("Only supported with random acces Parser."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void afterLast() throws SQLException {
        if (innerRA != null) {
            innerRA.setRow(innerRA.m_data.size());
        } else {
            throw new UnsupportedOperationException("Only supported with random acces Parser."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean first() throws SQLException {
        if (innerRA != null) {
            innerRA.setRow(0);
            return innerRA.activeRow();
        } else {
            throw new UnsupportedOperationException("Only supported with random acces Parser."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean last() throws SQLException {
        if (innerRA != null) {
            innerRA.setRow(innerRA.m_data.size()-1);
            return innerRA.activeRow();
        } else {
            throw new UnsupportedOperationException("Only supported with random acces Parser."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public int getRow() throws SQLException {
        return inner.getRow();
    }

    @Override
    public boolean absolute(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean relative(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFetchDirection(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFetchSize(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getType() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNull(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBoolean(int field, boolean arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateByte(int field, byte arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateShort(int field, short arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateInt(int field, int arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateLong(int field, long arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFloat(int field, float arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateDouble(int field, double arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBigDecimal(int field, BigDecimal arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateString(int field, String arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBytes(int field, byte[] arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateDate(int field, Date arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTime(int field, Time arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTimestamp(int field, Timestamp arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAsciiStream(int field, InputStream arg1, int arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBinaryStream(int field, InputStream arg1, int arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCharacterStream(int field, Reader arg1, int arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateObject(int field, Object arg1, int arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateObject(int field, Object arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNull(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBoolean(String field, boolean arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateByte(String field, byte arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateShort(String field, short arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateInt(String field, int arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateLong(String field, long arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFloat(String field, float arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateDouble(String field, double arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBigDecimal(String field, BigDecimal arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateString(String field, String arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBytes(String field, byte[] arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateDate(String field, Date arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTime(String field, Time arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTimestamp(String field, Timestamp arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAsciiStream(String field, InputStream arg1, int arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBinaryStream(String field, InputStream arg1, int arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCharacterStream(String field, Reader arg1, int arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateObject(String field, Object arg1, int arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateObject(String field, Object arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getObject(int field, Map<String, Class<?>> arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Ref getRef(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Blob getBlob(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clob getClob(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Array getArray(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getObject(String field, Map<String, Class<?>> arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Ref getRef(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Blob getBlob(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clob getClob(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Array getArray(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getDate(int field, Calendar arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getDate(String field, Calendar arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Time getTime(int field, Calendar arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Time getTime(String field, Calendar arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Timestamp getTimestamp(int field, Calendar arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Timestamp getTimestamp(String field, Calendar arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public URL getURL(int field) throws SQLException {
        try {
            return new URL(inner.getValue(field));
        } catch (MalformedURLException ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public URL getURL(String field) throws SQLException {
        try {
            return new URL(inner.getValue(field));
        } catch (MalformedURLException ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void updateRef(int field, Ref arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateRef(String field, Ref arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBlob(int field, Blob arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBlob(String field, Blob arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateClob(int field, Clob arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateClob(String field, Clob arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateArray(int field, Array arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateArray(String field, Array arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RowId getRowId(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RowId getRowId(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateRowId(int field, RowId arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateRowId(String field, RowId arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNString(int field, String arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNString(String field, String arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNClob(int field, NClob arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNClob(String field, NClob arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NClob getNClob(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NClob getNClob(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SQLXML getSQLXML(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SQLXML getSQLXML(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateSQLXML(int field, SQLXML arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateSQLXML(String field, SQLXML arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNString(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNString(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reader getNCharacterStream(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reader getNCharacterStream(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNCharacterStream(int field, Reader arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNCharacterStream(String field, Reader arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAsciiStream(int field, InputStream arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBinaryStream(int field, InputStream arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCharacterStream(int field, Reader arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAsciiStream(String field, InputStream arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBinaryStream(String field, InputStream arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCharacterStream(String field, Reader arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBlob(int field, InputStream arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBlob(String field, InputStream arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateClob(int field, Reader arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateClob(String field, Reader arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNClob(int field, Reader arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNClob(String field, Reader arg1, long arg2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNCharacterStream(int field, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNCharacterStream(String field, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAsciiStream(int field, InputStream arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBinaryStream(int field, InputStream arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCharacterStream(int field, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAsciiStream(String field, InputStream arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBinaryStream(String field, InputStream arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateCharacterStream(String field, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBlob(int field, InputStream arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBlob(String field, InputStream arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateClob(int field, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateClob(String field, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNClob(int field, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateNClob(String field, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T getObject(int field, Class<T> arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T getObject(String field, Class<T> arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T unwrap(Class<T> field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isWrapperFor(Class<?> field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public InputStream getAsciiStream(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getUnicodeStream(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getBinaryStream(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public InputStream getAsciiStream(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getUnicodeStream(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getBinaryStream(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
    @Override
    public Reader getCharacterStream(int field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Reader getCharacterStream(String field) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
