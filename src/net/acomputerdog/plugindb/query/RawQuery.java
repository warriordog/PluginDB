package net.acomputerdog.plugindb.query;

import net.acomputerdog.plugindb.schema.Index;

public class RawQuery extends Query {
    protected final String query;
    protected final Index[] params;

    public RawQuery(boolean isUpdate, String query, Index ... params) {
        super(isUpdate);
        this.query = query;
        this.params = params;
    }

    public String getQuery() {
        return query;
    }

    public Index getParamIdx(int idx) {
        return params[idx];
    }

    public Index getParamNum(int num) {
        return getParamIdx(num - 1);
    }
}
