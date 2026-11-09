package src.Model;

import java.time.LocalDate;

public class SearchState_Flight {
    private String departureFrom;
    private String arrivalTo;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private boolean showDate;

    public SearchState_Flight(String departureFrom, String arrivalTo, LocalDate departureDate, LocalDate returnDate, boolean showDate) {
        this.departureFrom = departureFrom;
        this.arrivalTo = arrivalTo;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.showDate = showDate;
    }

    // Getters and Setters
    public String getDepartureFrom() {
        return departureFrom;
    }

    public void setDepartureFrom(String departureFrom) {
        this.departureFrom = departureFrom;
    }

    public String getArrivalTo() {
        return arrivalTo;
    }

    public void setArrivalTo(String arrivalTo) {
        this.arrivalTo = arrivalTo;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }
}
