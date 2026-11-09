package Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Vehicles")
public class Vehicle {
    @Id
    @Column(name = "VehicleNo", nullable = false, length = 20)
    private String vehicleNo;

    @Column(name = "VehicleName", nullable = false)
    private String vehicleName;

    @Column(name = "DepartureFrom", nullable = false)
    private String departureFrom;

    @Column(name = "ArrivalTo", nullable = false)
    private String arrivalTo;

    @Column(name = "DepartureDate", nullable = false)
    private LocalDate departureDate;

    @Column(name = "DepartureTime", nullable = false)
    private LocalTime departureTime;

    @Column(name = "TotalSeats", columnDefinition = "tinyint not null")
    private Short totalSeats;

    @Column(name = "Images", nullable = false)
    private byte[] images;

    @Column(name = "AdultPrice", nullable = false)
    private Double adultPrice;

    @Column(name = "ChildPrice", nullable = false)
    private Double childPrice;

    @Lob
    @Column(name = "Description", nullable = false)
    private String description;

    @Column(name = "UploadBy", nullable = false, length = 50)
    private String uploadBy;

    @OneToMany(mappedBy = "vehicleNo")
    private Set<Booking> bookings = new LinkedHashSet<>();

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
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

    public Double getAdultPrice() {
        return adultPrice;
    }

    public void setAdultPrice(Double adultPrice) {
        this.adultPrice = adultPrice;
    }

    public Double getChildPrice() {
        return childPrice;
    }

    public void setChildPrice(Double childPrice) {
        this.childPrice = childPrice;
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

}