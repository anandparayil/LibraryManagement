import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LibraryManagementSystem {
    private JFrame frame;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField titleField, authorField, isbnField, yearField, searchField;
    private JComboBox<String> genreComboBox;
    private JCheckBox availabilityCheckBox;
    private int selectedRow = -1;

    public LibraryManagementSystem() {
        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        // About Menu Item
        JMenuItem aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);
        aboutMenuItem.addActionListener(e -> showAboutDialog());

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        // Table Setup
        String[] columnNames = {"Title", "Author", "ISBN", "Publication Year", "Genre", "Available"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        titleField = new JTextField();
        authorField = new JTextField();
        isbnField = new JTextField();
        yearField = new JTextField();
        String[] genres = {"Fiction", "Non-Fiction", "Science", "History", "Fantasy", "Biography"};
        genreComboBox = new JComboBox<>(genres);
        availabilityCheckBox = new JCheckBox("Available");

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("ISBN:"));
        inputPanel.add(isbnField);
        inputPanel.add(new JLabel("Publication Year:"));
        inputPanel.add(yearField);
        inputPanel.add(new JLabel("Genre:"));
        inputPanel.add(genreComboBox);
        inputPanel.add(availabilityCheckBox);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search Book");
        JButton clearSearchButton = new JButton("Clear Search");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);
        frame.add(searchPanel, BorderLayout.SOUTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton addBookButton = new JButton("Add Book");
        JButton updateBookButton = new JButton("Update Book");
        JButton deleteBookButton = new JButton("Delete Book");

        buttonPanel.add(addBookButton);
        buttonPanel.add(updateBookButton);
        buttonPanel.add(deleteBookButton);
        frame.add(buttonPanel, BorderLayout.EAST);

        // Action Listeners
        addBookButton.addActionListener(e -> addBook());
        updateBookButton.addActionListener(e -> updateBook());
        deleteBookButton.addActionListener(e -> deleteBook());
        searchButton.addActionListener(e -> searchBook());
        clearSearchButton.addActionListener(e -> clearSearch());

        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedRow = bookTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadBookDetails(selectedRow);
                }
            }
        });

        frame.setVisible(true);
    }

    // About Dialog
    private void showAboutDialog() {
        String aboutMessage = """
                Library Management System
                Version: 1.0
                Author: Anand Parayil Sunil Kumar""";
        JOptionPane.showMessageDialog(frame, aboutMessage, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    // Add Book
    private void addBook() {
        try {
            String title = titleField.getText();
            String author = authorField.getText();
            String isbn = isbnField.getText();
            String year = yearField.getText();
            String genre = (String) genreComboBox.getSelectedItem();
            boolean available = availabilityCheckBox.isSelected();

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || year.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }
            if (!isbn.matches("\\d{10}|\\d{13}")) {
                JOptionPane.showMessageDialog(frame, "ISBN must be 10 or 13 digits.");
                return;
            }
            if (!year.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(frame, "Publication year must be a valid 4-digit number.");
                return;
            }

            tableModel.addRow(new Object[]{title, author, isbn, year, genre, available});
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error adding book: " + e.getMessage());
        }
    }

    // Update Book
    private void updateBook() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a book to update.");
            return;
        }

        try {
            String title = titleField.getText();
            String author = authorField.getText();
            String isbn = isbnField.getText();
            String year = yearField.getText();
            String genre = (String) genreComboBox.getSelectedItem();
            boolean available = availabilityCheckBox.isSelected();

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || year.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }

            tableModel.setValueAt(title, selectedRow, 0);
            tableModel.setValueAt(author, selectedRow, 1);
            tableModel.setValueAt(isbn, selectedRow, 2);
            tableModel.setValueAt(year, selectedRow, 3);
            tableModel.setValueAt(genre, selectedRow, 4);
            tableModel.setValueAt(available, selectedRow, 5);

            clearFields();
            selectedRow = -1;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error updating book: " + e.getMessage());
        }
    }

    // Delete Book
    private void deleteBook() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a book to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selectedRow);
            clearFields();
            selectedRow = -1;
        }
    }

    // Search Book
    private void searchBook() {
        String searchQuery = searchField.getText().toUpperCase();
        for (int row = 0; row < bookTable.getRowCount(); row++) {
            String title = bookTable.getValueAt(row, 0).toString().toUpperCase();
            if (title.contains(searchQuery)) {
                bookTable.setRowSelectionInterval(row, row);
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "No matching book found.");
    }

    private void clearSearch() {
        searchField.setText("");
        bookTable.clearSelection();
    }

    private void loadBookDetails(int row) {
        titleField.setText((String) tableModel.getValueAt(row, 0));
        authorField.setText((String) tableModel.getValueAt(row, 1));
        isbnField.setText((String) tableModel.getValueAt(row, 2));
        yearField.setText((String) tableModel.getValueAt(row, 3));
        genreComboBox.setSelectedItem(tableModel.getValueAt(row, 4));
        availabilityCheckBox.setSelected((Boolean) tableModel.getValueAt(row, 5));
    }

    private void clearFields() {
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        yearField.setText("");
        genreComboBox.setSelectedIndex(0);
        availabilityCheckBox.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystem::new);
    }
}
