package LibraryCatalog;

import java.util.Scanner;

public class Main {
    static Scanner s;
    static Database database;

    public static void main(String[] args) {
        database = new Database(); 
        System.out.println("Welcome to Library Management System\n");

        int num;
        do {
            System.out.println("1. Login\n2. New User\n0.Exit");
            s = new Scanner(System.in);
            num = s.nextInt();

            switch (num) {
                case 1: login(); break;
                case 2: newUser(); break;
                case 0: System.out.println("Exiting... Thank You"); break;
                default: System.out.println("Invalid option. Please try again.");
            }
        } while (num != 0);
    }

    private static void login() {
        System.out.println("Enter User ID: ");
        String userID = s.next();
        System.out.println("Enter email: ");
        String email = s.next();
        System.out.println("Enter password: ");
        String password = s.next();

        int userIndex = database.login(userID, email, password);
        if (userIndex != -1) {
            User user = database.getUser(userIndex);
            user.menu(database, user);
        } else {
            System.out.println("User doesn't exist or incorrect credentials!");
        }
    }

    private static void newUser() {
        	System.out.println("Enter User ID: ");
	        String userID = s.next();
	        
	        System.out.println("Enter name: ");
	        s.nextLine();
	        String name = s.nextLine();
	      

	        System.out.println("Enter email: ");
	        String email = s.next();

	        System.out.println("Enter password: ");
	        String password = s.next();

	        System.out.println("1. Librarian\n2. Student ");
	        int userType = s.nextInt();
	        User user;
	        if (userType == 1) {
	            user = new Librarian(userID, name, email, password);
	        } else {
	            user = new Student(userID, name, email, password);
	        }
	        database.addUser(user);
	        user.menu(database, user);
        
        }
 }
    

