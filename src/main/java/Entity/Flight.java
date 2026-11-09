package Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Flights")
public class Flight {
    @Id
    @Column(name = "FlightNo", nullable = false, length = 20)
    private String flightNo;

    @Column(name = "AirlinesName", nullable = false, length = 50)
    private String airlinesName;

    @Column(name = "DepartureFrom", nullable = false)
    private String departureFrom;

    @Column(name = "ArrivalTo", nullable = false)
    private String arrivalTo;

    @Column(name = "DepartureDate", nullable = false)
    private LocalDate departureDate;

    @Column(name = "DepartureTime", nullable = false)
    private LocalTime departureTime;

    @Column(name = "TotalSeats", nullable = false)
    private Short totalSeats;

    @Column(name = "Images", nullable = false)
    private byte[] images;

    @Lob
    @Column(name = "Description", nullable = false)
    private String description;

    @Column(name = "UploadBy", nullable = false, length = 50)
    private String uploadBy;

    @OneToMany(mappedBy = "flightNo")
    private Set<Booking> bookings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "flightNo")
    private Set<SeatClass> seatClasses = new LinkedHashSet<>();

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public String getAirlinesName() {
        return airlinesName;
    }

    public void setAirlinesName(String airlinesName) {
        this.airlinesName = airlinesName;
    }

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

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public Short getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Short totalSeats) {
        this.totalSeats = totalSeats;
    }

    public byte[] getImages() {
        return images;
    }

    public void setImages(byte[] images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public Set<SeatClass> getSeatClasses() {
        return seatClasses;
    }

    public void setSeatClasses(Set<SeatClass> seatClasses) {
        this.seatClasses = seatClasses;
    }

}