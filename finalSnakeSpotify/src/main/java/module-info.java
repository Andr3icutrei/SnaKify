module com.example.finalsnakespotify {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jdk.jdi;
    requires nv.i18n;
    requires org.apache.httpcomponents.core5.httpcore5;

    requires se.michaelthelin.spotify;
    requires java.net.http;
    requires javafx.media;

    opens com.example.finalsnakespotify to javafx.fxml;
    exports com.example.finalsnakespotify;
    exports com.example.finalsnakespotify.Models;
    opens com.example.finalsnakespotify.Models to javafx.fxml;
}