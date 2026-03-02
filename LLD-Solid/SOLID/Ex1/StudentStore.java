import java.util.List;

public interface StudentStore {
    void save(StudentRecord r);
    int count();
    List<StudentRecord> all();
}
