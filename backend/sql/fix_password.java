import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class fix_password {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("admin123");
        System.out.println("BCrypt密码: " + encoded);
    }
}
