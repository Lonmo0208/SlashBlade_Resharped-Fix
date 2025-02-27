package jp.nyatla.nymmd;

public class MmdException extends Exception {
    private static final long serialVersionUID = 1L;

    public MmdException() {
        super();
    }

    public MmdException(Exception e) {
        super(e);
    }

    public MmdException(String m) {
        super(m);
    }

    public static void trap(String m) throws MmdException {
        throw new MmdException("トラップ:" + m);
    }

    public static void notImplement() throws MmdException {
        throw new MmdException("Not Implement!");
    }
}
