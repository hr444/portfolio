package LibraryCatalog;



public class Journal extends Item {
	private String author;
    private String publicationDate;
    private String ISSN;
    private String volume;
    private String issue;
    
    public Journal(String itemID, String title, String author, String publicationDate, String volume, String issue, String ISSN)
     {
        super(itemID, title, author); 
        this.author = author;
        this.ISSN = ISSN;
        this.volume = volume;
        this.issue = issue;
        this.publicationDate = publicationDate;
    }

 
    public String getISSN() {
        return ISSN;
    }

    public String getVolume() {
        return volume;
    }

    public String getIssue() {
        return issue;
    }
    
    public String getPublicationDate() {
    	return publicationDate;
    }
    
    public String getAuthor() {
    	return author;
    }
    

    @Override
    public String getItemDetails() {
        return "Journal [ID=" + getItemID() + ", Title=" + getTitle() + ", Author=" + getAuthor() + 
               ", Publication Date=" + getPublicationDate() + ", ISSN=" + ISSN + 
               ", Volume=" + volume + ", Issue=" + issue + "]";
    }


}

