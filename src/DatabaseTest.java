import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class DatabaseTest {

    private final Column nameCol = new Column(String.class, "name");
    private final Column isinCol = new Column(String.class, "isin");
    private final Column yearCol = new Column(Integer.class, "year");
    private final Column priceCol = new Column(Double.class, "price");
    private DbManager dbManager;
    private String dbName = "db";
    private String bookTable = "Books";

    @Before
    public void setUp() {
        dbManager = DbManager.getInstance();
        createDB(dbName);

        String tableName = bookTable;
        Set<Column> columnSet = new HashSet<>(4);
        columnSet.add(nameCol);
        columnSet.add(isinCol);
        columnSet.add(yearCol);
        columnSet.add(priceCol);

        createTable(tableName, columnSet);
    }

    @After
    public void clear() {
        dbManager.idMap.clear();
        dbManager.dataBaseMap.get(dbName).tableMap.clear();
        dbManager.dataBaseMap.clear();
    }


    @Test
    public void createDB() {
        createDB("db2");
        createDB("Db3");
        createDB("DataBase");
    }

    @Test
    public void createTable() {

        String tableName = "Students";
        Set<Column> columnSet = new HashSet<>(4);
        columnSet.clear();
        columnSet.add(new Column(String.class, "name"));
        columnSet.add(new Column(String.class, "sex"));
        columnSet.add(new Column(Integer.class, "age"));
        columnSet.add(new Column(Date.class, "birth_date"));

        createTable(tableName, columnSet);
    }


    @Test
    public void insert() {

        for (int i = 0; i < 100; i++) {
            List<ColumnValue> columnValues = new ArrayList<>(4);
            columnValues.add(new ColumnValue(nameCol, "Book" + i));
            columnValues.add(new ColumnValue(isinCol, "KN00" + i));
            columnValues.add(new ColumnValue(yearCol, 1900 + i));
            columnValues.add(new ColumnValue(priceCol, 20.0 + i));
            insert(dbName, bookTable, columnValues);
        }
    }

    @Test
    public void delete() {
        insert();

        for (int i = 1; i <= 100; i++) {
            delete(dbName, bookTable, i);
        }
    }

    @Test
    public void update() {
        insert();

        List<ColumnValue> columnValues = new ArrayList<>(4);
        columnValues.add(new ColumnValue(nameCol, "Book0"));
        columnValues.add(new ColumnValue(isinCol, "KN000"));
        columnValues.add(new ColumnValue(yearCol, 1900));
        columnValues.add(new ColumnValue(priceCol, 20.0));

        for (int i = 1; i <= 100; i++) {
            update(dbName, bookTable, i, columnValues);
        }

    }

    @Test
    public void sort() {
        insert();

        Table table = dbManager.getTable(dbName, bookTable);
        List<RowData> dataList = table.sort("id", 50);

        Assert.assertEquals(50, dataList.size());
        for (int i = 0; i < 50; i++) {
            RowData rowData = dataList.get(i);
            Assert.assertEquals(i + 1L, rowData.id.getValue());
            Assert.assertEquals(i + 1L, rowData.key.getValue());
            Assert.assertEquals("Book" + i, rowData.getColumnValue(nameCol).getValue());
            Assert.assertEquals("KN00" + i, rowData.getColumnValue(isinCol).getValue());
            Assert.assertEquals(1900 + i, rowData.getColumnValue(yearCol).getValue());
            Assert.assertEquals(20.0 + i, rowData.getColumnValue(priceCol).getValue());
        }
    }

    @Test
    public void testGroup() {
        Table table = dbManager.getTable(dbName, bookTable);

        insert();
        Map<String, Integer> result = table.group("name");
        for (int i = 0; i < 100; i++) {
            String key = "Book" + i;
            Assert.assertEquals(Integer.valueOf(1), result.get(key));
        }

        insert();
        result = table.group("name");
        for (int i = 0; i < 100; i++) {
            String key = "Book" + i;
            Assert.assertEquals(Integer.valueOf(2), result.get(key));
        }
    }


    private void createDB(String dbName) {
        if (!dbManager.exists(dbName)) {
            boolean result = dbManager.createDatabase(dbName);
            Assert.assertTrue(result);
            Assert.assertTrue(dbManager.exists(dbName));
        }
    }

    private void createTable(String tableName, Set<Column> columnSet) {
        if (!dbManager.dataBaseMap.get(dbName).exists(tableName)) {
            boolean result = dbManager.createTable(dbName, tableName, columnSet);
            Assert.assertTrue(result);

            Table table = dbManager.getTable(dbName, tableName);
            Assert.assertEquals(tableName.toLowerCase(), table.getName());
        }
    }

    private void insert(String dbName, String tableName, List<ColumnValue> columnValues) {
        RowData rowData = new RowData<>(columnValues);
        Table table = dbManager.getTable(dbName, tableName);
        long beforeCount = table.getCount();

        boolean result = table.insertRow(rowData);
        Assert.assertTrue(result);

        long afterCount = table.getCount();
        Assert.assertEquals(beforeCount + 1, afterCount);

    }

    private void delete(String dbName, String tableName, long id) {
        Table table = dbManager.getTable(dbName, tableName);
        long beforeCount = table.getCount();

        boolean result = table.deleteRow(id);
        Assert.assertTrue(result);

        long afterCount = table.getCount();
        Assert.assertEquals(beforeCount - 1, afterCount);

    }

    private void update(String dbName, String tableName, long id, List<ColumnValue> columnValues) {
        RowData rowData = new RowData<>(new ArrayList<>(columnValues));
        Table table = dbManager.getTable(dbName, tableName);
        long beforeCount = table.getCount();

        boolean result = table.updateRow(id, rowData);
        Assert.assertTrue(result);

        long afterCount = table.getCount();
        Assert.assertEquals(beforeCount, afterCount);

        rowData.id = rowData.key = new ColumnValue(new Column(Long.class, "id"), id);
        rowData.columns.add(rowData.id);

        List<RowData> dataList = table.getAll();
        int index = dataList.indexOf(rowData);
        Assert.assertTrue(index >= 0);

        for (ColumnValue newCol : columnValues) {
            ColumnValue updatedCol = rowData.getColumnValue(newCol.getColumn());
            Assert.assertEquals(newCol, updatedCol);
        }
    }
}