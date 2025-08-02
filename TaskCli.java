import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskCli {

    static final String FILE_NAME = "tasks.json";

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("No command provided. Please use 'add', 'list', or 'remove'.");
            return;
        }

        String command = args[0];
        System.out.println("Command: " + command);

        if (command.equals("add")) {
            System.out.println("add command detected");
            if (args.length < 2) {
                System.out.println("Please provide a description for the task.");
                return;
            }
            String description = args[1];
            addTask(description);
        } else if (command.equals("list")) {
            String filter = args.length > 1 ? args[1] : "all";
            listTasks(filter);
        } else if (command.equals("mark-done") || command.equals("mark-inProgress")) {
            String idStr = args.length > 1 ? args[1] : null;
            String status = command.equals("mark-done") ? "done" : "inProgress";
            if (idStr == null) {
                System.out.println("Please provide the ID of the task to mark as done.");
                return;
            }
            updatedStatus(idStr, status);
        } else if (command.equals("update")) {
            String idStr = args.length > 1 ? args[1] : null;
            String description = args.length > 2 ? args[2] : null;
            if (idStr == null || description == null) {
                System.out.println("Please provide the ID and new description for the task.");
                return;
            }
            updatedDescription(idStr, description);
        } else if (command.equals("delete")) {
            String idStr = args.length > 1 ? args[1] : null;
            if (idStr == null) {
                System.out.println("Please provide the ID of the task to delete.");
                return;
            }
            deleteTask(idStr);
        } else {
            System.out.println("Unknown command: " + command);
        }

    }

    private static void deleteTask(String idStr) {
        int id = Integer.parseInt(idStr);

        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                System.out.println("No tasks found. The file does not exist.");
                return;
            }

            String json = new Scanner(file).useDelimiter("\\Z").next();
            List<Task> tasks = parseTasks(json);

            if (tasks.isEmpty() || tasks.stream().noneMatch(task -> task.id == id)) {
                System.out.println("No tasks found.");
                return;
            }

            System.out.println("Are you sure you want to delete task with ID " + id + "? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                tasks.removeIf(task -> task.id == id);
                saveTasks(tasks);
                System.out.println("Task with ID " + id + " deleted successfully.");
            } else if (response.equals("no")) {
                System.out.println("Deletion cancelled.");
                return;
            } else {
                System.out.println("Invalid response. Please type 'yes' or 'no'.");
                return;
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updatedDescription(String idStr, String description) {
        int id = Integer.parseInt(idStr);

        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                System.out.println("No tasks found. The file does not exist.");
                return;

            }

            String json = new Scanner(file).useDelimiter("\\Z").next();
            List<Task> tasks = parseTasks(json);

            if (tasks.isEmpty() || tasks.stream().noneMatch(task -> task.id == id)) {
                System.out.println("No tasks found.");
                return;
            }

            for (Task task : tasks) {
                if (task.id == id && task.status.equals("done")) {
                    System.out.println("Cannot update a task that is already done.");
                    return;
                }
                if (task.id == id) {
                    task.description = description;
                    task.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    saveTasks(tasks);
                    System.out.println("Task updated successfully: " + task.toJson());
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

    }

    static void updatedStatus(String idStr, String status) {
        int id = Integer.parseInt(idStr);

        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                System.out.println("No tasks found. The file does not exist.");
                return;
            }

            String json = new Scanner(file).useDelimiter("\\Z").next();
            List<Task> tasks = parseTasks(json);

            if (tasks.isEmpty() || tasks.stream().noneMatch(task -> task.id == id)) {
                System.out.println("No tasks found.");
                return;
            }

            if (status.equals("inProgress")) {
                for (Task task : tasks) {
                    if (task.id == id) {
                        if (task.status.equals("done")) {
                            System.out.println("Task already marked as done. Cannot mark as in progress.");
                            return;
                        }
                        task.status = "inProgress";
                        task.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        saveTasks(tasks);
                        System.out.println("Task marked as in progress: " + task.toJson());
                        return;
                    }
                }
            } else {
                for (Task task : tasks) {
                    if (task.id == id) {
                        task.status = "done";
                        task.updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        saveTasks(tasks);
                        System.out.println("Task marked as done: " + task.toJson());
                        return;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
    }

    static void listTasks(String filter) {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                System.out.println("No tasks found. The file does not exist.");
                return;
            }

            String json = new Scanner(file).useDelimiter("\\Z").next();
            List<Task> tasks = parseTasks(json);

            if (tasks.isEmpty()) {
                System.out.println("No tasks found.");
                return;
            }

            for (Task task : tasks) {
                boolean show = switch (filter) {
                    case "todo" -> task.status.equals("todo");
                    case "done" -> task.status.equals("done");
                    case "inProgress" -> task.status.equals("inProgress");
                    case "all" -> true;
                    default -> {
                        System.out.println("Unknown filter: " + filter);
                        yield false;
                    }
                };

                if (show) {
                    System.out.printf("[ID: %d] (%s) %s — updated at %s%n",
                            task.id, task.status, task.description, task.updatedAt);
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void addTask(String description) {
        try {
            File file = new File(FILE_NAME);

            if (!file.exists()) {
                file.createNewFile();

                FileWriter writer = new FileWriter(file);
                writer.write("[]");
                writer.close();
            }

            String json = new Scanner(file).useDelimiter("\\Z").next();
            List<Task> tasks = parseTasks(json);

            int newId = tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).id + 1;
            Task newTask = new Task(newId, description);

            tasks.add(newTask);

            saveTasks(tasks);
            System.out.println("Task added successfully: " + newTask.toJson());

        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }

    }

    private static List<Task> parseTasks(String json) {
        List<Task> tasks = new ArrayList<>();
        json = json.trim();
        if (json.equals("[]")) {
            return tasks; // Return empty list if no tasks
        }

        json = json.substring(1, json.length() - 1); // Remove brackets
        String[] taskBlocks = json.split("},\\s*\\{");

        for (String block : taskBlocks) {
            block = block.trim();
            // Add curly braces if missing due to split
            if (!block.startsWith("{"))
                block = "{" + block;
            if (!block.endsWith("}"))
                block = block + "}";

            // Extract fields
            int id = extractInt(block, "id");
            String description = extractString(block, "description");
            String status = extractString(block, "status");
            String createdAt = extractString(block, "createdAt");
            String updatedAt = extractString(block, "updatedAt");

            Task task = new Task(id, description);
            task.status = status;
            task.createdAt = createdAt;
            task.updatedAt = updatedAt;

            tasks.add(task);
        }

        return tasks;
    }

    static int extractInt(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern) + pattern.length();
        int end = json.indexOf(",", start);
        if (end == -1)
            end = json.indexOf("}", start);
        return Integer.parseInt(json.substring(start, end).trim());
    }

    static String extractString(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern) + pattern.length();
        int firstQuote = json.indexOf("\"", start + 1);
        int secondQuote = json.indexOf("\"", firstQuote + 1);
        return json.substring(firstQuote + 1, secondQuote);
    }

    static void saveTasks(List<Task> tasks) throws IOException {
        FileWriter writer = new FileWriter(FILE_NAME);
        writer.write("[\n");

        for (int i = 0; i < tasks.size(); i++) {
            writer.write(tasks.get(i).toJson());
            if (i < tasks.size() - 1)
                writer.write(",\n");
        }

        writer.write("\n]");
        writer.close();
    }
}
