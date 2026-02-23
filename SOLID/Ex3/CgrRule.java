import java.util.Optional;

public class CgrRule implements EligibilityRule {
    private static final double MIN_CGR = 8.0;

    @Override
    public Optional<String> check(StudentProfile profile) {
        if (profile.cgr < MIN_CGR) {
            return Optional.of("CGR below 8.0");
        }
        return Optional.empty();
    }
}
