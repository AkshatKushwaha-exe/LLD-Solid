import java.util.Optional;

public interface EligibilityRule {
    Optional<String> check(StudentProfile profile);
}
