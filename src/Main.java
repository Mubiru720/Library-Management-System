// ======================================================
// CSC 1102: Structured and Object-Oriented Programming
// Practical Exam Project: Library Management System
//

// Group Members:
//   1. MUBIRU ELIJAH HERBERT  25/U/30801/EVE
//   2. KYAMANYWA JOHN BOSCO   25/U/03428/EVE
//   3. MARY MICHEAL ELIABA    25/X/03444/EVE
// ======================================================

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
}
