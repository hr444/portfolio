package LibraryCatalog;

import java.util.List;
import java.util.ArrayList;

public class LibrarySystem {
    private Catalog catalog;
    private List<User> users;

    public LibrarySystem() {
        this.catalog = new Catalog();
        this.users = new ArrayList<>();
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getUser(String userID) {
        for (User user : users) {
            if (user.getUserID().equals(userID)) {
                return user;
            }
        }
        return null;
    }
}

