package com.evam.marketing.communication.service.integration.firebase.model;

/**
 * Push notification custom data
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public class PushNotificationCustomData {
    private boolean campaignNotification;
    private String description;

    public boolean isCampaignNotification() {
        return campaignNotification;
    }

    public void setCampaignNotification(boolean campaignNotification) {
        this.campaignNotification = campaignNotification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static final class PushNotificationCustomDataBuilder {
        private boolean campaignNotification;
        private String description;

        private PushNotificationCustomDataBuilder() {
        }

        public static PushNotificationCustomDataBuilder aPushNotificationCustomData() {
            return new PushNotificationCustomDataBuilder();
        }

        public PushNotificationCustomDataBuilder campaignNotification(
            boolean campaignNotification) {
            this.campaignNotification = campaignNotification;
            return this;
        }

        public PushNotificationCustomDataBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PushNotificationCustomData build() {
            PushNotificationCustomData pushNotificationCustomData = new PushNotificationCustomData();
            pushNotificationCustomData.setCampaignNotification(campaignNotification);
            pushNotificationCustomData.setDescription(description);
            return pushNotificationCustomData;
        }
    }

    @Override
    public String toString() {
        String template = "PushNotificationCustomData{campaignNotification=%s, description=%s}";
        return String.format(template, this.campaignNotification, this.description);
    }
}
