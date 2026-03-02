import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentValidator {
    private static final List<String> ALLOWED_PROGRAMS = List.of("CSE", "AI", "SWE");

    public List<String> validate(Map<String, String> kv) {
        List<String> errors = new ArrayList<>();
        String name = kv.getOrDefault("name", "");
        String email = kv.getOrDefault("email", "");
        String phone = kv.getOrDefault("phone", "");
        String program = kv.getOrDefault("program", "");

        if (name.isBlank()) errors.add("name is required");
        if (email.isBlank() || !email.contains("@")) errors.add("email is invalid");
        if (phone.isBlank() || !phone.chars().allMatch(Character::isDigit)) errors.add("phone is invalid");
        if (!ALLOWED_PROGRAMS.contains(program)) errors.add("program is invalid");

        return errors;
    }
}
