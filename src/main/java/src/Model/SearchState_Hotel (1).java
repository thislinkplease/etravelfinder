package src.Model;

import Entity.Hotel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SearchState_Hotel {
    private static SearchState_Hotel instance;

    private String destination;
    private double minPrice;
    private double maxPrice;
    private ObservableList<Hotel> searchResults = FXCollections.observableArrayList();

    private SearchState_Hotel() {}

    public static SearchState_Hotel getInstance() {
        if (instance == null) {
            instance = new SearchState_Hotel();
        }
        return instance;
    }

    public void setSearchParameters(String destination, double minPrice, double maxPrice) {
        this.destination = destination;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getDestination() {
        return destination;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public ObservableList<Hotel> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Hotel> results) {
        searchResults.setAll(results);
    }
}
