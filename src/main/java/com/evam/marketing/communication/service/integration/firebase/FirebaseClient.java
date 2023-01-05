package com.evam.marketing.communication.service.integration.firebase;

import com.evam.marketing.communication.service.integration.firebase.model.PushNotificationRequest;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Firebase client
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Slf4j
public class FirebaseClient {
    private static final String FIREBASE_APPLICATION_NAME = "jazzcash-consumer-app";
    private static final String FIREBASE_ADMIN_JSON_PATH = "./config/jazzcash-consumer-app-firebase.json";
    private static final String PUSH_CATEGORY = "evam";
    private static final String PUSH_DATA_KEY = "data";
    private static final String CAMPAIGN_NOTIFICATION_KEY = "campaignNotification";
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";

    private static final List<String> SCOPES = Arrays.asList("https://accounts.google.com/o/oauth2/auth");

    private static FirebaseClient instance;

    private static FirebaseApp firebaseApp;

    private static void init() {
        try {
            //Change firebase
            FileInputStream credentialStream = new FileInputStream(FIREBASE_ADMIN_JSON_PATH);

            // auth/cloud-platform must
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(credentialStream)
                    .createScoped(SCOPES);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options, FIREBASE_APPLICATION_NAME);

            instance = new FirebaseClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FirebaseClient() {
        // do nothing
    }

    public static FirebaseClient getInstance() {
        if (instance == null) {
            init();
        }

        return instance;
    }

    //os: ios or android
    public BatchResponse pushNotification(PushNotificationRequest request, String os)
        throws RuntimeException, FirebaseMessagingException, IllegalArgumentException {

        checkArguments(request);

        Message.Builder builder = Message.builder();

        if (os.equalsIgnoreCase("ios")) {
            ApnsConfig apnsConfig = generateApnsConfig(request);
            builder.setApnsConfig(apnsConfig);

        } else if (os.equalsIgnoreCase("android")) {
            Notification notification = generateAndroidNotification(request);
            AndroidConfig config = AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setTtl(3600*1000)
                    .build();
            builder.setAndroidConfig(config);
            builder.setNotification(notification);

        } else {
            throw new IllegalArgumentException("unexpected device operation system!");
        }

        builder.putData(CAMPAIGN_NOTIFICATION_KEY,String.valueOf(request.getMessage().getData().isCampaignNotification()));
        builder.putData(DESCRIPTION,request.getMessage().getData().getDescription());
        builder.putData(TITLE,request.getMessage().getNotification().getTitle());

        Message message = builder.setToken(request.getMessage().getToken()).build();
        log.debug("message: {}", message.toString());

        return FirebaseMessaging.getInstance(firebaseApp)
            .sendAll(Collections.singletonList(message),false);
    }

    private static Notification generateAndroidNotification(PushNotificationRequest request) {
        return Notification.builder()
                .setTitle(request.getMessage().getNotification().getTitle())
                .setBody(request.getMessage().getNotification().getBody())
                .build();
    }

    private static ApnsConfig generateApnsConfig(PushNotificationRequest request) {
        ApsAlert apsAlert = ApsAlert.builder()
                .setTitle(request.getMessage().getNotification().getTitle())
                .setBody(request.getMessage().getNotification().getBody())
                .build();
        Aps aps = Aps.builder().setCategory(PUSH_CATEGORY).setAlert(apsAlert).setContentAvailable(true).build();
        return ApnsConfig.builder().putHeader("apns-priority","10").setAps(aps).build();
    }

    private static void checkArguments(PushNotificationRequest request) {
        assert request != null;
    }
}
