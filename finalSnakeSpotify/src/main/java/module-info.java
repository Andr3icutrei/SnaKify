module com.example.finalsnakespotify {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jdi;

    requires java.net.http;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires java.desktop;

    opens com.example.finalsnakespotify to javafx.fxml;
    exports com.example.finalsnakespotify;
    exports com.example.finalsnakespotify.Models;
    opens com.example.finalsnakespotify.Models to javafx.fxml;
    exports com.example.finalsnakespotify.Controllers;
    opens com.example.finalsnakespotify.Controllers to javafx.fxml;
    exports com.example.finalsnakespotify.Interfaces;
    opens com.example.finalsnakespotify.Interfaces to javafx.fxml;
}