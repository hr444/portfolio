package LibraryCatalog;

import java.util.Date;


public class Loan {
    private Item item;
    private Student student;
    private Date borrowDate;
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 1.0;

   
    public Loan(Item item, Student student) {
        this.item = item;
        this.student = student;
        this.borrowDate = new Date(); 
    }

    public Item getItem() {
        return item;
    }

    public double calculateFine() {
        long diffInMillies = new Date().getTime() - borrowDate.getTime();
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
        if (diffInDays > LOAN_PERIOD_DAYS) {
            return (diffInDays - LOAN_PERIOD_DAYS) * FINE_PER_DAY;
        } else {
            return 0.0;
        }
    }
}

