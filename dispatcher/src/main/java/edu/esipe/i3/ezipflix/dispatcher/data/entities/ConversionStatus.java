package edu.esipe.i3.ezipflix.dispatcher.data.entities;

public class ConversionStatus {
    private int percentage;
    private String id;

    public ConversionStatus(int percentage, String uuid) {
        this.percentage = percentage;
        this.id = uuid;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getUuid() {
        return id;
    }

    public void setUuid(String uuid) {
        this.id = uuid;
    }
}
