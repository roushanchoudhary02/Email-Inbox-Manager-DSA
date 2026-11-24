import java.util.*;

// Email Entity Class
class Email {
    int id;
    String sender;
    String subject;
    int priority; // 1 = High (Urgent), 2 = Medium (Work), 3 = Low (Promotions)
    long timestamp;

    public Email(int id, String sender, String subject, int priority, long timestamp) {
        this.id = id;
        this.sender = sender;
        this.subject = subject;
        this.priority = priority;
        this.timestamp = timestamp;
    }

    private String getPriorityLabel() {
        switch (priority) {
            case 1: return "HIGH";
            case 2: return "MEDIUM";
            default: return "LOW";
        }
    }

    @Override
    public String toString() {
        return String.format("[ID: %d | Priority: %-6s | From: %-18s | Subject: %s]",
                id, getPriorityLabel(), sender, subject);
    }
}

public class Main {

    // 1. HASHING: Fast O(1) lookup by Email ID
    private static HashMap<Integer, Email> emailMap = new HashMap<>();

    // 2. PRIORITY QUEUE: Processes highest priority emails first (Min-Heap on priority)
    private static PriorityQueue<Email> priorityInbox = new PriorityQueue<>(
        (a, b) -> {
            if (a.priority != b.priority) {
                return Integer.compare(a.priority, b.priority); // Lower number = Higher priority
            }
            return Long.compare(b.timestamp, a.timestamp); // Newer email first if same priority
        }
    );

    // 3. STACK: LIFO structure for Undo Delete functionality
    private static Stack<Email> trashStack = new Stack<>();

    private static int emailCounter = 101;
    private static long timeCounter = 1;

    // Receive / Add a new Email
    public static void receiveEmail(String sender, String subject, int priority) {
        if (priority < 1 || priority > 3) {
            priority = 3; // Default to Low if invalid
        }
        Email newEmail = new Email(emailCounter++, sender, subject, priority, timeCounter++);
        emailMap.put(newEmail.id, newEmail);
        priorityInbox.offer(newEmail);
        System.out.println("Email Received -> " + newEmail);
    }

    // View Top Priority Email (Peek Priority Queue)
    public static void viewMostUrgentEmail() {
        if (priorityInbox.isEmpty()) {
            System.out.println("Inbox is empty!");
            return;
        }
        System.out.println("Most Urgent Email: " + priorityInbox.peek());
    }

    // Search Email by ID using Hashing
    public static void searchEmailById(int id) {
        if (emailMap.containsKey(id)) {
            System.out.println("Email Found (O(1) Hash Lookup): " + emailMap.get(id));
        } else {
            System.out.println("No active email found with ID: " + id);
        }
    }

    // Delete Email (Removes from HashMap & PriorityQueue, Pushes to Stack)
    public static void deleteEmail(int id) {
        if (!emailMap.containsKey(id)) {
            System.out.println("Email ID " + id + " does not exist in Inbox.");
            return;
        }
        Email removed = emailMap.remove(id);
        priorityInbox.remove(removed);
        trashStack.push(removed);
        System.out.println("Moved to Trash: " + removed);
    }

    // Undo Last Delete using Stack
    public static void undoDelete() {
        if (trashStack.isEmpty()) {
            System.out.println("Trash is empty! Nothing to undo.");
            return;
        }
        Email restored = trashStack.pop();
        emailMap.put(restored.id, restored);
        priorityInbox.offer(restored);
        System.out.println("Restored from Trash: " + restored);
    }

    // 4. SORTING: Display Inbox sorted by Priority, Sender Name, or Latest First
    public static void displaySortedInbox(int sortType) {
        if (emailMap.isEmpty()) {
            System.out.println("Inbox is empty!");
            return;
        }

        List<Email> emailList = new ArrayList<>(emailMap.values());

        switch (sortType) {
            case 1: // Sort by Priority (High to Low)
                emailList.sort(Comparator.comparingInt(e -> e.priority));
                System.out.println("\n--- INBOX (Sorted by Priority) ---");
                break;
            case 2: // Sort Alphabetically by Sender
                emailList.sort(Comparator.comparing(e -> e.sender.toLowerCase()));
                System.out.println("\n--- INBOX (Sorted by Sender Name) ---");
                break;
            case 3: // Sort by Latest Arrival
                emailList.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
                System.out.println("\n--- INBOX (Sorted by Latest First) ---");
                break;
            default:
                System.out.println("Invalid sort option.");
                return;
        }

        for (Email e : emailList) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Pre-loading sample emails for quick demo during viva
        receiveEmail("HOD_CSE", "DSA Project Submission", 1);
        receiveEmail("Amazon", "Big Billion Sale Offers", 3);
        receiveEmail("Placement_Cell", "Internship Drive Registration", 1);
        receiveEmail("GitHub", "Security Alert on Repo", 2);

        while (true) {
            System.out.println("\n========= EMAIL INBOX MANAGER =========");
            System.out.println("1. Receive New Email");
            System.out.println("2. View Most Urgent Email (Priority Queue)");
            System.out.println("3. Search Email by ID (Hashing)");
            System.out.println("4. Delete Email");
            System.out.println("5. Undo Last Delete (Stack)");
            System.out.println("6. View Inbox Sorted by Priority (Sorting)");
            System.out.println("7. View Inbox Sorted by Sender (Sorting)");
            System.out.println("8. View Inbox Sorted by Latest (Sorting)");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Sender Name: ");
                    String sender = sc.nextLine();
                    System.out.print("Enter Subject: ");
                    String subject = sc.nextLine();
                    System.out.print("Enter Priority (1 = High, 2 = Medium, 3 = Low): ");
                    int priority = Integer.parseInt(sc.nextLine());
                    receiveEmail(sender, subject, priority);
                    break;
                case 2:
                    viewMostUrgentEmail();
                    break;
                case 3:
                    System.out.print("Enter Email ID to search: ");
                    int searchId = Integer.parseInt(sc.nextLine());
                    searchEmailById(searchId);
                    break;
                case 4:
                    System.out.print("Enter Email ID to delete: ");
                    int delId = Integer.parseInt(sc.nextLine());
                    deleteEmail(delId);
                    break;
                case 5:
                    undoDelete();
                    break;
                case 6:
                    displaySortedInbox(1);
                    break;
                case 7:
                    displaySortedInbox(2);
                    break;
                case 8:
                    displaySortedInbox(3);
                    break;
                case 0:
                    System.out.println("Exiting Email Inbox Manager...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
