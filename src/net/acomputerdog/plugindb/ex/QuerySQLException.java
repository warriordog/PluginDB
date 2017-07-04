package net.acomputerdog.plugindb.ex;

public class QuerySQLException extends QueryException {
    public QuerySQLException() {
        super();
    }

    public QuerySQLException(String message) {
        super(message);
    }

    public QuerySQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuerySQLException(Throwable cause) {
        super(cause);
    }
}
