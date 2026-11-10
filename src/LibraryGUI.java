import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LibraryGUI is the window the user sees.
 * It contains no library rules: it reads the text fields, calls a Library
 * method, and shows the message that comes back.
 *
 * Tabs: Books (add + search + view),
 *       Members (register one or several + view),
 *       Borrow/Return (borrow, return + activity log).
 */
public class LibraryGUI extends JFrame {

    // The one Library object used by the whole GUI
    private Library library = new Library();

    // ---------- Books tab components ----------
    private JTextField bookIdField = new JTextField();
    private JTextField bookTitleField = new JTextField();
    private JTextField bookAuthorField = new JTextField();
    private JTextField bookCategoryField = new JTextField();
    private JComboBox<String> searchTypeBox = new JComboBox<>(
            new String[]{"Title", "Author", "Category", "ID"});
    private JTextField searchField = new JTextField(15);
    private DefaultTableModel bookTableModel;

    // ---------- Members tab components ----------
    private JTextField memberIdField = new JTextField();
    private JTextField memberNameField = new JTextField();
    private JTextField memberCourseField = new JTextField();
    private JTextField memberPhoneField = new JTextField();
    private DefaultTableModel memberTableModel;

    // ---------- Borrow/Return tab components ----------
    private JTextField borrowMemberIdField = new JTextField();
    private JTextField borrowBookIdField = new JTextField();
    private JTextArea logArea;
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");

    // ---------- Constructor: builds the window ----------
    public LibraryGUI() {
        setTitle("Library Management System");
        setSize(900, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        tabs.addTab("Borrow/Return", createBorrowPanel());
        add(tabs);
    }

    // =====================================================
    //                     BOOKS TAB
    // =====================================================

    private JPanel createBooksPanel() {
        // Form at the top
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Add a Book"));
        addField(form, "Book ID:", bookIdField);
        addField(form, "Title:", bookTitleField);
        addField(form, "Author:", bookAuthorField);
        addField(form, "Category:", bookCategoryField);

        JButton addButton = new JButton("Add Book");
        JButton addSeveralButton = new JButton("Add Several...");
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 8, 0));
        buttonRow.add(addButton);
        buttonRow.add(addSeveralButton);
        form.add(new JLabel(""));
        form.add(buttonRow);

        // Table in the middle
        String[] columns = {"ID", "Title", "Author", "Category", "Status"};
        bookTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(bookTableModel);
        table.setDefaultEditor(Object.class, null); // cells cannot be edited

        // Search bar at the bottom
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchTypeBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        // Put the three parts together
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(searchPanel, BorderLayout.SOUTH);
        

        // Connect buttons to their handler methods
        addButton.addActionListener(e -> handleAddBook());
        addSeveralButton.addActionListener(e -> handleAddSeveralBooks());
        searchButton.addActionListener(e -> handleSearchBooks());
        showAllButton.addActionListener(e -> refreshBookTable(library.getAllBooks()));

