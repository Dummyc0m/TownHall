package com.dummyc0m.bukkit.townhall.region;

import java.util.UUID;

/**
 * Created by Dummyc0m on 3/12/16.
 */
public class PlayerMessage {
    private volatile UUID from;
    private volatile String title;
    private volatile String message;
    private volatile boolean isApplication;

    public PlayerMessage(UUID from, String title, String message, boolean isApplication) {
        this.from = from;
        this.title = title;
        this.message = message;
        this.isApplication = isApplication;
    }

    public UUID getFrom() {
        return from;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isApplication() {
        return isApplication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerMessage that = (PlayerMessage) o;

        if (isApplication != that.isApplication) return false;
        if (!from.equals(that.from)) return false;
        if (!title.equals(that.title)) return false;
        return message.equals(that.message);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + (isApplication ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlayerMessage{" +
                "from=" + from +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", isApplication=" + isApplication +
                '}';
    }

    public static class PlayerMsgBuilder {
        private String name;
        private UUID from;
        private String title;
        private String message;
        private boolean isApplication;

        public void setFrom(UUID from, String name) {
            this.from = from;
            this.name = name;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setApplication(boolean isApplication) {
            this.isApplication = isApplication;
        }

        public PlayerMessage build() {
            return new PlayerMessage(from, title, message + " - " + name, isApplication);
        }
    }
}
