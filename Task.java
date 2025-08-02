import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    public int id;
    public String description;
    public String status;
    public String createdAt;
    public String updatedAt;

    public Task(int id, String description){
        this.id = id;
        this.description = description;
        this.status = "todo";
        String now = getCurrentTime();
        this.createdAt = now;
        this.updatedAt = now;
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String toJson() {
        return String.format("""
            {
              "id": %d,
              "description": "%s",
              "status": "%s",
              "createdAt": "%s",
              "updatedAt": "%s"
            }""", id, description, status, createdAt, updatedAt);
    }
}
