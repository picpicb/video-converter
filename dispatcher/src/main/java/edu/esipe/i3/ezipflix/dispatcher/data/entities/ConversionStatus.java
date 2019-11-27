package edu.esipe.i3.ezipflix.dispatcher.data.entities;

public class ConversionStatus {
    private int progress;
    private String id;

    public ConversionStatus(int progress, String id) {
        this.progress = progress;
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
