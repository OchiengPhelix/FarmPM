package ke.co.realcodes.farmpm.rest;

import ke.co.realcodes.farmpm.model.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ResendCodeApi {
    @FormUrlEncoded
    @POST("user/resend_code/")
    Call<UserResponse> resendCode(@Field("phone") String phoneNumber, @Field("random_id") String randomId);
}
