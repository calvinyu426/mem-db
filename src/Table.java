import java.util.*;
import java.util.stream.Collectors;

public class Table {

    private String name;
    private Set<Column> columns;
    private BinarySearchTree<RowData> tree;

    public Table(String name, Set<Column> columns) {
        this.name = name.toLowerCase();
        this.columns = columns;
        this.tree = new BinarySearchTree<>();

        Column id = new Column(Long.class, "id");
        if (!columns.contains(id)) {
            this.columns.add(id);
            DbManager.getInstance().idMap.put(name, 0L);
        }
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
        rowData.setId(this.name);
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
        return tree.deleteNode(idColumn);
    }

    public boolean updateRow(long id, RowData<Comparable> rowData) {
        ColumnValue<Long> idColumn = new ColumnValue<>(new Column(Long.class, "id"), id);
        RowData foundData = tree.findNode(tree.root, idColumn);
        if (null == foundData) {
            return false;
        }

        for (ColumnValue newCol : rowData.columns) {
            for (int i = 0; i < foundData.columns.size(); i++) {
                ColumnValue currCol = (ColumnValue) foundData.columns.get(i);
                if (currCol.getColumn().equals(newCol.getColumn())) {
                    foundData.columns.set(i, newCol);
                }
            }
        }
        return true;
    }

    public List<RowData> getAll() {
        List<RowData> dataList = new ArrayList<>();
        tree.inOrder(tree.root, dataList);
        return dataList;
    }

    public List<RowData> getWithCount(int count) {
        List<RowData> dataList = new ArrayList<>(count);
        tree.inOrder(tree.root, dataList, count);

        if (dataList.size() > count) {
            return dataList.subList(0, count);
        }
        return dataList;
    }

    public List<RowData> sort(String column, int count) {
        Column sorteColumn = this.columns.stream().filter(col -> {
            return col.getName().equalsIgnoreCase(column);
        }).findFirst().get();

        List<RowData> dataList = getAll();
        dataList.forEach(row -> row.updateKey(sorteColumn));

        BinarySearchTree<RowData> newTree = new BinarySearchTree<>();
        newTree.root = newTree.generateBST(dataList);
        tree = newTree;

        return getWithCount(count);
    }

    public Map<String, Integer> group(String groupBy) {
        Column groupByColumn = this.columns.stream().filter(col -> {
            return col.getName().equalsIgnoreCase(groupBy);
        }).findFirst().get();

        List<RowData> dataList = getAll();
        Map<ColumnValue, Long> countMap = dataList.stream()
                .collect(Collectors.groupingBy(rowData -> rowData.getColumnValue(groupByColumn), Collectors.counting()));

        return countMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getValue().toString(), entry -> entry.getValue().intValue()));
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
