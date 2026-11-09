package Entity;

import jakarta.persistence.*;

@Entity
public class SeatClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SeatClassID", nullable = false)
    private Integer id;

    @Column(name = "SeatClass", nullable = false, length = 50)
    private String seatClass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FlightNo", nullable = false)
    private Flight flightNo;

    @Column(name = "AdultPrice")
    private Double adultPrice;

    @Column(name = "ChildPrice")
    private Double childPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public Flight getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(Flight flightNo) {
        this.flightNo = flightNo;
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

}