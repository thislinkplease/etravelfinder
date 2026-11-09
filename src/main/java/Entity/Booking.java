package Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "VehicleNo")
    private Vehicle vehicleNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FlightNo")
    private Flight flightNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AttractionName")
    private Attraction attractionName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "HotelName")
    private Hotel hotelName;

    @Column(name = "VehicleName")
    private String vehicleName;

    @Column(name = "AirlinesName", length = 50)
    private String airlinesName;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "Username", nullable = false)
    private UserInfor username;

    @Column(name = "NumberOfTickets")
    private Short numberOfTickets;

    @Column(name = "NumberOfSeats", columnDefinition = "tinyint")
    private Short numberOfSeats;

    @Column(name = "Checkin")
    private LocalDate checkin;

    @Column(name = "Checkout")
    private LocalDate checkout;

    @Column(name = "DepartureFrom")
    private String departureFrom;

    @Column(name = "ArrivalTo")
    private String arrivalTo;

    @Column(name = "DepartureDate")
    private LocalDate departureDate;

    @Column(name = "DepartureTime")
    private LocalTime departureTime;

    @Column(name = "BookingDate", nullable = false)
    private OffsetDateTime bookingDate;

    @Column(name = "Totalprice", nullable = false)
    private Double totalprice;

    @Column(name = "UploadBy", nullable = false, length = 50)
    private String uploadBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Vehicle getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(Vehicle vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Flight getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(Flight flightNo) {
        this.flightNo = flightNo;
    }

    public Attraction getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(Attraction attractionName) {
        this.attractionName = attractionName;
    }

    public Hotel getHotelName() {
        return hotelName;
    }

    public void setHotelName(Hotel hotelName) {
        this.hotelName = hotelName;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getAirlinesName() {
        return airlinesName;
    }

    public void setAirlinesName(String airlinesName) {
        this.airlinesName = airlinesName;
    }

    public UserInfor getUsername() {
        return username;
    }

    public void setUsername(UserInfor username) {
        this.username = username;
    }

    public Short getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(Short numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public Short getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Short numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public LocalDate getCheckin() {
        return checkin;
    }

    public void setCheckin(LocalDate checkin) {
        this.checkin = checkin;
    }

    public LocalDate getCheckout() {
        return checkout;
    }

    public void setCheckout(LocalDate checkout) {
        this.checkout = checkout;
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

    public OffsetDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(OffsetDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(Double totalprice) {
        this.totalprice = totalprice;
    }

    public String getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }

}