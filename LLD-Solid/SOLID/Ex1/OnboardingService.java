import java.util.List;
import java.util.Map;

public class OnboardingService {
    private final StudentStore store;
    private final RawInputParser parser;
    private final StudentValidator validator;
    private final OnboardingPrinter printer;

    public OnboardingService(StudentStore store) {
        this.store = store;
        this.parser = new RawInputParser();
        this.validator = new StudentValidator();
        this.printer = new OnboardingPrinter();
    }

    public void registerFromRawInput(String raw) {
        printer.printInput(raw);

        Map<String, String> kv = parser.parse(raw);
        List<String> errors = validator.validate(kv);
        if (!errors.isEmpty()) {
            printer.printValidationErrors(errors);
            return;
        }

        String id = IdUtil.nextStudentId(store.count());
        StudentRecord rec = new StudentRecord(
                id,
                kv.getOrDefault("name", ""),
                kv.getOrDefault("email", ""),
                kv.getOrDefault("phone", ""),
                kv.getOrDefault("program", "")
        );

        store.save(rec);
        printer.printConfirmation(rec, id, store.count());
    }
}
