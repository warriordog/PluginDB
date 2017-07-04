package net.acomputerdog.plugindb.util;

import net.acomputerdog.plugindb.query.Callback;
import net.acomputerdog.plugindb.query.QueryCallback;
import net.acomputerdog.plugindb.query.UpdateCallback;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class CallbackManager {

    private final Semaphore lock = new Semaphore(1);

    private final Queue<CallbackHolder> callbackQueue;
    //private final Queue<Callback> genericQueue;
    //private final Queue<Pair<QueryCallback, ResultSet>> queryQueue;
    //private final Queue<Pair<UpdateCallback, Integer>> updateQueue;

    public CallbackManager() {
        callbackQueue = new LinkedList<>();
    }

    public void addCallback(Callback callback) {
        lock.acquireUninterruptibly();
        try {
            callbackQueue.add(new CallbackHolder(callback));
        } finally {
            lock.release();
        }
    }

    public void addQueryCallback(QueryCallback callback, ResultSet resultSet) {
        lock.acquireUninterruptibly();
        try {
            callbackQueue.add(new CallbackHolder(callback, resultSet));
        } finally {
            lock.release();
        }
    }

    public void addUpdateCallback(UpdateCallback callback, int numRows) {
        lock.acquireUninterruptibly();
        try {
            callbackQueue.add(new CallbackHolder(callback, numRows));
        } finally {
            lock.release();
        }
    }

    public void onTick() {
        CallbackHolder holder = null;
        lock.acquireUninterruptibly();
        try {
            if (!callbackQueue.isEmpty()) {
                holder = callbackQueue.poll();
            }
        } finally {
            lock.release();
        }

        if (holder != null) {
            switch (holder.getCallbackType()) {
                case GENERIC:
                    holder.getCb().onComplete();
                    break;
                case QUERY:
                    holder.getQcb().onQueryComplete(holder.getResultSet());
                    break;
                case UPDATE:
                    holder.getUcb().onUpdateComplete(holder.getNumRows());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown query type: " + holder.getCallbackType());
            }
        }
    }
}
