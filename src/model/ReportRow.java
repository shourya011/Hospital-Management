package model;

public class ReportRow {
    private String label;
    private int value;

    public ReportRow(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public int getValue() { return value; }

    @Override
    public String toString() {
        return label + ": " + value;
    }
}