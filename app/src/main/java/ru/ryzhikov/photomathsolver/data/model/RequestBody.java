package ru.ryzhikov.photomathsolver.data.model;

/**
 * Тело запроса
 */
public class RequestBody {
    final String url;
    final String[] formats;

    /**
     * @param url - фото в формате base64
     * @param formats - форматы полученного текста. В данном случае:
     *                latex и текст, адаптированный для wolfram
     */
    public RequestBody(String url, String[] formats) {
        this.url = url;
        this.formats = formats;
    }
}
