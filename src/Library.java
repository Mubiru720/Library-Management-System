
public class Library {
   // Arrays for storing books and members, along with counters for the number of each.
   private Book[] books = new Book[100];
    private int bookCount = 0;

    private Member[] members = new Member[50];
    private int memberCount = 0;


    // Adding a book to the system. Returns a message describing what happened.
    public String addBook(int id, String title, String author, String category) {
        if (title.trim().isEmpty() || author.trim().isEmpty() || category.trim().isEmpty()) {
            return "Title, author, and category cannot be empty.";

        } else if (bookCount >= books.length) {
            return "Library is full. Cannot add more books.";
   
        
        } else {
            books[bookCount] = new Book(id, title.trim(), author.trim(), category.trim());
            bookCount++;
            return "Book added successfully.";
        }
    }
    
    

    // Registering a member to the system if their ID is unique and space is available
    public String registerMember(String id, String name, String course, String phone) {
        if (name.trim().isEmpty() || course.trim().isEmpty() || phone.trim().isEmpty()) {
            return "Name, course, and phone cannot be empty.";

        } else if (memberCount >= members.length) {
            return "Reached maximum member capacity. Cannot add more members.";
       
        } else {
            members[memberCount] = new Member(id, name.trim(), course.trim(), phone.trim());
            memberCount++;
            return "Member registered successfully.";
        }
    }
} 
   

