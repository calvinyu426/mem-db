import java.util.Objects;

public class Column {

    private Class type;

    private String name;


    public Column(Class type, String name) {
        this.type = type;
        this.name = name;
    }


    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Column column = (Column) o;
        return Objects.equals(type, column.type) &&
                Objects.equals(name, column.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return "type=" + type.getSimpleName() + ", name='" + name;
    }
}
