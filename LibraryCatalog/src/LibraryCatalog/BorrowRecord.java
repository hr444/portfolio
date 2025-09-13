package LibraryCatalog;

import java.time.LocalDate;

public class BorrowRecord {
    private String userID;
    private String bookID;
    private String journalID;
    private LocalDate borrowDate;

    public BorrowRecord(String userID, String bookID, String journalID, LocalDate borrowDate) {
        this.userID = userID;
        this.bookID = bookID;
        this.journalID = journalID;
        this.borrowDate = borrowDate;
    }

    public String getUserID() {
        return userID;
    }

    public String getBookID() {
        return bookID;
    }
    
    public String getJournalID() {
        return journalID;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    
}

