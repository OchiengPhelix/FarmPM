package ke.co.realcodes.farmpm.rest;

import ke.co.realcodes.farmpm.model.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ProfileInfo {
    @FormUrlEncoded
    @POST("user/user_information/")
    Call<UserResponse> insertUserInformation(@Field("random_id") String randomId, @Field("fname") String newFirstNameInput, @Field("lname") String newLastNameInput, @Field("username") String newUserNameInput, @Field("location") String newUserLocationInput);
}
