package com.labstack;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

@SuppressWarnings("Duplicates")
public class Client {
    private String apiKey;
    private OkHttpClient okHttp;
    private Moshi moshi = new Moshi.Builder().add(Date.class, new Rfc3339DateJsonAdapter().nullSafe()).build();
    private static final String API_URL = "https://api.labstack.com";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    // JSON adapters
    private JsonAdapter<ApiException> apiExceptionJsonAdapter = moshi.adapter(ApiException.class);
    private JsonAdapter<Email.VerifyRequest> emailVerifyRequestJsonAdapter = moshi.adapter(Email.VerifyRequest.class);
    private JsonAdapter<Email.VerifyResponse> emailVerifyResponseJsonAdapter = moshi.adapter(Email.VerifyResponse.class);
    private JsonAdapter<Barcode.GenerateRequest> barcodeGenerateRequestJsonAdapter = moshi.adapter(Barcode.GenerateRequest.class);
    private JsonAdapter<Barcode.GenerateResponse> barcodeGenerateResponseJsonAdapter = moshi.adapter(Barcode.GenerateResponse.class);
    private JsonAdapter<Barcode.ScanResponse> barcodeScanResponseJsonAdapter = moshi.adapter(Barcode.ScanResponse.class);
    private JsonAdapter<Image.CompressResponse> imageCompressResponseJsonAdapter = moshi.adapter(Image.CompressResponse.class);
    private JsonAdapter<Image.ResizeResponse> imageResizeResponseJsonAdapter = moshi.adapter(Image.ResizeResponse.class);
    private JsonAdapter<Text.SentimentRequest> textSentimentRequestJsonAdapter = moshi.adapter(Text.SentimentRequest.class);
    private JsonAdapter<Text.SentimentResponse> textSentimentResponseJsonAdapter = moshi.adapter(Text.SentimentResponse.class);
    private JsonAdapter<Text.SpellCheckRequest> textSpellCheckRequestJsonAdapter = moshi.adapter(Text.SpellCheckRequest.class);
    private JsonAdapter<Text.SpellCheckResponse> textSpellCheckResponseJsonAdapter = moshi.adapter(Text.SpellCheckResponse.class);
    private JsonAdapter<Text.SummaryRequest> textSummaryRequestJsonAdapter = moshi.adapter(Text.SummaryRequest.class);
    private JsonAdapter<Text.SummaryResponse> textSummaryResponseJsonAdapter = moshi.adapter(Text.SummaryResponse.class);

