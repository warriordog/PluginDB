package net.acomputerdog.plugindb.query;

public abstract class Query {
    private final boolean isGet;

    private Object storedQuery;

    protected Query(boolean isGet) {
        this.isGet = isGet;
    }

    public Object getStoredQuery() {
        return storedQuery;
    }

    public boolean isGet() {
        return isGet;
    }

    public void setStoredQuery(Object storedQuery) {
        this.storedQuery = storedQuery;
    }
}
