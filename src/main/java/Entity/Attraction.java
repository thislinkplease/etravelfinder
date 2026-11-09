package Entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Attractions")
public class Attraction {
    @Id
    @Column(name = "AttractionName", nullable = false, length = 400)
    private String attractionName;

    @Column(name = "AttractionAddress", nullable = false, length = 400)
    private String attractionAddress;

    @Column(name = "Category", nullable = false, length = 50)
    private String category;

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

    @OneToMany(mappedBy = "attractionName")
    private Set<Booking> bookings = new LinkedHashSet<>();

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public String getAttractionAddress() {
        return attractionAddress;
    }

    public void setAttractionAddress(String attractionAddress) {
        this.attractionAddress = attractionAddress;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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