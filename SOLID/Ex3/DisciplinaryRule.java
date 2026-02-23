import java.util.Optional;

public class DisciplinaryRule implements EligibilityRule {
    @Override
    public Optional<String> check(StudentProfile profile) {
        if (profile.disciplinaryFlag != LegacyFlags.NONE) {
            return Optional.of("disciplinary flag present");
        }
        return Optional.empty();
    }
}
