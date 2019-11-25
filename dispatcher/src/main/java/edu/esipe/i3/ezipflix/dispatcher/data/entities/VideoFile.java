package edu.esipe.i3.ezipflix.dispatcher.data.entities;

public class VideoFile {
    private String id;
    private String uri;
    private String name;
    private String size;
    private String contentType;

    public VideoFile(String id, String uri, String name, String size, String contentType) {
        this.id = id;
        this.uri = uri;
        this.name = name;
        this.size = size;
        this.contentType = contentType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
