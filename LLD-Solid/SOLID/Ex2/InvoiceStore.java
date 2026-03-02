public interface InvoiceStore {
    void save(String invoiceId, String content);
    int countLines(String invoiceId);
}
