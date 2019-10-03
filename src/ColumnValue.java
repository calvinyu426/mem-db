import java.util.Objects;

public class ColumnValue<T extends Comparable> implements Comparable<ColumnValue> {

    private Column column;

    private T value;

    public ColumnValue(Column column, T value) {
        this.column = column;
        this.value = value;
    }

    public Column getColumn() {
        return column;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }


    @Override
    public int compareTo(ColumnValue o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return "column=" + column + ", value=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColumnValue<?> that = (ColumnValue<?>) o;
        return Objects.equals(column, that.column) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, value);
    }
}
