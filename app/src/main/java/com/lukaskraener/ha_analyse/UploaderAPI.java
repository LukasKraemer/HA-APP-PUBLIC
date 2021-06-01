package com.lukaskraener.ha_analyse;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploaderAPI {

    public static void uploadFile(String serverURL, File file, String token) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("txtFile", file.getName(),
                            RequestBody.create (file, MediaType.parse("text/plain")))

                    .build();

            Request request = new Request.Builder()
                    .url(serverURL+"filename")
                    .header("User-Agent", "HA-Tool Android")
                    .addHeader("Authorization", token)
                    .post(requestBody)
                    .build();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(final Call call, final IOException e) {
                        API.Companion.fail();
                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {
                        API.Companion.sucess();
                        response.close();
                    }
                });
            } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
