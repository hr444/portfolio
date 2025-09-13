package LibraryCatalog;

import java.util.List;
import java.util.Scanner;

public class Student extends User {

    public Student(String userID, String name, String email, String password) {
        super(userID, name, email, password);
    }

    @Override
    public void menu(Database database, User user) {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("Student Menu:");
            System.out.println("1. View Books");
            System.out.println("2. Search Books");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Calculate Fine");
            System.out.println("0. Logout");
            System.out.print("Select an option: ");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    viewBooks(database);
                    break;
                case 2:
                    searchBooks(database, scanner);
                    break;
                case 3:
                    borrowBook(database, scanner);
                    break;
                case 4:
                    returnBook(database, scanner);
                    break;
                case 5:
                    calculateFine(database);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

        } while (option != 0);
    }

    private void viewBooks(Database database) {
        System.out.println("Viewing available books:");
        for (Book book : database.getBooks()) {
            System.out.println(book);
        }
    }

    private void searchBooks(Database database, Scanner scanner) {
    	System.out.println("Enter search query (title or author): ");
    	scanner.nextLine();
    	String query = scanner.nextLine();
    	
    	List<Book> results = database.searchBooks(query);
    	
        if (results.isEmpty()) {
            System.out.println("No books found matching your query.");
        } else {
            System.out.println("Search results:");
            for (Book book : results) {
                System.out.println(book); 
            }
        }
    }


    private void borrowBook(Database database, Scanner scanner) {
        System.out.println("Enter the Book ID to borrow: ");
        String bookID = scanner.next();
        boolean success = database.borrowBook(this.getUserID(), bookID);
        if (success) {
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Failed to borrow book. It might be unavailable.");
        }
    }

    private void returnBook(Database database, Scanner scanner) {
        System.out.println("Enter the Book ID to return: ");
        String bookID = scanner.next();
        boolean success = database.returnBook(this.getUserID(), bookID);
        if (success) {
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Failed to return book. It might not be borrowed.");
        }
    }

    private void calculateFine(Database database) {
        double fine = database.calculateFine(this.getUserID());
        System.out.println("Your total fine is: RM " + fine);
    }
}
