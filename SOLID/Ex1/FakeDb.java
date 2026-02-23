import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeDb implements StudentStore {
    private final List<StudentRecord> rows = new ArrayList<>();

    @Override
    public void save(StudentRecord r) {
        rows.add(r);
    }

    @Override
    public int count() {
        return rows.size();
    }

    @Override
    public List<StudentRecord> all() {
        return Collections.unmodifiableList(rows);
    }
}
