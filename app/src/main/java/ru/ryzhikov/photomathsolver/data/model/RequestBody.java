package ru.ryzhikov.photomathsolver.data.model;

public class RequestBody {
    final String url;
    final String[] formats;

    public RequestBody(String url, String[] formats) {
        this.url = url;
        this.formats = formats;
    }
}
