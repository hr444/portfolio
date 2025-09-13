package LibraryCatalog;

import javax.swing.*;

import LibraryCatalog.Database.BorrowInfo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class LibraryGUI {
    private static Database database = new Database(); 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowMainMenu());
    }

    private static void createAndShowMainMenu() {
       
        JFrame frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200); 
        frame.setLayout(new GridLayout(3, 1, 10, 10)); 

       
        JLabel titleLabel = new JLabel("Welcome to Library Catalog", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        frame.add(titleLabel);

       
        JButton createUserButton = new JButton("Create User");
        JButton loginButton = new JButton("Log In");

       
        Dimension buttonSize = new Dimension(120, 30); 
        createUserButton.setPreferredSize(buttonSize);
        loginButton.setPreferredSize(buttonSize);

        
        frame.add(createUserButton);
        frame.add(loginButton);

       
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAndShowUserCreationForm();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAndShowLoginForm();
            }
        });

       
        frame.setVisible(true);
    }


    private static void createAndShowUserCreationForm() {
        
        JFrame frame = new JFrame("Create User");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel userIDLabel = new JLabel("Enter User ID:");
        JTextField userIDField = new JTextField();
        frame.add(userIDLabel);
        frame.add(userIDField);

        JLabel nameLabel = new JLabel("Enter Name:");
        JTextField nameField = new JTextField();
        frame.add(nameLabel);
        frame.add(nameField);

        JLabel emailLabel = new JLabel("Enter Email:");
        JTextField emailField = new JTextField();
        frame.add(emailLabel);
        frame.add(emailField);

        JLabel passwordLabel = new JLabel("Enter Password:");
        JPasswordField passwordField = new JPasswordField();
        frame.add(passwordLabel);
        frame.add(passwordField);

        JLabel userTypeLabel = new JLabel("User Type:");
        JComboBox<String> userTypeComboBox = new JComboBox<>(new String[]{"Librarian", "Student"});
        frame.add(userTypeLabel);
        frame.add(userTypeComboBox);

        JButton submitButton = new JButton("Create User");
        submitButton.setPreferredSize(new Dimension(120, 30));

        frame.add(new JLabel()); 
        frame.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = userIDField.getText();
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String userType = (String) userTypeComboBox.getSelectedItem();

                User user;
                if (userType.equals("Librarian")) {
                    user = new Librarian(userID, name, email, password);
                    saveUserToFile(user, "librarian_info.txt");
                } else {
                    user = new Student(userID, name, email, password);
                    saveUserToFile(user, "student_info.txt");
                }
                database.addUser(user);

                JOptionPane.showMessageDialog(frame, "User created successfully!");

                frame.dispose();
                
                createAndShowMainMenu();
            }
        });

        frame.setVisible(true);
    }

    private static void createAndShowLoginForm() {
       
        JFrame frame = new JFrame("Log In");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel userIDLabel = new JLabel("User ID:");
        JTextField userIDField = new JTextField();
        frame.add(userIDLabel);
        frame.add(userIDField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        frame.add(passwordLabel);
        frame.add(passwordField);

        JButton loginButton = new JButton("Log In");

      
        loginButton.setPreferredSize(new Dimension(120, 30));

        frame.add(new JLabel()); 
        frame.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = userIDField.getText();
                String password = new String(passwordField.getPassword());

                User user = authenticateUser(userID, password);

                if (user != null) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    if (user instanceof Librarian) {
                        showLibrarianMenu(user);
                    } else {
                        showStudentMenu(user);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid User ID or Password.");
                }
            }
        });

        frame.setVisible(true);
    }

    private static void saveUserToFile(User user, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println("Name: " + user.getName());
            writer.println("Email: " + user.getEmail());
            writer.println("ID: " + user.getUserID());
            writer.println("Password: " + user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static User authenticateUser(String userID, String password) {
        User user = authenticateFromFile(userID, password, "librarian_info.txt", "Librarian");
        if (user == null) {
            user = authenticateFromFile(userID, password, "student_info.txt", "Student");
        }
        return user;
    }

    private static User authenticateFromFile(String ID, String password, String filename, String userType) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String name = null, email = null, fileID = null, filePassword = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Name: ")) {
                    name = line.substring(6).trim();
                } else if (line.startsWith("Email: ")) {
                    email = line.substring(7).trim();
                } else if (line.startsWith("ID: ")) {
                    fileID = line.substring(4).trim();
                } else if (line.startsWith("Password: ")) {
                    filePassword = line.substring(10).trim();
                }
                
                if (fileID != null && filePassword != null) {
                    if (fileID.equals(ID) && filePassword.equals(password)) {
                        if (userType.equals("Librarian")) {
                            return new Librarian(fileID, name, email, filePassword);
                        } else {
                            return new Student(fileID, name, email, filePassword);
                        }
                    }
                    
                   
                    name = email = fileID = filePassword = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


	protected static String userID;
	protected static String journalID;
	protected static String bookID;
	protected static Object itemID;

    

    private static void showStudentMenu(User user) {
        JFrame studentFrame = new JFrame("Student Menu");
        studentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        studentFrame.setSize(500, 300);
        studentFrame.setLayout(new GridLayout(4, 1, 10, 10));
        
        JButton viewBooksButton = new JButton("View Books");
        JButton searchBooksButton = new JButton("Search Books");
        JButton borrowBookButton = new JButton("Borrow Book");
        JButton returnBookButton = new JButton("Return Book");
        JButton viewJournalsButton = new JButton("View Journals");
        JButton searchJournalsButton = new JButton("Search Journals");
        JButton borrowJournalButton = new JButton("Borrow Journal");
        JButton returnJournalButton = new JButton("Return Journal");
        JButton calculateFineButton = new JButton("Calculate Fine");
        JButton logoutButton = new JButton("Log Out");

      
        Dimension buttonSize = new Dimension(120, 30);
        viewBooksButton.setPreferredSize(buttonSize);
        searchBooksButton.setPreferredSize(buttonSize);
        borrowBookButton.setPreferredSize(buttonSize);
        returnBookButton.setPreferredSize(buttonSize);
        viewJournalsButton.setPreferredSize(buttonSize);
        searchJournalsButton.setPreferredSize(buttonSize);
        borrowJournalButton.setPreferredSize(buttonSize);
        returnJournalButton.setPreferredSize(buttonSize);
        calculateFineButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        studentFrame.add(viewBooksButton);
        studentFrame.add(searchBooksButton);
        studentFrame.add(borrowBookButton);
        studentFrame.add(returnBookButton);
        studentFrame.add(viewJournalsButton);
        studentFrame.add(searchJournalsButton);
        studentFrame.add(borrowJournalButton);
        studentFrame.add(returnJournalButton);
        studentFrame.add(calculateFineButton);
        studentFrame.add(logoutButton);
        
        viewBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String filePath = "books.txt";
                StringBuilder allBooks = new StringBuilder(); 

                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        
                        String[] bookDetails = line.split(",");  
                        if (bookDetails.length == 6) {
                        	if (bookDetails[0].equals("ItemID") || bookDetails[1].equals("Title")) {
                                continue;  
                            }
                            String bookInfo = "Book ID : " + bookDetails[0] + "\n"
                                    + "Title: " + bookDetails[1] + "\n"
                                    + "Author: " + bookDetails[2] + "\n"
                                    + "Publication Date:" +bookDetails[3] + "\n"
                                    + "Genre: " + bookDetails[4] + "\n"
                                    + "ISBN: " + bookDetails[5] + "\n\n";
                            allBooks.append(bookInfo);  
                        }
                    }
                    if (allBooks.length() > 0) {
                        JOptionPane.showMessageDialog(studentFrame, allBooks.toString(), "Books List", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                   
                        JOptionPane.showMessageDialog(studentFrame, "No books found in the library.", "No Books", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(studentFrame, "Error reading from file: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchQuery = JOptionPane.showInputDialog("Enter book title or author to search:");
                
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                   database.loadBooksFromFile();

                    List<Book> result = database.searchBooks(searchQuery);
                    
                    if (result.isEmpty()) {
                        JOptionPane.showMessageDialog(studentFrame, "No books found for: " + searchQuery);
                    } else {
                        StringBuilder resultMessage = new StringBuilder("Search Results:\n");
                        for (Book book : result) {
                            resultMessage.append("Title: ").append(book.getTitle())
                                         .append("\nAuthor: ").append(book.getAuthor())
                                         .append("\nPublication Date: ").append(book.getPublicationDate())
                                         .append("\nISBN: ").append(book.getISBN())
                                         .append("\nGenre: ").append(book.getGenre());
                                         
                        }
                        JOptionPane.showMessageDialog(studentFrame, resultMessage.toString());
                    }
                } else {
                    JOptionPane.showMessageDialog(studentFrame, "Search query cannot be empty.");
                }
            }
        });

        borrowBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = JOptionPane.showInputDialog("Enter your user ID:");
                String bookID = JOptionPane.showInputDialog("Enter book ID to borrow:");

                if (userID == null || bookID == null || userID.isEmpty() || bookID.isEmpty()) {
                    JOptionPane.showMessageDialog(studentFrame, "User ID or Book ID cannot be empty.");
                    return;
                }

                boolean success = database.borrowBook(userID, bookID); 

                if (success) {
                    JOptionPane.showMessageDialog(studentFrame, "Book borrowed successfully!");
                } else {
                    JOptionPane.showMessageDialog(studentFrame, "Failed to borrow book. Either the book is already borrowed or an error occurred.");
                }
            }
        });

        returnBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 String userID = JOptionPane.showInputDialog("Enter your user ID:");
                String bookID = JOptionPane.showInputDialog("Enter book ID to return:");
                
                if (bookID == null || bookID.isEmpty()) {
                    JOptionPane.showMessageDialog(studentFrame, "Book ID cannot be empty.");
                    return;
                }

                boolean success = database.returnBook(userID, bookID); 
                if (success) {
                    JOptionPane.showMessageDialog(studentFrame, "Book returned successfully!");
                } else {
                	BorrowInfo borrowInfo = database.getBorrowInfo(bookID); 
                    if (borrowInfo != null && !borrowInfo.getUserID().equals(userID)) {
                        JOptionPane.showMessageDialog(studentFrame, "Failed to return book. It is borrowed by another user.");
                    } else {
                        JOptionPane.showMessageDialog(studentFrame, "Failed to return book. It might not be borrowed.");
                    }
                }
            }
        });

        viewJournalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "journals.txt";
                StringBuilder allJournals = new StringBuilder(); 

                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    
                    while ((line = br.readLine()) != null) {
                        String[] journalDetails = line.split(",");  
                        if (journalDetails.length == 7) {
                            if (journalDetails[0].equals("ItemID") || journalDetails[1].equals("Title")) {
                                continue;  
                            }

                            String journalInfo = "Journal ID: " + journalDetails[0] + "\n"
                                               + "Title: " + journalDetails[1] + "\n"
                                               + "Author: " + journalDetails[2] + "\n"
                                               + "Publication Date: " + journalDetails[3] + "\n"
                                               + "Volume: " + journalDetails[4] + "\n"
                                               + "Issue: " + journalDetails[5] + "\n"
                                               + "ISSN: " + journalDetails[6] + "\n\n";
                            allJournals.append(journalInfo);  
                        }
                    }

                    if (allJournals.length() > 0) {
                        JOptionPane.showMessageDialog(studentFrame, allJournals.toString(), "Journals List", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(studentFrame, "No journals found in the library.", "No Journals", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(studentFrame, "Error reading from file: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchJournalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchQuery = JOptionPane.showInputDialog("Enter journal title or author to search:");

                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                database.loadJournalsFromFile(); 

                
                List<Journal> result = database.searchJournals(searchQuery);

               
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(studentFrame, "No journals found for query: " + searchQuery);
                } else {
                    StringBuilder resultMessage = new StringBuilder("Search Results:\n");
                    for (Journal journal : result) {
                        resultMessage.append("Title: ").append(journal.getTitle()).append("\n")
                                      .append("Author: ").append(journal.getAuthor()).append("\n")
                                      .append("Publication Date: ").append(journal.getPublicationDate()).append("\n")
                                      .append("Volume: ").append(journal.getVolume()).append("\n")
                                      .append("Issue: ").append(journal.getIssue()).append("\n")
                                      .append("ISSN: ").append(journal.getISSN()).append("\n\n");
                    }
                    JOptionPane.showMessageDialog(studentFrame, resultMessage.toString());
                }
            } else {
                JOptionPane.showMessageDialog(studentFrame, "Search query cannot be empty.");
            }
        }
    });

        borrowJournalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = JOptionPane.showInputDialog("Enter your user ID:");
                String journalID = JOptionPane.showInputDialog("Enter journal ID to borrow:");

                if (userID == null || journalID == null || userID.isEmpty() || journalID.isEmpty()) {
                    JOptionPane.showMessageDialog(studentFrame, "User ID or Journal ID cannot be empty.");
                    return;
                }

                boolean success = database.borrowJournal(userID, journalID); 

                if (success) {
                    JOptionPane.showMessageDialog(studentFrame, "Journal borrowed successfully!");
                } else {
                    JOptionPane.showMessageDialog(studentFrame, "Failed to borrow Journal. Either the Journal is already borrowed or an error occurred.");
                }
            }
        });

        returnJournalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 String userID = JOptionPane.showInputDialog("Enter your user ID:");
                String journalID = JOptionPane.showInputDialog("Enter journal ID to return:");
                
                if (journalID == null || journalID.isEmpty()) {
                    JOptionPane.showMessageDialog(studentFrame, "Journal ID cannot be empty.");
                    return;
                }

                boolean success = database.returnBook(userID, journalID); 
                if (success) {
                    JOptionPane.showMessageDialog(studentFrame, "Journal returned successfully!");
                } else {
                	BorrowInfo borrowInfo = database.getBorrowInfo(journalID); 
                    if (borrowInfo != null && !borrowInfo.getUserID().equals(userID)) {
                        JOptionPane.showMessageDialog(studentFrame, "Failed to return journal. It is borrowed by another user.");
                    } else {
                        JOptionPane.showMessageDialog(studentFrame, "Failed to return journal. It might not be borrowed.");
                    }
                }
            }
        });
        calculateFineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = JOptionPane.showInputDialog("Enter Your User ID:");

                if (userID != null && !userID.trim().isEmpty()) {
                    double fine = database.calculateFine(userID);

                    JOptionPane.showMessageDialog(studentFrame, "Total Fine for User with ID: " + userID + ": RM " + fine);
                } else {
                    JOptionPane.showMessageDialog(studentFrame, "Please enter a valid User ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                studentFrame.dispose(); 
                
                createAndShowMainMenu(); 
                
                JOptionPane.showMessageDialog(null, "Logged out successfully!");
            }
        });

        studentFrame.setVisible(true);
    }


    private static void showLibrarianMenu(User user) {
        JFrame librarianFrame = new JFrame("Librarian Menu");
        librarianFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        librarianFrame.setSize(500, 300);
        librarianFrame.setLayout(new GridLayout(4, 1, 10, 10));
        
        JButton addBookButton = new JButton("Add Book");
        JButton viewBooksButton = new JButton("View Books");
        JButton removeBookButton = new JButton("Remove Books");
        JButton searchBooksButton = new JButton("Search Books");
        JButton addJournalButton = new JButton("Add Journal");
        JButton viewJournalsButton = new JButton("View Journals");
        JButton removeJournalsButton = new JButton("Remove Journals");
        JButton searchJournalsButton = new JButton("Search Journals");
        JButton checkstudentButton = new JButton("Check Student Penalty");
        JButton logoutButton = new JButton("Log Out");

       
        Dimension buttonSize = new Dimension(120, 30);
        addBookButton.setPreferredSize(buttonSize);
        viewBooksButton.setPreferredSize(buttonSize);
        removeBookButton.setPreferredSize(buttonSize);
        searchBooksButton.setPreferredSize(buttonSize);
        addJournalButton.setPreferredSize(buttonSize);
        viewJournalsButton.setPreferredSize(buttonSize);
        removeJournalsButton.setPreferredSize(buttonSize);
        searchJournalsButton.setPreferredSize(buttonSize);
        checkstudentButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        librarianFrame.add(addBookButton);
        librarianFrame.add(viewBooksButton);
        librarianFrame.add(removeBookButton);
        librarianFrame.add(searchBooksButton);
        librarianFrame.add(addJournalButton);
        librarianFrame.add(viewJournalsButton);
        librarianFrame.add(removeJournalsButton);
        librarianFrame.add(searchJournalsButton);
        librarianFrame.add(checkstudentButton);
        librarianFrame.add(logoutButton);
        
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookID = JOptionPane.showInputDialog("Enter Book ID:");
                String title = JOptionPane.showInputDialog("Enter Book Title:");
                String author = JOptionPane.showInputDialog("Enter Author Name:");
                String publicationDate = JOptionPane.showInputDialog("Enter Publication Date (YYYY-MM-DD):");
                String genre = JOptionPane.showInputDialog("Enter Genre:");
                String ISBN = JOptionPane.showInputDialog("Enter ISBN:");

                if (bookID != null && title != null && author != null && publicationDate != null && genre != null && ISBN != null) {
                    if (database.isBookIDExists(bookID)) { 
                        JOptionPane.showMessageDialog(librarianFrame, "The Book ID is already in use. Please use a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                       
                        Book book = new Book(bookID, title, author, publicationDate, genre, ISBN);
                        database.addBook(book); 
                        JOptionPane.showMessageDialog(librarianFrame, "Book added successfully!");
                    }
                } else {
                  
                    JOptionPane.showMessageDialog(librarianFrame, "Please provide all book details.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        
        removeBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookID = JOptionPane.showInputDialog(librarianFrame, "Enter Book ID to remove:");

                if (bookID != null && !bookID.trim().isEmpty()) {
                    boolean isRemoved = database.removeBook(bookID);

                    if (isRemoved) {
                        JOptionPane.showMessageDialog(librarianFrame, "Book with ID " + bookID + " was removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(librarianFrame, "Book with ID " + bookID + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    
                    JOptionPane.showMessageDialog(librarianFrame, "Please enter a valid Book ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String filePath = "books.txt";
                StringBuilder allBooks = new StringBuilder(); 

                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        
                        String[] bookDetails = line.split(",");  
                        if (bookDetails.length == 6) {
                        	if (bookDetails[0].equals("ItemID") || bookDetails[1].equals("Title")) {
                                continue;  
                            }
                            String bookInfo = "Book ID: " + bookDetails[0] + "\n"
                                             + "Title: " + bookDetails[1] + "\n"
                                             + "Author: " + bookDetails[2] + "\n"
                                             + "Publication Date:" +bookDetails[3] + "\n"
                                             + "ISBN: " + bookDetails[5] + "\n"
                                             + "Genre: " + bookDetails[4] + "\n\n";
                            allBooks.append(bookInfo);  
                        }
                    }
                    if (allBooks.length() > 0) {
                        JOptionPane.showMessageDialog(librarianFrame, allBooks.toString(), "Books List", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                   
                        JOptionPane.showMessageDialog(librarianFrame, "No books found in the library.", "No Books", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(librarianFrame, "Error reading from file: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        searchBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchQuery = JOptionPane.showInputDialog("Enter book title or author to search:");
                
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                   database.loadBooksFromFile();

                    List<Book> result = database.searchBooks(searchQuery);
                    
                    if (result.isEmpty()) {
                        JOptionPane.showMessageDialog(librarianFrame, "No books found for: " + searchQuery);
                    } else {
                        StringBuilder resultMessage = new StringBuilder("Search Results:\n");
                        for (Book book : result) {
                            resultMessage.append("Title: ").append(book.getTitle())
                                         .append("\nAuthor: ").append(book.getAuthor())
                                         .append("\nPublication Date: ").append(book.getPublicationDate())
                                         .append("\nISBN: ").append(book.getISBN())
                                         .append("\nGenre: ").append(book.getGenre());
                                         
                        }
                        JOptionPane.showMessageDialog(librarianFrame, resultMessage.toString());
                    }
                } else {
                    JOptionPane.showMessageDialog(librarianFrame, "Search query cannot be empty.");
                }
            }
        });

        
        removeJournalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String journalID = JOptionPane.showInputDialog(librarianFrame, "Enter Journal ID to remove:");

                if (journalID != null && !journalID.trim().isEmpty()) {
                    boolean isRemoved = database.removeJournal(journalID);

                    if (isRemoved) {
                        JOptionPane.showMessageDialog(librarianFrame, "Journal with ID " + journalID + " was removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(librarianFrame, "Journal with ID " + journalID + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    
                    JOptionPane.showMessageDialog(librarianFrame, "Please enter a valid Journal ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
       
        addJournalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String journalID = JOptionPane.showInputDialog("Enter Journal ID:");
                String title = JOptionPane.showInputDialog("Enter Journal Title:");
                String author = JOptionPane.showInputDialog("Enter Author Name:");
                String publicationDate = JOptionPane.showInputDialog("Enter Publication Date (YYYY-MM-DD):");
                String volume = JOptionPane.showInputDialog("Enter Journal Volume:");
                String issueNumber = JOptionPane.showInputDialog("Enter Issue Number:");
                String ISSN = JOptionPane.showInputDialog("Enter ISSN:");

               
                if (journalID != null && title != null && author != null && publicationDate != null && volume != null && issueNumber != null && ISSN != null) {
                if (database.isJournalIDExists(journalID)) { 
                        JOptionPane.showMessageDialog(librarianFrame, "The Journal ID is already in use. Please use a unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                       
                        Journal journal = new Journal(journalID, title, author, publicationDate, volume, issueNumber, ISSN);
                        database.addJournal(journal); 
                        JOptionPane.showMessageDialog(librarianFrame, "Journal added successfully!");
                    }
                } else {
                  
                    JOptionPane.showMessageDialog(librarianFrame, "Please provide all Journal details.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });    
                   
        
        viewJournalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "journals.txt";
                StringBuilder allJournals = new StringBuilder(); 

                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    
                    while ((line = br.readLine()) != null) {
                        String[] journalDetails = line.split(",");  
                        if (journalDetails.length == 7) {
                            if (journalDetails[0].equals("ItemID") || journalDetails[1].equals("Title")) {
                                continue;  
                            }

                            String journalInfo = "Journal ID: " + journalDetails[0] + "\n"
                                               + "Title: " + journalDetails[1] + "\n"
                                               + "Author: " + journalDetails[2] + "\n"
                                               + "Publication Date: " + journalDetails[3] + "\n"
                                               + "Volume: " + journalDetails[4] + "\n"
                                               + "Issue: " + journalDetails[5] + "\n"
                                               + "ISSN: " + journalDetails[6] + "\n\n";
                            allJournals.append(journalInfo);  
                        }
                    }

                    if (allJournals.length() > 0) {
                        JOptionPane.showMessageDialog(librarianFrame, allJournals.toString(), "Journals List", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(librarianFrame, "No journals found in the library.", "No Journals", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(librarianFrame, "Error reading from file: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        searchJournalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchQuery = JOptionPane.showInputDialog("Enter journal title or author to search:");

                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                database.loadJournalsFromFile(); 

                
                List<Journal> result = database.searchJournals(searchQuery);

               
                if (result.isEmpty()) {
                    JOptionPane.showMessageDialog(librarianFrame, "No journals found for query: " + searchQuery);
                } else {
                    StringBuilder resultMessage = new StringBuilder("Search Results:\n");
                    for (Journal journal : result) {
                        resultMessage.append("Title: ").append(journal.getTitle()).append("\n")
                                      .append("Author: ").append(journal.getAuthor()).append("\n")
                                      .append("Publication Date: ").append(journal.getPublicationDate()).append("\n")
                                      .append("Volume: ").append(journal.getVolume()).append("\n")
                                      .append("Issue: ").append(journal.getIssue()).append("\n")
                                      .append("ISSN: ").append(journal.getISSN()).append("\n\n");
                    }
                    JOptionPane.showMessageDialog(librarianFrame, resultMessage.toString());
                }
            } else {
                JOptionPane.showMessageDialog(librarianFrame, "Search query cannot be empty.");
            }
        }
    });

        checkstudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userID = JOptionPane.showInputDialog("Enter Student ID to check penalty:");

                if (userID != null && !userID.trim().isEmpty()) {
                    String filePath = "student_info.txt";
                    boolean studentExists = false;

                    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                        String line;
                        String currentID = null;

                        System.out.println("Reading student_info.txt file...");

                        while ((line = br.readLine()) != null) {
                            line = line.trim();  

                            if (line.startsWith("ID:")) {
                                currentID = line.substring(4).trim(); 
                                
                                System.out.println("Found ID: " + currentID);

                                if (currentID.equals(userID)) {
                                    studentExists = true;
                                    break;  
                                }
                            }
                        }

                        if (studentExists) {
                            
                            System.out.println("Student ID found. Calculating penalty...");

                            double penalty = database.calculateFine(userID);

                            System.out.println("Penalty calculated: RM " + penalty);

                            if (penalty > 0) {
                                JOptionPane.showMessageDialog(librarianFrame, "Total penalty for student with ID: " + userID + " is RM " + penalty);
                            } else {
                                JOptionPane.showMessageDialog(librarianFrame, "No penalty for student with ID: " + userID);
                            }
                        } else {
                            JOptionPane.showMessageDialog(librarianFrame, "Student ID " + userID + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (IOException ex) {
                        
                        System.out.println("Error reading the file: " + ex.getMessage());
                        JOptionPane.showMessageDialog(librarianFrame, "Error reading from file: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(librarianFrame, "Please enter a valid Student ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                librarianFrame.dispose();
                
                createAndShowMainMenu(); 
                
                JOptionPane.showMessageDialog(null, "Logged out successfully!"); 
                
               
            }
        });

  

        librarianFrame.setVisible(true);
    }
}
