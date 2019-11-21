package edu.esipe.i3.ezipflix.frontend.data.entities;

public class VideoFile {
    private String id;
    private String name;
    private String size;
    private String contentType;

    public VideoFile(String id, String name, String size, String contentType) {
        this.id = id;
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
}
