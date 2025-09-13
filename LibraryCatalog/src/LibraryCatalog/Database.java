package LibraryCatalog;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Database {
    private List<User> users;
    private List<Book> books;
    private List<Journal> journals;
    private Map<String, BorrowInfo> borrowedBooks; 

    private static final String STUDENTS_FILE_PATH = "student_info.txt";
    private static final String LIBRARIANS_FILE_PATH = "librarian_info.txt";
    private static final String BOOKS_FILE_PATH = "books.txt";
    private static final String BOOKS_FILE_HEADER = "ItemID,Title,Author,PublicationDate,ISBN,Genre";
    private static final String JOURNALS_FILE_PATH = "journals.txt";
    private static final String JOURNALS_FILE_HEADER = "ItemID,Title,Editor,PublicationDate,Volume,IssueNumber,ISSN";
    private static final int BORROW_PERIOD_DAYS = 5; 
    private static final double DAILY_FINE = 1.0; 

    public Database() {
        users = new ArrayList<>();
        books = new ArrayList<>();
        journals = new ArrayList<>();
        borrowedBooks = new HashMap<>();
        loadUsersFromFiles(); 
        loadBooksFromFile(); 
        loadJournalsFromFile();
    }

    public void addUser(User user) {
        if (isUserIDExist(user.getUserID())) {
            System.out.println("Error: User with ID " + user.getUserID() + " already exists.");
            return;
        }
        users.add(user);
        saveUserToFile(user);
    }

    private boolean isUserIDExist(String userID) {
        for (User existingUser : users) {
            if (existingUser.getUserID().equals(userID)) {
                return true;
            }
        }
        return false;
    }

    public User getUser(int index) {
        return users.get(index);
    }

    public int login(String userID, String email, String password) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUserID().equals(userID) && user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return i;
            }
        }
        return -1;
    }
    
    public String viewBooks(String bookID) {
       
        for (Book book : books) {
            if (book.getItemID().equals(bookID)) {
                
                System.out.println("Book Details:");
                System.out.println("ItemID: " + book.getItemID());
                System.out.println("Title: " + book.getTitle());
                System.out.println("Author: " + book.getAuthor());
                System.out.println("Publication Date: " + book.getPublicationDate());
                System.out.println("Genre: " + book.getGenre());
                System.out.println("ISBN: " + book.getISBN());
                return bookID;
            }
        }
        
        System.out.println("Journal with ID " + bookID + " not found.");
		return bookID;
    }


    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book added successfully: " + book);
        writeBookToFile(book);
    }

    public List<Book> getBooks() {
        return books;
    }
    
    public List<Book> searchBooks(String query) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                results.add(book);
            }
        }
        return results;
    }

    public boolean removeBook(String itemID) {
        Iterator<Book> iterator = books.iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (book.getItemID().equals(itemID)) {
                iterator.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            updateBooksFile();
            System.out.println("Book removed successfully.");
        } else {
            System.out.println("Book not found.");
        }

        return removed;
    }
    

    public boolean borrowBook(String userID, String bookID) {
        if (userID == null || bookID == null) {
            System.out.println("Error: userID or bookID is null.");
            return false;
        }
        for (Book book : books) {
            if (book.getItemID().equals(bookID)) {
                if (borrowedBooks.containsKey(bookID)) {
                    BorrowInfo borrowInfo = borrowedBooks.get(bookID);
                
                    if (borrowInfo == null) {
                        System.out.println("Error: borrowInfo for bookID " + bookID + " is null.");
                        return false;
                    }

                    if (borrowInfo.getUserID() != null && borrowInfo.getUserID().equals(userID)) {
                        System.out.println("The book is already borrowed by this user.");
                        return false;
                    } else {
                        System.out.println("The book is already borrowed by another user.");
                        return false;
                    }
                } else {
              
                    borrowedBooks.put(bookID, new BorrowInfo(userID, LocalDate.now()));
                    System.out.println("Book borrowed successfully by userID: " + userID);
                    return true;
                }
            }
        }

        System.out.println("Book with ID " + bookID + " not found.");
        return false;
    }


    public boolean returnBook(String userID, String bookID) {
        if (userID == null || bookID == null) {
            System.out.println("Error: userID or bookID is null.");
            return false;
        }

        if (borrowedBooks.containsKey(bookID)) {
            BorrowInfo borrowInfo = borrowedBooks.get(bookID);
            
            if (borrowInfo == null) {
                System.out.println("Error: borrowInfo for bookID " + bookID + " is null.");
                return false;
            }

            if (borrowInfo.getUserID() != null && borrowInfo.getUserID().equals(userID)) {
               
                borrowedBooks.remove(bookID);
                System.out.println("Book returned successfully by userID: " + userID);
                return true;
            } else {
                
                System.out.println("Failed to return book. It is borrowed by another user.");
                return false;
            }
        } else {
          
            System.out.println("Failed to return book. It might not be borrowed.");
            return false;
        }
    }

    public String viewJournals(String journalID) {
        
        for (Journal journal : journals) {
            if (journal.getItemID().equals(journalID)) {
               
                System.out.println("Journal Details:");
                System.out.println("ItemID: " + journal.getItemID());
                System.out.println("Title: " + journal.getTitle());
                System.out.println("Author: " + journal.getAuthor());
                System.out.println("Publication Date: " + journal.getPublicationDate());
                System.out.println("Volume: " + journal.getVolume());
                System.out.println("Issue Number: " + journal.getIssue());
                System.out.println("ISSN: " + journal.getISSN());
                return journalID;
            }
        }
        
        System.out.println("Journal with ID " + journalID + " not found.");
		return journalID;
    }

    
    public void addJournal(Journal journal) {
        journals.add(journal);
        System.out.println("Journal added successfully: " + journal);
        writeJournalToFile(journal);
    }

    public List<Journal> getJournals() {
        return journals;
    }

    public List<Journal> searchJournals(String query) {
        List<Journal> results = new ArrayList<>();
        for (Journal journal : journals) {
            if (journal.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                journal.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                results.add(journal);
            }
        }
        return results;
    }

    public boolean removeJournal(String itemID) {
        Iterator<Journal> iterator = journals.iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            Journal journal = iterator.next();
            if (journal.getItemID().equals(itemID)) {
                iterator.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            updateJournalsFile();
            System.out.println("Journal removed successfully.");
        } else {
            System.out.println("Journal not found.");
        }

        return removed;
    }
    
    public boolean borrowJournal(String userID, String journalID) {
        for (Journal journal : journals) {
            if (journal.getItemID().equals(journalID)) {
                if (borrowedBooks.containsKey(journalID)) {
                    BorrowInfo borrowInfo = borrowedBooks.get(journalID);
                    if (borrowInfo.getUserID().equals(userID)) {
                        System.out.println("The book is already borrowed.");
                        return false;
                    } 
                } else {
                    borrowedBooks.put(journalID, new BorrowInfo(userID, LocalDate.now()));
                    return true;
                }
            }
        }
        System.out.println("Journal not found.");
        return false;
    }


    public boolean returnJournal(String userID, String journalID) {
        if (borrowedBooks.containsKey(journalID) && borrowedBooks.get(journalID).getUserID().equals(userID)) {
            borrowedBooks.remove(journalID);
            return true;
        }else {
        System.out.println("Failed to return book. It might not be borrowed by you.");
        return false;
        }
    }

    public double calculateFine(String userID) {
        double totalFine = 0.0;
        LocalDate today = LocalDate.now();

        for (Map.Entry<String, BorrowInfo> entry : borrowedBooks.entrySet()) {
            BorrowInfo info = entry.getValue();
            if (info.getUserID().equals(userID)) {
                LocalDate borrowDate = info.getBorrowDate();
                long daysOverdue = ChronoUnit.DAYS.between(borrowDate.plusDays(BORROW_PERIOD_DAYS), today);
                if (daysOverdue > 0) {
                    totalFine += daysOverdue * DAILY_FINE;
                }
            }
        }
        return totalFine;
    }

    private void loadUsersFromFiles() {
        loadUsersFromFile(STUDENTS_FILE_PATH, Student.class);
        loadUsersFromFile(LIBRARIANS_FILE_PATH, Librarian.class);
    }


    private void loadUsersFromFile(String filePath, Class<? extends User> userClass) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            User user = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Name:")) {
                    String name = line.substring(6).trim();
                    String email = reader.readLine().substring(6).trim();
                    String id = reader.readLine().substring(4).trim();
                    String passwordLine = reader.readLine();
                    String password = passwordLine != null && passwordLine.length() > 10 
                        ? passwordLine.substring(10).trim()
                        : ""; 
                    if (userClass == Librarian.class) {
                        user = new Librarian(id, name, email, password);
                    } else {
                        user = new Student(id, name, email, password);
                    }
                    users.add(user);
                    reader.readLine(); 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveUserToFile(User user) {
        String filePath = (user instanceof Librarian) ? LIBRARIANS_FILE_PATH : STUDENTS_FILE_PATH;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            if (!isUserInFile(filePath, user.getUserID())) {
                writer.write("Name: " + user.getName() + "\n");
                writer.write("Email: " + user.getEmail() + "\n");
                writer.write("ID: " + user.getUserID() + "\n");
                writer.write("Password: " + user.getPassword() + "\n\n");
            } else {
                System.out.println("User with ID " + user.getUserID() + " is already in the file.");
            }
        } catch (IOException e) {
            System.out.println("Error writing user info to file: " + e.getMessage());
        }
    }

    private boolean isUserInFile(String filePath, String userID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ID:") && line.substring(4).trim().equals(userID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUserExists(String id) {
        for (User user : users) {
            if (user.getUserID().equals(id)) {
                return true;
            }
        }
        return false;
    }



    void loadBooksFromFile() {
    	books.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE_PATH))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String[] details = line.split(",");
                if (details.length == 6) {
                    Book book = new Book(details[0], details[1], details[2], details[3], details[4], details[5]);
                    books.add(book);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateBooksFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKS_FILE_PATH))) {
            writer.println(BOOKS_FILE_HEADER);
            for (Book book : books) {
                writer.println(book.getItemID() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.getPublicationDate() + "," + book.getGenre() + "," + book.getISBN());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBookToFile(Book book) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKS_FILE_PATH, true))) {
            
            File file = new File(BOOKS_FILE_PATH);
            if (file.length() == 0) {
                writer.println(BOOKS_FILE_HEADER);
            }
            
            writer.println(book.getItemID() + "," + book.getTitle() + "," + book.getAuthor() + "," + book.getPublicationDate() + "," + book.getGenre() + "," + book.getISBN());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void loadJournalsFromFile() {
    	journals.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(JOURNALS_FILE_PATH))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String[] details = line.split(",");
                if (details.length == 7) {
                    Journal journal = new Journal(details[0], details[1], details[2], details[3], details[4], details[5], details[6]);
                    journals.add(journal);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeJournalToFile(Journal journal) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(JOURNALS_FILE_PATH, true))) {
            File file = new File(JOURNALS_FILE_PATH);
            if (file.length() == 0) {
                writer.println(JOURNALS_FILE_HEADER);
            }
            writer.println(journal.getItemID() + "," + journal.getTitle() + "," + journal.getAuthor() + "," +
                    journal.getPublicationDate() + "," + journal.getVolume() + "," + journal.getIssue() + "," + journal.getISSN());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateJournalsFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(JOURNALS_FILE_PATH))) {
            writer.println(JOURNALS_FILE_HEADER);
            for (Journal journal : journals) {
                writer.println(journal.getItemID() + "," + journal.getTitle() + "," + journal.getAuthor() + "," +
                        journal.getPublicationDate() + "," + journal.getVolume() + "," + journal.getIssue() + "," + journal.getISSN());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static class BorrowInfo {
        private String userID;
        private LocalDate borrowDate;

        public BorrowInfo(String userID, LocalDate borrowDate) {
            this.userID = userID;
            this.borrowDate = borrowDate;
        }

        public String getUserID() {
            return userID;
        }

        public LocalDate getBorrowDate() {
            return borrowDate;
        }
    }


	public Book getBookID(String bookID) {
		   for (Book book : books) {
		        if (book.getItemID().equals(bookID)) {
		            return book; 
		        }
		    }
		    return null;
		
	}

	public Journal getJournalID(String journalID) {
		   for (Journal journal : journals) {
		        if (journal.getItemID().equals(journalID)) {
		            return journal; 
		        }
		    }
		return null;
	}

	public boolean isBookIDExists(String bookID) {
	    for (Book book : books) {
	        if (book.getItemID().equals(bookID)) {
	            return true; 
	        }
	    }
	    return false; 
	}

	public boolean isJournalIDExists(String journalID) {
	    for (Journal journal : journals) {
	        if (journal.getItemID().equals(journalID)) {
	            return true; 
	        }
	    }
	    return false; 
	}

	public BorrowInfo getBorrowInfo(String bookID) {

	    if (borrowedBooks.containsKey(bookID)) {
	        return borrowedBooks.get(bookID);
	    } else {
	        System.out.println("No borrow record found for bookID: " + bookID);
	        return null;
	    }
	}



  

}
