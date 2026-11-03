package exceptions;

public class ReporterNotSupportedException extends RuntimeException {

    public ReporterNotSupportedException(String reporter) {
        super(String.format("[%s] reporter not supported.", reporter));
    }
}
