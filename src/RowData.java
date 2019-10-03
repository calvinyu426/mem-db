import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RowData<T extends Comparable> implements Comparable<RowData> {
    protected ColumnValue<Long> id;
    protected ColumnValue<T> key;
    protected List<ColumnValue> columns;

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

    public RowData(String tableName, ColumnValue<T> key, List<ColumnValue> columns) {
        DbManager dbManager = DbManager.getInstance();
        this.id = new ColumnValue<>(new Column(Long.class, "id"), dbManager.generateId(tableName));
        this.key = key;
        this.columns = columns;

        if (null == key) {
            this.key = (ColumnValue<T>) id;
        }

        Set<Column> columnSet = columns.stream().map(ColumnValue::getColumn).collect(Collectors.toSet());
        if (!columnSet.contains(id.getColumn())) {
            columns.add(id);
        }
    }


    @Override
    public int compareTo(RowData o) {
        return this.key.compareTo(o.key);
    }
}