        return panel;
    }

    private void handleAddBook() {
        try {
            int id = Integer.parseInt(bookIdField.getText().trim());
            String message = library.addBook(id, bookTitleField.getText(),
                    bookAuthorField.getText(), bookCategoryField.getText());
            JOptionPane.showMessageDialog(this, message);

            if (message.equals("Book added successfully.")) {
                bookIdField.setText("");
                bookTitleField.setText("");
                bookAuthorField.setText("");
                bookCategoryField.setText("");
                refreshBookTable(library.getAllBooks());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID must be a number.");
        }
    }

    private void handleAddSeveralBooks() {
        int again;

        do {
            // A small form shown inside a dialog
            JTextField idField = new JTextField();
            JTextField titleField = new JTextField();
            JTextField authorField = new JTextField();
            JTextField categoryField = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
            addField(inputPanel, "Book ID:", idField);
            addField(inputPanel, "Title:", titleField);
            addField(inputPanel, "Author:", authorField);
            addField(inputPanel, "Category:", categoryField);

            int choice = JOptionPane.showConfirmDialog(this, inputPanel, "Add a Book",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                break; // user pressed Cancel: stop the loop
            }

            String message;
            try {
                int id = Integer.parseInt(idField.getText().trim());
                message = library.addBook(id, titleField.getText(),
                        authorField.getText(), categoryField.getText());
            } catch (NumberFormatException ex) {
                message = "Book ID must be a number.";
            }

            refreshBookTable(library.getAllBooks());

            // Show what happened and ask whether to continue
            again = JOptionPane.showConfirmDialog(this,
                    message + "\n\nAdd another book?",
                    "Add Several", JOptionPane.YES_NO_OPTION);

        } while (again == JOptionPane.YES_OPTION);
    }
    private void handleSearchBooks() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type something to search for.");
        } else {
            String searchType = (String) searchTypeBox.getSelectedItem();
            Book[] results = library.searchBooks(searchType, keyword);
            refreshBookTable(results);

            if (results.length == 0) {
                JOptionPane.showMessageDialog(this, "No books found.");
            }
        }
    }

    // Clears the books table and refills it with the given books
    private void refreshBookTable(Book[] list) {
        bookTableModel.setRowCount(0);

        for (int i = 0; i < list.length; i++) {
            Book b = list[i];
            String status;
            if (b.isAvailable()) {
                status = "Available";
            } else {
                status = "Borrowed";
            }
            bookTableModel.addRow(new Object[]{b.getBookId(), b.getTitle(),
                    b.getAuthor(), b.getCategory(), status});
        }
    }

    // =====================================================
    //                    MEMBERS TAB
    // =====================================================

    private JPanel createMembersPanel() {
        // Form at the top
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Register a Member"));
        addField(form, "Member ID:", memberIdField);
        addField(form, "Name:", memberNameField);
        addField(form, "Course:", memberCourseField);
        addField(form, "Phone:", memberPhoneField);

        // Two buttons side by side in the last row
        JButton registerButton = new JButton("Register Member");
        JButton registerSeveralButton = new JButton("Register Several...");
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 8, 0));
        buttonRow.add(registerButton);
        buttonRow.add(registerSeveralButton);
        form.add(new JLabel(""));
        form.add(buttonRow);


        // Table in the middle
        String[] columns = {"Member ID", "Name", "Course", "Phone", "Books Borrowed"};
        memberTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(memberTableModel);
        table.setDefaultEditor(Object.class, null);

        // Put them together
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        registerButton.addActionListener(e -> handleRegisterMember());
        registerSeveralButton.addActionListener(e -> handleRegisterSeveral());
        return panel;
    }

    private void handleRegisterMember() {
        try {
            int id = Integer.parseInt(memberIdField.getText().trim());
            String message = library.registerMember(id, memberNameField.getText(),
                    memberCourseField.getText(), memberPhoneField.getText());
            JOptionPane.showMessageDialog(this, message);

            if (message.equals("Member registered successfully.")) {
                memberIdField.setText("");
                memberNameField.setText("");
                memberCourseField.setText("");
                memberPhoneField.setText("");
                refreshMemberTable();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID must be a number.");
        }
    }

    // Registers several members one after another.
    // A do-while loop is used because the form must be shown AT LEAST ONCE,
    // and then repeated for as long as the user answers "Yes" to "Register another?".
    private void handleRegisterSeveral() {
        int again;

        do {
            // A small form shown inside a dialog
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField courseField = new JTextField();
            JTextField phoneField = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
            addField(inputPanel, "Member ID:", idField);
            addField(inputPanel, "Name:", nameField);
            addField(inputPanel, "Course:", courseField);
            addField(inputPanel, "Phone:", phoneField);

            int choice = JOptionPane.showConfirmDialog(this, inputPanel, "Register a Member",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                break; // user pressed Cancel: stop the loop
            }

            String message;
            try {
                int id = Integer.parseInt(idField.getText().trim());
                message = library.registerMember(id, nameField.getText(),
                        courseField.getText(), phoneField.getText());
            } catch (NumberFormatException ex) {
                message = "Member ID must be a number.";
            }

            refreshMemberTable();

            // Show what happened and ask whether to continue
            again = JOptionPane.showConfirmDialog(this,
                    message + "\n\nRegister another member?",
                    "Register Several", JOptionPane.YES_NO_OPTION);

        } while (again == JOptionPane.YES_OPTION);
    }

    // Clears the members table and refills it from the Library
    private void refreshMemberTable() {
        memberTableModel.setRowCount(0);
        Member[] members = library.getAllMembers();

        for (int i = 0; i < members.length; i++) {
            Member m = members[i];
            memberTableModel.addRow(new Object[]{m.getMemberId(), m.getName(), m.getCourse(),
                    m.getPhone(), m.getBorrowedCount() + "/" + Member.MAX_BOOKS});
        }
    }

    // =====================================================
    //                 BORROW / RETURN TAB
    // =====================================================

    private JPanel createBorrowPanel() {
        // Form at the top
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Borrow or Return a Book"));
        addField(form, "Member ID:", borrowMemberIdField);
        addField(form, "Book ID:", borrowBookIdField);
        

        JButton borrowButton = new JButton("Borrow Book");
        JButton borrowSeveralButton = new JButton("Borrow Several...");
        JButton returnButton = new JButton("Return Book");
        JButton returnSeveralButton = new JButton("Return Several...");
        form.add(borrowButton);
        form.add(returnButton);
        form.add(borrowSeveralButton);
        form.add(returnSeveralButton);

        // Activity log in the middle: a text area that shows every borrow/return
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Activity Log"));

        // Put them together
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(form, BorderLayout.NORTH);
        panel.add(logScroll, BorderLayout.CENTER);

        borrowButton.addActionListener(e -> handleBorrow());
        borrowSeveralButton.addActionListener(e -> handleBorrowSeveral());
        returnButton.addActionListener(e -> handleReturn());
        returnSeveralButton.addActionListener(e -> handleReturnSeveral());
        return panel;
    }

    private void handleBorrow() {
        try {
            int memberId = Integer.parseInt(borrowMemberIdField.getText().trim());
            int bookId = Integer.parseInt(borrowBookIdField.getText().trim());

            // NOTE: borrowBook takes the MEMBER id first, then the BOOK id
            String message = library.borrowBook(memberId, bookId);
            JOptionPane.showMessageDialog(this, message);
            logActivity("BORROW", memberId, bookId, message);
            finishBorrowAction();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID and Book ID must be numbers.");
        }
    }

    private void handleBorrowSeveral() {
        int again;

        do {
            // A small form shown inside a dialog
            JTextField memberIdField = new JTextField();
            JTextField bookIdField = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            addField(inputPanel, "Member ID:", memberIdField);
            addField(inputPanel, "Book ID:", bookIdField);

            int choice = JOptionPane.showConfirmDialog(this, inputPanel, "Borrow a Book",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                break; // user pressed Cancel: stop the loop
            }

            String message;
            try {
                int memberId = Integer.parseInt(memberIdField.getText().trim());
                int bookId = Integer.parseInt(bookIdField.getText().trim());
                message = library.borrowBook(memberId, bookId);
                logActivity("BORROW", memberId, bookId, message);
                finishBorrowAction();
            } catch (NumberFormatException ex) {
                message = "Member ID and Book ID must be numbers.";
                
            }

            // Show what happened and ask whether to continue
            again = JOptionPane.showConfirmDialog(this,
                    message + "\n\nBorrow another book?",
                    "Borrow Several", JOptionPane.YES_NO_OPTION);

        } while (again == JOptionPane.YES_OPTION);
    }

    private void handleReturnSeveral() {
        int again;

        do {
            // A small form shown inside a dialog
            JTextField memberIdField = new JTextField();
            JTextField bookIdField = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            addField(inputPanel, "Member ID:", memberIdField);
            addField(inputPanel, "Book ID:", bookIdField);

            int choice = JOptionPane.showConfirmDialog(this, inputPanel, "Return a Book",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                break; // user pressed Cancel: stop the loop
            }

            String message;
            try {
                int memberId = Integer.parseInt(memberIdField.getText().trim());
                int bookId = Integer.parseInt(bookIdField.getText().trim());
                message = library.returnBook(bookId, memberId);
                logActivity("RETURN", memberId, bookId, message);
                finishBorrowAction();
            } catch (NumberFormatException ex) {
                message = "Member ID and Book ID must be numbers.";
            }

            // Show what happened and ask whether to continue
            again = JOptionPane.showConfirmDialog(this,
                    message + "\n\nReturn another book?",
                    "Return Several", JOptionPane.YES_NO_OPTION);

        } while (again == JOptionPane.YES_OPTION);
    }

    private void handleReturn() {
        try {
            int memberId = Integer.parseInt(borrowMemberIdField.getText().trim());
            int bookId = Integer.parseInt(borrowBookIdField.getText().trim());

            // NOTE: returnBook takes the BOOK id first, then the MEMBER id
            String message = library.returnBook(bookId, memberId);
            JOptionPane.showMessageDialog(this, message);
            logActivity("RETURN", memberId, bookId, message);
            finishBorrowAction();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID and Book ID must be numbers.");
        }
    }

    // After a borrow or return: clear the fields and update every table
    private void finishBorrowAction() {
        borrowMemberIdField.setText("");
        borrowBookIdField.setText("");
        refreshBookTable(library.getAllBooks());
        refreshMemberTable();
    }

    // Adds one entry to the activity log: the time, the action, who and which
    // book was involved, and the result message that Library returned.
    private void logActivity(String action, int memberId, int bookId, String message) {
        String time = LocalDateTime.now().format(TIME_FORMAT);

        // Look up the names so the log is easy to read (null if the ID does not exist)
        Member member = library.findMemberById(memberId);
        Book book = library.findBookById(bookId);

        String memberText = "Member " + memberId;
        if (member != null) {
            memberText = memberText + " (" + member.getName() + ")";
        }

        String bookText = "Book " + bookId;
        if (book != null) {
            bookText = bookText + " (" + book.getTitle() + ")";
        }

        logArea.append("[" + time + "] " + action + " - " + memberText + ", " + bookText + "\n");
        logArea.append("      " + message + "\n\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // scroll to the newest entry
    }

    // =====================================================
    //                   SMALL HELPER
    // =====================================================

    // Adds one "label + text field" row to a form
    private void addField(JPanel form, String labelText, JTextField field) {
        form.add(new JLabel(labelText));
        form.add(field);
    }
}