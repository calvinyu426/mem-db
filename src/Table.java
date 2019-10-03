import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Table {

    private String name;
    private Set<Column> columns;
    private BinarySearchTree<RowData> tree;

    public Table(String name) {
        this.name = name;
        this.columns = new HashSet<>();
        tree = new BinarySearchTree<>();
    }

    public Table(String name, Set<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Column> getColumns() {
        return columns.stream().filter(column -> {
            if ("id".equalsIgnoreCase(column.getName())) {
                return false;
            }
            return true;
        }).collect(Collectors.toSet());
    }

    public void setColumns(Set<Column> columns) {
        this.columns = columns;
    }

    public boolean insertRow(RowData<Comparable> rowData) {
        if (rowData.columns.size() > columns.size()) {
            throw new DbException("The columns values size is inconstant with the number of table columns.");
        }
        for (ColumnValue column : rowData.columns) {
            if (!columns.contains(column.getColumn())) {
                throw new DbException("The columnValue: " + column + " is unsupported.");
            }
        }

        TreeNode<RowData> newNode = tree.insertNode(tree.root, rowData);
        return null != newNode;
    }

    public boolean deleteRow(long id) {
        ColumnValue<Long> idColumn = new ColumnValue<>(new Column(Long.class, "id"), id);
        return tree.deleteNode(tree.root, idColumn, null);
    }

    public boolean updateRow(long id, RowData<Comparable> rowData) {
        ColumnValue<Long> idColumn = new ColumnValue<>(new Column(Long.class, "id"), id);
        RowData foundData = tree.findNode(tree.root, idColumn);
        if (null != foundData) {
            return false;
        }

        for (ColumnValue newCol : rowData.columns) {
            int index = foundData.columns.indexOf(newCol);
            if (index >= 0) {
                foundData.columns.set(index, newCol);
            }
        }
        return true;
    }

    public long getCount() {
        return tree.size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Table table = (Table) o;
        return Objects.equals(name, table.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
