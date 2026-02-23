public class ExportResult {
    public final String contentType;
    public final byte[] bytes;
    public final String errorMessage;

    public ExportResult(String contentType, byte[] bytes) {
        this.contentType = contentType;
        this.bytes = bytes;
        this.errorMessage = null;
    }

    private ExportResult(String errorMessage) {
        this.contentType = null;
        this.bytes = new byte[0];
        this.errorMessage = errorMessage;
    }

    public static ExportResult error(String message) {
        return new ExportResult(message);
    }

    public boolean isError() {
        return errorMessage != null;
    }
}
