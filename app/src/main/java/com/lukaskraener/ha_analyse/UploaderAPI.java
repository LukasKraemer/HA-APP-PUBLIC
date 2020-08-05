package com.lukaskraener.ha_analyse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploaderAPI {

    
    public static Boolean uploadFile(String serverURL, File file, String token) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("uploadedfile", file.getName(),
                            RequestBody.create(MediaType.parse("text/plain"), file))
                    .addFormDataPart("APP", "uploader")
                    .addFormDataPart("token", token)
                    .build();

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(final Call call, final IOException e) {
                    // Handle the error
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        // Handle the error
                    }
                    // Upload successful
                }
            });
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
