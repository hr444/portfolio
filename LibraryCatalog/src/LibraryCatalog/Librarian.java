package LibraryCatalog;

import java.util.Scanner;

public class Librarian extends User {

    public Librarian(String userID, String name, String email, String password) {
        super(userID, name, email, password);
    }

    @Override
    public void menu(Database database, User user) {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("Librarian Menu:");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. View Books");
            System.out.println("4. Check Student Penalty");
            System.out.println("0. Logout");
            System.out.print("Select an option: ");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    addBook(database, scanner);
                    break;
                case 2:
                    removeBook(database, scanner);
                    break;
                case 3:
                    viewBooks(database);
                    break;
                case 4:
                    checkStudentPenalty(database, scanner);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

        } while (option != 0);
    }

    private void addBook(Database database, Scanner scanner) {
        
        System.out.println("Enter Book ID: ");
        String itemID = scanner.next();
        
        System.out.println("Enter Book Title: ");
        String title = scanner.nextLine();
        
        System.out.println("Enter Book Author: ");
        String author = scanner.nextLine();
        
        System.out.println("Enter genre: ");
        String genre = scanner.next();
        
        System.out.println("Enter ISBN: ");
        String ISBN = scanner.next();
        
        System.out.println("Enter Publication Date (YYYY-MM-DD): ");
        scanner.nextLine(); 
        String publicationDate = scanner.nextLine();

        
        Book book = new Book(itemID, title, author, publicationDate, ISBN, genre);
        database.addBook(book);
    }

    private void removeBook(Database database, Scanner scanner) {
        
        System.out.println("Enter Book ID to remove: ");
        String itemID = scanner.next();

        
        boolean removed = database.removeBook(itemID);
        if (!removed) {
            System.out.println("No book found with the provided ID.");
        }
    }

    private void viewBooks(Database database) {
        
        System.out.println("Viewing all books:");
        for (Book book : database.getBooks()) {
            System.out.println(book);
        }
    }

    private void checkStudentPenalty(Database database, Scanner scanner) {
        
        System.out.println("Enter Student ID to check penalty: ");
        String studentID = scanner.next();

        
        double penalty = database.calculateFine(studentID);
        System.out.println("The total penalty for student ID " + studentID + " is: RM " + penalty);
    }
}
