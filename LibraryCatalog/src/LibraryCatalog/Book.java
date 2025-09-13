package LibraryCatalog;


public class Book extends Item {
    private String author;
    private String publicationDate;
    private String ISBN;
    private String genre;

    public Book(String itemID, String title, String author, String publicationDate, String genre, String ISBN) {
        super(itemID, title, genre);
        this.author = author;
        this.publicationDate = publicationDate;
        this.ISBN = ISBN;
        this.genre = genre;
    }

  
    public String getAuthor() {
        return author;
    }
    
    public String getTitle() {
    	return title;
    }

    public String getPublicationDate() {
        return publicationDate;
    }
    
    public String getGenre() {
        return genre;
    }

    public String getISBN() {
        return ISBN;
    }


    @Override
    public String toString() {
        return "Book [ID=" + getItemID() + ", Title=" + getTitle() + ", Author=" + author + ", Publication Date=" + publicationDate + ", Genre=" + genre + ", ISBN=" + ISBN + "]";
    }
}

