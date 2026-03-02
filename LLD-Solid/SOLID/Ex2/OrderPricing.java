import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderPricing {
    public static class LineDetail {
        public final String text;
        public final double lineTotal;

        public LineDetail(String text, double lineTotal) {
            this.text = text;
            this.lineTotal = lineTotal;
        }
    }

    public static class Result {
        public final double subtotal;
        public final List<LineDetail> lines;

        public Result(double subtotal, List<LineDetail> lines) {
            this.subtotal = subtotal;
            this.lines = lines;
        }
    }

    public static Result compute(Map<String, MenuItem> menu, List<OrderLine> orderLines) {
        double subtotal = 0.0;
        List<LineDetail> lines = new ArrayList<>();
        for (OrderLine l : orderLines) {
            MenuItem item = menu.get(l.itemId);
            double lineTotal = item.price * l.qty;
            subtotal += lineTotal;
            lines.add(new LineDetail(
                    String.format("- %s x%d = %.2f", item.name, l.qty, lineTotal),
                    lineTotal
            ));
        }
        return new Result(subtotal, lines);
    }
}
