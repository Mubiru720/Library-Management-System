import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class LibraryGUI extends JFrame {

    // The one Library object used by the whole GUI
    private Library library = new Library();

    // ---------- Components of the Books tab ----------
    private JTextField bookIdField = new JTextField();
    private JTextField bookTitleField = new JTextField();
    private JTextField bookAuthorField = new JTextField();
    private JTextField bookCategoryField = new JTextField();
    private DefaultTableModel bookTableModel;

    // ---------- Components of the Members tab ----------
    private JTextField memberIdField = new JTextField();
    private JTextField memberNameField = new JTextField();
    private JTextField memberCourseField = new JTextField();
    private JTextField memberPhoneField = new JTextField();
    private DefaultTableModel memberTableModel;

    // ---------- Constructor that builds the window ----------
    public LibraryGUI() {
        setTitle("Library Management System");
        setSize(750, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        add(tabs);
    }

    //----------BOOKS TAB-----------
    

    private JPanel createBooksPanel() {
        // Form at the top
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        addField(form, "Book ID:", bookIdField);
        addField(form, "Title:", bookTitleField);
        addField(form, "Author:", bookAuthorField);
        addField(form, "Category:", bookCategoryField);

        JButton addButton = new JButton("Add Book");
        form.add(new JLabel(""));
        form.add(addButton);

        // Table in the middle
        String[] columns = {"ID", "Title", "Author", "Category", "Status"};
        bookTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(bookTableModel);
        table.setDefaultEditor(Object.class, null); 

        // Put them together
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        addButton.addActionListener(e -> handleAddBook());
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
               
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID must be a whole number.");
        }
    }


    
    //----------MEMBERS TAB-----------------
    private JPanel createMembersPanel() {
        // Form at the top
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        addField(form, "Member ID:", memberIdField);
        addField(form, "Name:", memberNameField);
        addField(form, "Course:", memberCourseField);
        addField(form, "Phone:", memberPhoneField);

        JButton registerButton = new JButton("Register Member");
        form.add(new JLabel(""));
        form.add(registerButton);

        // Table in the middle
        String[] columns = {"Member ID", "Name", "Course", "Phone"};
        memberTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(memberTableModel);
        table.setDefaultEditor(Object.class, null);

        // Put them together
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        registerButton.addActionListener(e -> handleRegisterMember());
        return panel;
    }

    private void handleRegisterMember() {
        String message = library.registerMember(memberIdField.getText(), memberNameField.getText(),
                memberCourseField.getText(), memberPhoneField.getText());
        JOptionPane.showMessageDialog(this, message);

        if (message.equals("Member registered successfully.")) {
            memberIdField.setText("");
            memberNameField.setText("");
            memberCourseField.setText("");
            memberPhoneField.setText("");
            
        }
    }

    // Adds one "label + text field" row to a form
    private void addField(JPanel form, String labelText, JTextField field) {
        form.add(new JLabel(labelText));
        form.add(field);
    }
}