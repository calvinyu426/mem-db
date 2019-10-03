import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RowData<T extends Comparable> implements Comparable<RowData> {
    protected ColumnValue<Long> id;
    protected ColumnValue<T> key;
    protected List<ColumnValue> columns;

    public RowData(List<ColumnValue> columns) {
        this.columns = columns;
    }

    public RowData(String tableName, List<ColumnValue> columns) {
        DbManager dbManager = DbManager.getInstance();
        this.id = new ColumnValue<>(new Column(Long.class, "id"), dbManager.generateId(tableName));
        this.columns = columns;
        this.key = (ColumnValue<T>) id;

        Set<Column> columnSet = columns.stream().map(ColumnValue::getColumn).collect(Collectors.toSet());
        if (!columnSet.contains(id.getColumn())) {
            columns.add(id);
        }
    }

    public void setId(String tableName) {
        DbManager dbManager = DbManager.getInstance();
        this.id = new ColumnValue<>(new Column(Long.class, "id"), dbManager.generateId(tableName));
        this.key = (ColumnValue<T>) id;

        Set<Column> columnSet = columns.stream().map(ColumnValue::getColumn).collect(Collectors.toSet());
        if (!columnSet.contains(id.getColumn())) {
            columns.add(id);
        }
    }

    public boolean updateKey(Column key) {
        Optional<ColumnValue> keyValue = this.columns.stream().filter(col -> {
            return col.getColumn().equals(key);
        }).findFirst();

        if (keyValue.isPresent()) {
            this.key = keyValue.get();
            return true;
        }
        return false;
    }

    public boolean updateValue(ColumnValue value) {
        Optional<ColumnValue> oldColumn = this.columns.stream().filter(col -> {
            return col.getColumn().equals(value.getColumn());
        }).findFirst();

        if (oldColumn.isPresent()) {
            int index = this.columns.indexOf(oldColumn.get());
            this.columns.set(index, value);
            return true;
        } else {
            return false;
        }
    }


    public ColumnValue getColumnValue(Column column) {
        Optional<ColumnValue> colValue = this.columns.stream().filter(col -> {
            return col.getColumn().equals(column);
        }).findFirst();
        return colValue.orElse(null);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id.toString() + "\r\n");
        for (ColumnValue column : columns) {
            if (column == id || "id".equalsIgnoreCase(column.getColumn().getName())) {
                continue;
            }
            sb.append(column.toString() + "\r\n");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(RowData o) {
        return this.key.compareTo(o.key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RowData<?> rowData = (RowData<?>) o;
        return Objects.equals(id, rowData.id) &&
                Objects.equals(key, rowData.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key);
    }
}

