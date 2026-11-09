package src.Model;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserSession {
    private static UserSession instance;
    private String username;
    private String usertype;

    private UserSession(String username) {
        this.username = username;
        this.usertype = getUserTypeFromDatabase(username);
    }

    public static UserSession getInstance(String username) {
        if (instance == null) {
            instance = new UserSession(username);
        } else {
            instance.username = username;
            instance.usertype = instance.getUserTypeFromDatabase(username);
        }
        return instance;
    }

    public static UserSession getInstance() {
        return instance;
    }

    public String getUserName() {
        return username;
    }

    public String getUserType() {
        return usertype;
    }

    public void setUserType(String usertype) {
        this.usertype = usertype;
    }

    public void cleanUserSession() {
        username = "";
        usertype = "";
        instance = null;
    }

    private String getUserTypeFromDatabase(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT usertype FROM UserInfor WHERE username = :username";
            Query<String> query = session.createQuery(hql);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "username='" + username + '\'' +
                ", usertype='" + usertype + '\'' +
                '}';
    }
}
