import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LibraryGUI is the window the user sees.
 * It contains no library rules: it reads the text fields, calls a Library
 * method, and shows the message that comes back.
 * All colours and fonts come from UITheme.
 *
 * The banner at the top shows how many books and members are stored.
 * Tabs: Books (add one or several + search + view),
 *       Members (register one or several + view),
 *       Borrow/Return (borrow, return + activity log).
 */
public class LibraryGUI extends JFrame {

    // How many table rows are always visible (the tables scroll after that)
    private static final int VISIBLE_ROWS = 10;

    // The one Library object used by the whole GUI
    private Library library = new Library();

    // ---------- Banner components (the counters) ----------
    private JLabel bookCountLabel = new JLabel("0");
    private JLabel memberCountLabel = new JLabel("0");

    // ---------- Books tab components ----------
    private JTextField bookIdField = new JTextField();
    private JTextField bookTitleField = new JTextField();
    private JTextField bookAuthorField = new JTextField();
    private JTextField bookCategoryField = new JTextField();
    private JComboBox<String> searchTypeBox = new JComboBox<>(
            new String[]{"Title", "Author", "Category", "ID"});
    private JTextField searchField = new JTextField(18);
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
        UITheme.setup(); // make pop-up dialogs use our fonts

        setTitle("Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BACKGROUND);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.BOLD);
        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        tabs.addTab("Borrow/Return", createBorrowPanel());

        add(createBanner(), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        updateCounts(); // show the starting numbers (0 and 0)

        // pack() sizes the window to fit everything, which includes room for
        // VISIBLE_ROWS table rows. Then we make sure it is never smaller than that.
        pack();
        setSize(Math.max(getWidth(), 900), getHeight());
        setMinimumSize(getSize());
        setLocationRelativeTo(null); // centre on screen
    }

    // =====================================================
    //            BANNER AND COUNTERS (top of window)
    // =====================================================

    // The dark header strip: the title on the left, the two counters on the right
    private JPanel createBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(UITheme.PRIMARY);
        banner.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Library Management System");
        title.setFont(UITheme.TITLE);
        title.setForeground(Color.WHITE);

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        stats.setOpaque(false);
        stats.add(createStatBox(bookCountLabel, "BOOKS"));
        stats.add(createStatBox(memberCountLabel, "MEMBERS"));

        banner.add(title, BorderLayout.WEST);
        banner.add(stats, BorderLayout.EAST);
        return banner;
    }

    // A small lighter box showing a big number with a caption underneath
    private JPanel createStatBox(JLabel numberLabel, String caption) {
        numberLabel.setFont(new Font(UITheme.TITLE.getFamily(), Font.BOLD, 22));
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(UITheme.SMALL);
        captionLabel.setForeground(UITheme.BORDER);
        captionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(UITheme.PRIMARY.brighter());
        box.setBorder(BorderFactory.createEmptyBorder(4, 22, 4, 22));
        box.add(numberLabel, BorderLayout.CENTER);
        box.add(captionLabel, BorderLayout.SOUTH);
        return box;
    }

    // Reads the totals from the Library and updates the two counters.
    // These are the TOTAL stored, not just the rows currently shown in a table.
    private void updateCounts() {
        bookCountLabel.setText(String.valueOf(library.getBookCount()));
        memberCountLabel.setText(String.valueOf(library.getMemberCount()));
    }

    // =====================================================
    //                 SHARED HELPER METHODS
    // =====================================================

