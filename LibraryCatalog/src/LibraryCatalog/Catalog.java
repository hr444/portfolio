package LibraryCatalog;

import java.util.ArrayList;
import java.util.List;

public class Catalog {
    private List<Item> items;

    public Catalog() {
        items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Item> searchCatalog(String keyword) {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(item);
            }
        }
        return result;
    }
}
