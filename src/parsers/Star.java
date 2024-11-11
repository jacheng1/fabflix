package parsers;

import java.util.ArrayList;

public class Star{
    private String name;
    private int birthyear;

    public Star() {
    }

    public Star(String name, int birthyear) {
        this.name = name;
        this.birthyear = birthyear;
    }

    public int getDOB() {
        return birthyear;
    }

    public void setDOB(int birthyear) {
        this.birthyear = birthyear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("Name:" + getName());
        sb.append(", ");
        sb.append("DOB:" + getDOB());

        return sb.toString();
    }
}
