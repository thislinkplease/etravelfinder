package src.Model;

import Entity.Attraction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SearchState_Attraction {
    private static SearchState_Attraction instance;

    private String destination;
    private String category;
    private double minPrice;
    private double maxPrice;
    private ObservableList<Attraction> searchResults = FXCollections.observableArrayList();

    private SearchState_Attraction() {}

    public static SearchState_Attraction getInstance() {
        if (instance == null) {
            instance = new SearchState_Attraction();
        }
        return instance;
    }

    public void setSearchParameters(String destination, String category, double minPrice, double maxPrice) {
        this.destination = destination;
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getDestination() {
        return destination;
    }

    public String getCategory() {
        return category;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public ObservableList<Attraction> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Attraction> results) {
        searchResults.setAll(results);
    }
}
