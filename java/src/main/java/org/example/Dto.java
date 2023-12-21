public class LoginRequest {

    private String username;
    private String password;

    // Getters and setters...
}
public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getters...
}
