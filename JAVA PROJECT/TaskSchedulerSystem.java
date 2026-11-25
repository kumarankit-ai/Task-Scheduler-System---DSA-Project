import java.util.Scanner;

// Task class
class Task {
    String id;
    String name;
    int priority;
    int arrivalOrder;

    Task(String id, String name, int priority, int arrivalOrder) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.arrivalOrder = arrivalOrder;
    }
}

// Custom Min Heap
class TaskHeap {
    Task[] heap;
    int size;

    TaskHeap() {
        heap = new Task[10];
        size = 0;
    }

    // Compare priority and arrival order
    boolean isHigherPriority(Task a, Task b) {
        if (a.priority != b.priority) {
            return a.priority < b.priority;
        }
        return a.arrivalOrder < b.arrivalOrder;
    }

    // Insert task into heap
    void insert(Task task) {
        if (size == heap.length) {
            Task[] newHeap = new Task[heap.length * 2];

            for (int i = 0; i < size; i++) {
                newHeap[i] = heap[i];
            }

            heap = newHeap;
        }

        heap[size] = task;
        int index = size;
        size++;

        // Heapify Up
        while (index > 0) {
            int parent = (index - 1) / 2;

            if (isHigherPriority(heap[index], heap[parent])) {
                Task temp = heap[index];
                heap[index] = heap[parent];
                heap[parent] = temp;

                index = parent;
            } else {
                break;
            }
        }
    }

    // Execute highest priority task
    Task removeHighest() {
        if (size == 0) {
            return null;
        }

        Task result = heap[0];

        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        // Heapify Down
        int index = 0;

        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size &&
                isHigherPriority(heap[left], heap[smallest])) {
                smallest = left;
            }

            if (right < size &&
                isHigherPriority(heap[right], heap[smallest])) {
                smallest = right;
            }

            if (smallest != index) {
                Task temp = heap[index];
                heap[index] = heap[smallest];
                heap[smallest] = temp;

                index = smallest;
            } else {
                break;
            }
        }

        return result;
    }

    // Return copy of pending tasks
    Task[] getPendingTasks() {
        Task[] copy = new Task[size];

        for (int i = 0; i < size; i++) {
            copy[i] = heap[i];
        }

        return copy;
    }

    boolean isEmpty() {
        return size == 0;
    }

    int getSize() {
        return size;
    }
}

// Completed Tasks FIFO Queue
class CompletedQueue {

    class Node {
        Task task;
        Node next;

        Node(Task task) {
            this.task = task;
            next = null;
        }
    }

    Node front, rear;

    // Add completed task
    void enqueue(Task task) {
        Node newNode = new Node(task);

        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
    }

    // Display completed tasks
    void display() {
        if (front == null) {
            System.out.println("No completed tasks.");
            return;
        }

        System.out.print("Completed: ");

        Node temp = front;

        while (temp != null) {
            System.out.print(temp.task.id);

            if (temp.next != null) {
                System.out.print(", ");
            }

            temp = temp.next;
        }

        System.out.println();
    }
}

// Main Class
public class TaskSchedulerSystem {

    static Scanner sc = new Scanner(System.in);

    static TaskHeap pendingTasks = new TaskHeap();
    static CompletedQueue completedTasks = new CompletedQueue();

    static int taskCounter = 1;
    static int arrivalCounter = 1;

    // Add new task
    static void addTask() {

        System.out.print("Enter task name: ");
        String name = sc.nextLine();

        System.out.print("Enter priority (1-5): ");

        int priority;

        try {
            priority = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid priority. Enter a value from 1 to 5.");
            return;
        }

        if (priority < 1 || priority > 5) {
            System.out.println("Invalid priority. Enter a value from 1 to 5.");
            return;
        }

        String id = "T" + taskCounter++;

        Task task = new Task(
            id, name, priority, arrivalCounter++
        );

        pendingTasks.insert(task);

        System.out.println(
            "Task added. ID: " + id + " | " +
            name + " | P" + priority
        );
    }

    // Execute next task
    static void executeNext() {

        if (pendingTasks.isEmpty()) {
            System.out.println("No tasks are pending.");
            return;
        }

        Task task = pendingTasks.removeHighest();

        completedTasks.enqueue(task);

        System.out.println(
            "Executing " + task.id + ": " +
            task.name + " (P" + task.priority + ")"
        );

        System.out.println(
            "Pending tasks: " + pendingTasks.getSize()
        );
    }

    // Display pending tasks using Insertion Sort
    static void displayPending() {

        if (pendingTasks.isEmpty()) {
            System.out.println("No pending tasks.");
            return;
        }

        Task[] tasks = pendingTasks.getPendingTasks();

        // Insertion Sort
        for (int i = 1; i < tasks.length; i++) {

            Task key = tasks[i];
            int j = i - 1;

            while (j >= 0 &&
                (tasks[j].priority > key.priority ||
                (tasks[j].priority == key.priority &&
                tasks[j].arrivalOrder > key.arrivalOrder))) {

                tasks[j + 1] = tasks[j];
                j--;
            }

            tasks[j + 1] = key;
        }

        System.out.println("Pending Tasks (Sorted by Priority):");

        for (Task task : tasks) {
            System.out.println(
                task.id + " (" + task.name +
                ", P" + task.priority + ")"
            );
        }
    }

    // Main Menu
    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== TASK SCHEDULER SYSTEM =====");
            System.out.println("1. Add Task");
            System.out.println("2. Execute Next Task");
            System.out.println("3. View Pending Tasks");
            System.out.println("4. View Completed Tasks");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");

            String input = sc.nextLine();

            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            switch (choice) {

                case 1:
                    addTask();
                    break;

                case 2:
                    executeNext();
                    break;

                case 3:
                    displayPending();
                    break;

                case 4:
                    completedTasks.display();
                    break;

                case 5:
                    System.out.println("Exiting Task Scheduler System...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}