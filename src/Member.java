public class Member {
    // Attributes/information stored for each library member
    private String memberId;
    private String name;
    private String course;
    private String phone;

    // Constructor for initializing new Member objects
    public Member(String memberId, String name, String course, String phone) {
        this.memberId = memberId;
        this.name = name;
        this.course = course;
        this.phone = phone;
    }

    // Getters for retrieving the values of the attributes
    public String getMemberId() {
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

    // Setters for updating the values of the attributes
    public void setMemberId(String memberId) {
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

    // Method that returns the member details as a String
    @Override
    public String toString() {
        return "Member ID: " + memberId
                + ", Name: " + name
                + ", Course: " + course
                + ", Phone: " + phone;
    }
}

