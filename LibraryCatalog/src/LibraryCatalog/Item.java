package LibraryCatalog;

public class Item {
    protected String itemID;
    protected String title;
    protected String author;
    protected String publicationDate;
    protected boolean availability;
    protected String genre;

    
    public Item(String itemID, String title, String genre) {
        this.itemID = itemID;
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.availability = true;
    }



	public String getItemID() {
        return itemID;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAvailable() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getItemDetails() {
        return "Title: " + title + ", Author: " + author + ", Published: " + publicationDate;
    }
}
