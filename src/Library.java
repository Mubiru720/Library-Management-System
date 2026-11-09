
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

        } else if (findBookById(id) != null) {
            return "A book with ID " + id + " already exists.";
            
        } else {
            books[bookCount] = new Book(id, title.trim(), author.trim(), category.trim());
            bookCount++;
            return "Book added successfully.";
        }
    }
    
    // Looks for a book by ID. Returns the Book or null if not found.
    public Book findBookById(int id) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i].getBookId() == id) {
                return books[i];
            }
        }
        return null;
    }
    
    // Searches by title, author, ID or category. Returns an array of all matches.
    public Book[] searchBooks(String searchType, String keyword) {
    keyword = keyword.trim().toLowerCase();

    Book[] found = new Book[bookCount]; // big enough for the worst case
    int foundCount = 0;

    // The loop visits every book...
    for (int i = 0; i < bookCount; i++) {
        boolean match = false;

        // ...and the switch decides WHAT to compare for each one
        switch (searchType) {
            case "Title":
                match = books[i].getTitle().toLowerCase().contains(keyword);
                break;
            case "Author":
                match = books[i].getAuthor().toLowerCase().contains(keyword);
                break;
            case "ID":
                match = String.valueOf(books[i].getBookId()).equals(keyword);
                break;
            case "Category":
                match = books[i].getCategory().toLowerCase().contains(keyword);
                break;    
        }
        

        if (match) {
            found[foundCount] = books[i];
            foundCount++;
        }
    }
    // Copy into an array of exactly the right size
    Book[] results = new Book[foundCount];
    for (int i = 0; i < foundCount; i++) {
        results[i] = found[i];
    }
    return results;
}

//Returns the total number of books stored in the library
    public Book[] getAllBooks() {
        Book[] result = new Book[bookCount];
        for (int i = 0; i < bookCount; i++) {
            result[i] = books[i];
        }
        return result;
    }

    public int getBookCount() {
        return bookCount;
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
  // Searches for a member by ID. Returns the Member or null if not found.
    public Member findMemberById(String memberId) {
        int i = 0;
        while (i < memberCount) {
            if (members[i].getMemberId() == memberId) {
                return members[i];
            }
            i++;
        }
        return null;
    }

//Returns the total number of members stored in the library
    public Member[] getAllMembers() {
        Member[] result = new Member[memberCount];
        for (int i = 0; i < memberCount; i++) {
            result[i] = members[i];
        }
        return result;
    }
    public int getMemberCount() {
        return memberCount;
    }


} 
   

