package pxl.be.clockie;

public enum DayOfTheWeek {
    MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6), SATURDAY(7), SUNDAY(1);

    private int value;

    DayOfTheWeek(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
