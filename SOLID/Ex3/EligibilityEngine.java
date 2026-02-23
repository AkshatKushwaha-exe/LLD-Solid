import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EligibilityEngine {
    private final List<EligibilityRule> rules;
    private final FakeEligibilityStore store;

    public EligibilityEngine(FakeEligibilityStore store) {
        this.store = store;
        this.rules = List.of(
                new DisciplinaryRule(),
                new CgrRule(),
                new AttendanceRule(),
                new CreditsRule()
        );
    }

    public void runAndPrint(StudentProfile s) {
        ReportPrinter p = new ReportPrinter();
        EligibilityEngineResult r = evaluate(s);
        p.print(s, r);
        store.save(s.rollNo, r.status);
    }

    public EligibilityEngineResult evaluate(StudentProfile s) {
        List<String> reasons = new ArrayList<>();
        for (EligibilityRule rule : rules) {
            Optional<String> reason = rule.check(s);
            if (reason.isPresent()) {
                reasons.add(reason.get());
                return new EligibilityEngineResult("NOT_ELIGIBLE", reasons);
            }
        }
        return new EligibilityEngineResult("ELIGIBLE", reasons);
    }
}
