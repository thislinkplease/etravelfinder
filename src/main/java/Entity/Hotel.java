package Entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Hotels")
public class Hotel {
    @Id
    @Column(name = "HotelName", nullable = false, length = 400)
    private String hotelName;

    @Column(name = "HotelAddress", nullable = false, length = 500)
    private String hotelAddress;

    @Column(name = "Images", nullable = false)
    private byte[] images;

    @Column(name = "TotalRooms", nullable = false)
    private Short totalRooms;

    @Column(name = "Price", nullable = false)
    private Double price;

    @Lob
    @Column(name = "Description", nullable = false)
    private String description;

    @Column(name = "UploadBy", nullable = false, length = 50)
    private String uploadBy;

    @OneToMany(mappedBy = "hotelName")
    private Set<Booking> bookings = new LinkedHashSet<>();

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public void setHotelAddress(String hotelAddress) {
        this.hotelAddress = hotelAddress;
    }

    public byte[] getImages() {
        return images;
    }

    public void setImages(byte[] images) {
        this.images = images;
    }

    public Short getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Short totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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