    // Builds a white "card" with labelled input fields and a row of buttons.
    // The fields are split into 'columns' groups side by side (1 or 2),
    // which keeps the form short so the table below has room for its rows.
    private JPanel buildForm(String title, String[] labelTexts, JTextField[] fields,
                             JPanel buttonRow, int columns) {
        JPanel inputs = new JPanel(new GridLayout(1, columns, 25, 0));
        inputs.setOpaque(false);

        int perColumn = (fields.length + columns - 1) / columns;
        for (int c = 0; c < columns; c++) {
            int start = c * perColumn;
            int end = Math.min(fields.length, start + perColumn);
            inputs.add(createFieldGroup(labelTexts, fields, start, end));
        }

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UITheme.CARD);
        card.setBorder(UITheme.cardBorder(title));
        card.add(inputs, BorderLayout.NORTH);
        card.add(buttonRow, BorderLayout.SOUTH);
        return card;
    }

    // One group of "label | text field" rows (fields start up to end-1)
    private JPanel createFieldGroup(String[] labelTexts, JTextField[] fields, int start, int end) {
        int rows = end - start;
        JPanel labelColumn = new JPanel(new GridLayout(rows, 1, 0, 10));
        JPanel fieldColumn = new JPanel(new GridLayout(rows, 1, 0, 10));
        labelColumn.setOpaque(false);
        fieldColumn.setOpaque(false);

        for (int i = start; i < end; i++) {
            labelColumn.add(UITheme.label(labelTexts[i]));
            UITheme.styleField(fields[i]);
            fieldColumn.add(fields[i]);
        }

        JPanel group = new JPanel(new BorderLayout(12, 0));
        group.setOpaque(false);
        group.add(labelColumn, BorderLayout.WEST);
        group.add(fieldColumn, BorderLayout.CENTER);
        return group;
    }

    // A right-aligned row of buttons
    private JPanel createButtonRow(JButton... buttons) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        row.setOpaque(false);
        for (int i = 0; i < buttons.length; i++) {
            row.add(buttons[i]);
        }
        return row;
    }

    // A table model where the user cannot type into the cells
    private DefaultTableModel createReadOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Wraps a styled table in a scroll pane that is tall enough to show
    // VISIBLE_ROWS rows. With fewer rows, the rest is empty space; with more,
    // a scroll bar appears.
    private JScrollPane createTableScroll(DefaultTableModel model) {
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        table.setPreferredScrollableViewportSize(
                new Dimension(800, table.getRowHeight() * VISIBLE_ROWS));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scroll.getViewport().setBackground(UITheme.CARD);
        return scroll;
    }

    // A page-coloured panel with padding, used as the base of every tab
    private JPanel createTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        return panel;
    }

    // Adds one "label + text field" row to a form (used by the pop-up dialogs)
    private void addField(JPanel form, String labelText, JTextField field) {
        form.add(UITheme.label(labelText));
        UITheme.styleField(field);
        form.add(field);
    }

    // =====================================================
    //                     BOOKS TAB
    // =====================================================

    private JPanel createBooksPanel() {
        JPanel panel = createTabPanel();

        // Form at the top
        JButton addButton = UITheme.primaryButton("Add Book");
        JButton addSeveralButton = UITheme.secondaryButton("Add Several...");

        JPanel form = buildForm("Add a Book",
                new String[]{"Book ID:", "Title:", "Author:", "Category:"},
                new JTextField[]{bookIdField, bookTitleField, bookAuthorField, bookCategoryField},
                createButtonRow(addSeveralButton, addButton), 2);

        // Table in the middle (always has room for 10 rows)
        String[] columns = {"ID", "Title", "Author", "Category", "Status"};
        bookTableModel = createReadOnlyModel(columns);
        JScrollPane scrollPane = createTableScroll(bookTableModel);

        // Search bar at the bottom
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        searchPanel.setBackground(UITheme.CARD);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        searchTypeBox.setFont(UITheme.BODY);
        UITheme.styleField(searchField);
        JButton searchButton = UITheme.primaryButton("Search");
        JButton showAllButton = UITheme.secondaryButton("Show All");
        searchPanel.add(UITheme.label("Search by:"));
        searchPanel.add(searchTypeBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        // Put the three parts together
        panel.add(form, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
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

            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));
            inputPanel.setPreferredSize(new Dimension(340, 170));
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

        updateCounts();
    }

    // =====================================================
    //                    MEMBERS TAB
    // =====================================================

    private JPanel createMembersPanel() {
        JPanel panel = createTabPanel();

        // Form at the top
        JButton registerButton = UITheme.primaryButton("Register Member");
        JButton registerSeveralButton = UITheme.secondaryButton("Register Several...");

        JPanel form = buildForm("Register a Member",
                new String[]{"Member ID:", "Name:", "Course:", "Phone:"},
                new JTextField[]{memberIdField, memberNameField, memberCourseField, memberPhoneField},
                createButtonRow(registerSeveralButton, registerButton), 2);

        // Table in the middle
        String[] columns = {"Member ID", "Name", "Course", "Phone", "Books Borrowed"};
        memberTableModel = createReadOnlyModel(columns);
        JScrollPane scrollPane = createTableScroll(memberTableModel);

        panel.add(form, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

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

            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));
            inputPanel.setPreferredSize(new Dimension(340, 170));
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

        updateCounts();
    }

    // =====================================================
    //                 BORROW / RETURN TAB
    // =====================================================

    private JPanel createBorrowPanel() {
        JPanel panel = createTabPanel();

        // Form at the top
        JButton borrowButton = UITheme.primaryButton("Borrow Book");
        JButton returnButton = UITheme.secondaryButton("Return Book");
        JButton borrowSeveralButton = UITheme.secondaryButton("Borrow Several...");
        JButton returnSeveralButton = UITheme.secondaryButton("Return Several...");

        JPanel form = buildForm("Borrow or Return a Book",
                new String[]{"Member ID:", "Book ID:"},
                new JTextField[]{borrowMemberIdField, borrowBookIdField},
                createButtonRow(borrowButton,returnButton, borrowSeveralButton, returnSeveralButton ), 2);

        // Activity log in the middle: a text area that shows every borrow/return
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(UITheme.MONO);
        logArea.setForeground(UITheme.TEXT);
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.getViewport().setBackground(UITheme.CARD);
        logScroll.setBorder(UITheme.cardBorder("Activity Log"));

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

            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 8, 8));
            inputPanel.setPreferredSize(new Dimension(340, 90));
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

            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 8, 8));
            inputPanel.setPreferredSize(new Dimension(340, 90));
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
}