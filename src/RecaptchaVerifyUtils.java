// derived from Project 3 reCaptcha Example: https://github.com/UCI-Chenli-teaching/cs122b-project3-recaptcha-example/blob/main/src/RecaptchaVerifyUtils.java

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class RecaptchaVerifyUtils {
    public static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public static void verify(String gRecaptchaResponse) throws Exception {
        URL verifyUrl = new URL(SITE_VERIFY_URL);

        // open connection to URL
        HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();

        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // prepare data to be sent to server
        String postParams = "secret=" + RecaptchaConstants.SECRET_KEY + "&response=" + gRecaptchaResponse;

        // send request
        conn.setDoOutput(true);

        OutputStream outStream = conn.getOutputStream();
        outStream.write(postParams.getBytes());

        outStream.flush();
        outStream.close();

        // get the InputStream from connection to read data sent from the server.
        InputStream inputStream = conn.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        JsonObject jsonObject = new Gson().fromJson(inputStreamReader, JsonObject.class);

        inputStreamReader.close();

        // verification succeed
        if (jsonObject.get("success").getAsBoolean()) {
            return;
        }

        throw new Exception("recaptcha verification failed: response is " + jsonObject);
    }
}