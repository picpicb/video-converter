package edu.esipe.i3.ezipflix.dispatcher;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
public class ConversionRequest {

    private String path;
    private String format;

    public ConversionRequest() {
    }
    public ConversionRequest(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
