public class Member {
    // Attributes/information stored for each library member
    private int memberId;
    private String name;
    private String course;
    private String phone;
    private int borrowedCount = 0; // Number of books currently borrowed by the member

    // Constant representing the maximum number of books a member can borrow
    public static final int MAX_BOOKS = 2;


    // Constructor for initializing new Member objects
    public Member(int memberId, String name, String course, String phone) {
        this.memberId = memberId;
        this.name = name;
        this.course = course;
        this.phone = phone;
        this.borrowedCount = 0; // Initialize borrowed books count to 0
    }

    // Getters for retrieving the values of the attributes
    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public String getPhone() {
        return phone;
    }
    
    public int getBorrowedCount() {
        return borrowedCount;
    }
    // Setters for updating the values of the attributes
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Method to check if member has not reached the maximum borrowing limit
    public boolean canBorrow() {
        return borrowedCount < MAX_BOOKS;
    }

    // Method called when a member borrows a book, increments the borrowed count
    public void incrementBorrowed() {
        borrowedCount++;
    }

    // Method called when a member returns a book, decrements the borrowed count
    public void decrementBorrowed() {
        if (borrowedCount > 0) {
            borrowedCount--;
        }
    }

    // Method that returns the member details as a String
    @Override
    public String toString() {
        return memberId + " | " + name + " | Books borrowed: " + borrowedCount + "/" + MAX_BOOKS;
    }
}

