import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Database {

    private String name;
    private Map<String, Table> tableMap;

    public Database(String name) {
        this.name = name;
        this.tableMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Table> getTableMap() {
        return tableMap;
    }


    public boolean createTable(String tableName, Set<Column> columnSet) {
        Table table = tableMap.get(tableName);
        if (null != table) {
            throw new DbException("Table: " + tableName + " has been already xists.");
        }

        table = new Table(tableName, columnSet);
        tableMap.put(tableName, table);
        return false;
    }

    public Table getTable(String tableName) {
        Table table = tableMap.get(tableName);
        if (null == table) {
            throw new DbException("Table: " + tableName + " not exists.");
        }
        return table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Database dataBase = (Database) o;
        return Objects.equals(name, dataBase.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
