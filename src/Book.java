public class Book {

    // Attributes/Information stored for each book
    private int bookId;
    private String title;
    private String author;
    private String category;
    private boolean available;

    // Constructor for initializing a new Book object
    public Book(int bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.available = true;
    }

    // Getter methods for retrieving the values of the attributes
    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    // Setter methods for updating the values of the attributes
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    

    // Method that returns the book details as a String
    @Override
    public String toString() {
        return "Book ID: " + bookId
                + ", Title: " + title
                + ", Author: " + author
                + ", Category: " + category
                + ", Available: " + (available ? "Yes" : "No");
    }
}
