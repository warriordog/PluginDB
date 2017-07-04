package net.acomputerdog.plugindb.util;

import net.acomputerdog.plugindb.query.Callback;
import net.acomputerdog.plugindb.query.QueryCallback;
import net.acomputerdog.plugindb.query.UpdateCallback;

import java.sql.ResultSet;

public class CallbackHolder {
    private final CallbackType callbackType;

    private final Callback cb;
    private final QueryCallback qcb;
    private final UpdateCallback ucb;

    private final ResultSet resultSet;
    private final int numRows;

    public CallbackHolder(Callback cb) {
        this.callbackType = CallbackType.GENERIC;
        this.cb = cb;
        this.qcb = null;
        this.ucb = null;
        this.resultSet = null;
        this.numRows = -1;
    }

    public CallbackHolder(QueryCallback qcb, ResultSet resultSet) {
        this.callbackType = CallbackType.QUERY;
        this.cb = null;
        this.qcb = qcb;
        this.ucb = null;
        this.resultSet = resultSet;
        this.numRows = -1;
    }

    public CallbackHolder(UpdateCallback ucb, int numRows) {
        this.callbackType = CallbackType.UPDATE;
        this.cb = null;
        this.qcb = null;
        this.ucb = ucb;
        this.resultSet = null;
        this.numRows = numRows;
    }

    public CallbackType getCallbackType() {
        return callbackType;
    }

    public Callback getCb() {
        return cb;
    }

    public QueryCallback getQcb() {
        return qcb;
    }

    public UpdateCallback getUcb() {
        return ucb;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public int getNumRows() {
        return numRows;
    }

    public enum CallbackType {
        GENERIC,
        QUERY,
        UPDATE;
    }
}
