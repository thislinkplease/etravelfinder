package Entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class UserInfor {
    @Id
    @Column(name = "Username", nullable = false, length = 50)
    private String username;

    @Lob
    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Usertype", nullable = false, length = 50)
    private String usertype;

    @OneToMany(mappedBy = "username")
    private Set<Booking> bookings = new LinkedHashSet<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

}