package ke.co.realcodes.farmpm.rest;

import ke.co.realcodes.farmpm.model.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface RegisterApi {
    @FormUrlEncoded
    @POST("user/user_number/")
    Call<UserResponse> registerUser(@Field("phone") String phoneNumber);
}
