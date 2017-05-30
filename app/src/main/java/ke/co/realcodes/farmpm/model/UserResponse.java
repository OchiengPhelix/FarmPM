package ke.co.realcodes.farmpm.model;


public class UserResponse {
    public boolean success;
    public String message;
    private int id;
    public String random_id;
    private String phone_number;
    public String verification_code;
    private String date;
    private String expiry_date;
    public String fname;
    public String lname;
    public String username;
    public String location;
    public String profile_picture;
    public String profile_picture_name;

    public UserResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", random_id='" + random_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", verification_code='" + verification_code + '\'' +
                ", date='" + date + '\'' +
                ", expiry_date='" + expiry_date + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", username='" + username + '\'' +
                ", location='" + location + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", profile_picture_name='" + profile_picture_name + '\'' +
                '}';
    }
}
