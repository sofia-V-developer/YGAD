package com.example.ygad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeepSeekOCR {

    // ВСТАВЬ СВОЙ API КЛЮЧ ОТ DEEPSEEK
    // Получить можно на platform.deepseek.com после регистрации
    private static final String API_KEY = "sk-a90216ee40f8435bb4bee6ad7e574a5f";  // ← ЗАМЕНИ НА СВОЙ КЛЮЧ
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    private OkHttpClient client;

    public DeepSeekOCR() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public interface OCRCallback {
        void onSuccess(String recognizedText);
        void onError(String error);
    }

    public void recognizeGradesFromImage(String imagePath, OCRCallback callback) {
        new Thread(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                if (bitmap == null) {
                    callback.onError("Не удалось загрузить фото");
                    return;
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                String prompt = "Ты помощник для распознавания оценок студентов. " +
                        "На фото таблица с оценками. Извлеки фамилии и оценки. " +
                        "Верни результат строго в формате: Фамилия Оценка\n" +
                        "Например:\nИванов 5\nПетрова 4\nСидоров 3\n\n" +
                        "Не пиши ничего лишнего, только фамилии и оценки.";

                JsonObject message = new JsonObject();
                message.addProperty("role", "user");
                message.addProperty("content", prompt + "\n\nФото: " + base64Image);

                JsonArray messages = new JsonArray();
                messages.add(message);

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", "deepseek-chat");
                requestBody.add("messages", messages);
                requestBody.addProperty("temperature", 0.1);

                RequestBody body = RequestBody.create(
                        requestBody.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    String recognizedText = jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .get("message").getAsJsonObject()
                            .get("content").getAsString();
                    callback.onSuccess(recognizedText);
                } else {
                    callback.onError("Ошибка API: " + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("Ошибка: " + e.getMessage());
            }
        }).start();
    }
}