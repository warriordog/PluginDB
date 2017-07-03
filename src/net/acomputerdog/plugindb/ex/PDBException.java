package net.acomputerdog.plugindb.ex;

public class PDBException extends RuntimeException {
    public PDBException() {
        super();
    }

    public PDBException(String message) {
        super(message);
    }

    public PDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public PDBException(Throwable cause) {
        super(cause);
    }
}