    public Client(String apiKey) {
        this.apiKey = apiKey;
        okHttp = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor(apiKey))
                .build();
    }

    public void download(String id, String path) {
        try (BufferedSink sink = Okio.buffer(Okio.sink(Paths.get(path)))) {
            Request request = new Request.Builder()
                    .url(Client.API_URL + "/download/" + id)
                    .build();
            Response response = okHttp.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download file: " + response);
            }
            sink.writeAll(response.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }

    public Barcode.GenerateResponse barcodeGenerate(Barcode.GenerateRequest request) {
        String json = barcodeGenerateRequestJsonAdapter.toJson(request);
        Request req = new Request.Builder()
                .url(API_URL + "/barcode/generate")
                .post(RequestBody.create(MEDIA_TYPE_JSON, json))
                .build();
        try {
            Response res = okHttp.newCall(req).execute();
            if (res.isSuccessful()) {
                return barcodeGenerateResponseJsonAdapter.fromJson(res.body().source());
            }
            throw apiExceptionJsonAdapter.fromJson(res.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }

    public Barcode.ScanResponse barcodeScan(Barcode.ScanRequest request) {
        try {
            File file = new File(request.getFile());
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(null, file))
                    .build();
            Request req = new Request.Builder()
                    .url(API_URL + "/barcode/scan")
                    .post(body)
                    .build();
            Response res = okHttp.newCall(req).execute();
            if (res.isSuccessful()) {
                return barcodeScanResponseJsonAdapter.fromJson(res.body().source());
            }
            throw apiExceptionJsonAdapter.fromJson(res.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }

    public Email.VerifyResponse emailVerify(Email.VerifyRequest request) {
        String json = emailVerifyRequestJsonAdapter.toJson(request);
        Request req = new Request.Builder()
                .url(API_URL + "/email/verify")
                .post(RequestBody.create(MEDIA_TYPE_JSON, json))
                .build();
        try {
            Response res = okHttp.newCall(req).execute();
            if (res.isSuccessful()) {
                return emailVerifyResponseJsonAdapter.fromJson(res.body().source());
            }
            throw apiExceptionJsonAdapter.fromJson(res.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }

    public Image.CompressResponse imageCompress(Image.CompressRequest request) {
        try {
            File file = new File(request.getFile());
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(null, file))
                    .build();
            Request req = new Request.Builder()
                    .url(API_URL + "/image/compress")
                    .post(body)
                    .build();
            Response res = okHttp.newCall(req).execute();
            if (res.isSuccessful()) {
                return imageCompressResponseJsonAdapter.fromJson(res.body().source());
            }
            throw apiExceptionJsonAdapter.fromJson(res.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }

    public Image.ResizeResponse imageResize(Image.ResizeRequest request) {
        try {
            File file = new File(request.getFile());
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(), RequestBody.create(null, file))
                    .addFormDataPart("width", String.valueOf(request.getWidth()))
                    .addFormDataPart("height", String.valueOf(request.getHeight()))
                    .addFormDataPart("crop", String.valueOf(request.isCrop()))
                    .build();
            Request req = new Request.Builder()
                    .url(API_URL + "/image/resize")
                    .post(body)
                    .build();
            Response res = okHttp.newCall(req).execute();
            if (res.isSuccessful()) {
                return imageResizeResponseJsonAdapter.fromJson(res.body().source());
            }
            throw apiExceptionJsonAdapter.fromJson(res.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }

    public Text.SentimentResponse textSentiment(Text.SentimentRequest request) {
        String json = textSentimentRequestJsonAdapter.toJson(request);
        Request req = new Request.Builder()
                .url(API_URL + "/text/sentiment")
                .post(RequestBody.create(MEDIA_TYPE_JSON, json))
                .build();
        try {
            Response res = okHttp.newCall(req).execute();
            if (res.isSuccessful()) {
                return textSentimentResponseJsonAdapter.fromJson(res.body().source());
            }
            throw apiExceptionJsonAdapter.fromJson(res.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }

    public Text.SpellCheckResponse textSpellCheck(Text.SpellCheckRequest request) {
        String json = textSpellCheckRequestJsonAdapter.toJson(request);
        Request req = new Request.Builder()
                .url(API_URL + "/text/spell-check")
                .post(RequestBody.create(MEDIA_TYPE_JSON, json))
                .build();
        try {
            Response res = okHttp.newCall(req).execute();
            if (res.isSuccessful()) {
                return textSpellCheckResponseJsonAdapter.fromJson(res.body().source());
            }
            throw apiExceptionJsonAdapter.fromJson(res.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }

    public Text.SummaryResponse textSummary(Text.SummaryRequest request) {
        String json = textSummaryRequestJsonAdapter.toJson(request);
        Request req = new Request.Builder()
                .url(API_URL + "/text/summary")
                .post(RequestBody.create(MEDIA_TYPE_JSON, json))
                .build();
        try {
            Response res = okHttp.newCall(req).execute();
            if (res.isSuccessful()) {
                return textSummaryResponseJsonAdapter.fromJson(res.body().source());
            }
            throw apiExceptionJsonAdapter.fromJson(res.body().source());
        } catch (IOException e) {
            throw new ApiException(0, e.getMessage());
        }
    }
}

class Interceptor implements okhttp3.Interceptor {
    private String apiKey;

    public Interceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(okhttp3.Interceptor.Chain chain) throws IOException {
        Request compressedRequest = chain.request().newBuilder()
                .header("Authorization", "Bearer " + apiKey)
                .build();
        return chain.proceed(compressedRequest);
    }
}

class Download {
    private String id;
    private String name;
    private String url;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}


