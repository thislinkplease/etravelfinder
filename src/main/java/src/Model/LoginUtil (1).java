package src.Model;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;

public class LoginUtil {
    private static final String LOGIN_FILE = "login.txt";

    // Phương thức để lưu thông tin đăng nhập và ghi đè lên tệp cũ
    public static void saveLoginInfo(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOGIN_FILE, false))) {
            writer.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức để tải thông tin đăng nhập từ tệp
    public static String loadLoginInfo() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGIN_FILE))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
