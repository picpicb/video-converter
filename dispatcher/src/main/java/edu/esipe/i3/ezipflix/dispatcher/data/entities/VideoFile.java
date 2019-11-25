package edu.esipe.i3.ezipflix.dispatcher.data.entities;

public class VideoFile {
    private String bucket;
    private String uri;
    private String name;
    private String size;
    private String contentType;

    public VideoFile(String bucket, String uri, String name, String size, String contentType) {
        this.bucket = bucket;
        this.uri = uri;
        this.name = name;
        this.size = size;
        this.contentType = contentType;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
