package pxl.be.clockie;

/**
 * Created by Titaanje-Laptop on 24/10/2017.
 */

public class Weather {
    public int id;
    public String main;
    public String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {

        final StringBuffer sb = new StringBuffer("weather{");
        sb.append("id=").append(id);
        sb.append(",main=").append(main).append('\'');
        sb.append(",description=").append(description).append('\'');
        sb.append('}');

        return sb.toString();

    }
}
