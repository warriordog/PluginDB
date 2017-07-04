package net.acomputerdog.plugindb.ex;

public class QueryFormatException extends QueryException {
    public QueryFormatException() {
        super();
    }

    public QueryFormatException(String message) {
        super(message);
    }

    public QueryFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryFormatException(Throwable cause) {
        super(cause);
    }
}
