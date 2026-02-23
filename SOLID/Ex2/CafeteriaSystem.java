import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CafeteriaSystem {
    private final Map<String, MenuItem> menu = new LinkedHashMap<>();
    private final TaxPolicy taxPolicy;
    private final DiscountPolicy discountPolicy;
    private final InvoiceStore store;
    private int invoiceSeq = 1000;

    public CafeteriaSystem(TaxPolicy taxPolicy, DiscountPolicy discountPolicy, InvoiceStore store) {
        this.taxPolicy = taxPolicy;
        this.discountPolicy = discountPolicy;
        this.store = store;
    }

    public void addToMenu(MenuItem i) {
        menu.put(i.id, i);
    }

    public void checkout(String customerType, List<OrderLine> lines) {
        String invId = "INV-" + (++invoiceSeq);

        OrderPricing.Result pricing = OrderPricing.compute(menu, lines);
        double subtotal = pricing.subtotal;
        double taxPct = taxPolicy.taxPercent(customerType);
        double tax = subtotal * (taxPct / 100.0);
        double discount = discountPolicy.discountAmount(customerType, subtotal, lines.size());
        double total = subtotal + tax - discount;

        String printable = InvoiceFormatter.format(invId, pricing.lines, subtotal, taxPct, tax, discount, total);
        System.out.print(printable);

        store.save(invId, printable);
        System.out.println("Saved invoice: " + invId + " (lines=" + store.countLines(invId) + ")");
    }
}
