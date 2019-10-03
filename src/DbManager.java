import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DbManager {

    protected Map<String, Database> dataBaseMap;
    protected Map<String, Long> idMap = new HashMap<>();

    private DbManager() {
        dataBaseMap = new HashMap<>();
    }

    public static DbManager getInstance() {
        return InstanceHolder.instance;
    }

    public static Class parseType(String type) {
        if ("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type) || "smallint".equalsIgnoreCase(type)) {
            return Integer.class;
        }
        if ("long".equalsIgnoreCase(type) || "bigint".equalsIgnoreCase(type)) {
            return Long.class;
        }
        if ("tinyint".equalsIgnoreCase(type)) {
            return Boolean.class;
        }
        if ("varchar".equalsIgnoreCase(type) || "char".equalsIgnoreCase(type) || "text".equalsIgnoreCase(type)) {
            return String.class;
        }
        if ("datetime".equalsIgnoreCase(type) || "date".equalsIgnoreCase(type) || "timestamp".equalsIgnoreCase(type)) {
            return Date.class;
        }
        if ("float".equalsIgnoreCase(type) || "decimal".equalsIgnoreCase(type) || "double".equalsIgnoreCase(type)) {
            return Double.class;
        }

        return null;
    }

    public static void setDefaultValueIfNUll(ColumnValue value) {
        Class type = value.getColumn().getType();
        if (null == value.getValue() || "NULL".equalsIgnoreCase(value.getValue().toString())) {
            if (type == Integer.class || type == Double.class || type == Long.class) {
                value.setValue(0);
            } else if (type == Boolean.class) {
                value.setValue(Boolean.valueOf(false));
            } else if (type == Date.class) {
                value.setValue(new Date());
            } else if (type == String.class) {
                value.setValue("");
            }
        }
    }

    public static <T extends Comparable> T convert(Class<T> type, String value) throws ParseException {
        if (type == Integer.class) {
            return (T) Integer.valueOf(value);
        }
        if (type == Double.class) {
            return (T) Double.valueOf(value);
        }
        if (type == Long.class) {
            return (T) Long.valueOf(value);
        }
        if (type == Boolean.class) {
            return (T) Boolean.valueOf(value);
        }
        if (type == Date.class) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return (T) sdf.parse(value);
        }
        return (T) value;
    }

    public boolean createDatabase(String dbName) {
        if (exists(dbName)) {
            throw new DbException("Database: " + dbName + " has been already exists.");
        }

        dataBaseMap.put(dbName.toLowerCase(), new Database(dbName));
        return true;
    }

    public boolean exists(String dbName) {
        if (null != dataBaseMap.get(dbName.toLowerCase())) {
            return true;
        }
        return false;
    }

    public boolean createTable(String dbName, String tableName, Set<Column> columnSet) {
        Database database = dataBaseMap.get(dbName.toLowerCase());
        if (null == database) {
            throw new DbException("Database: " + dbName + " dose not exists.");
        }

        return database.createTable(tableName, columnSet);
    }

    public Table getTable(String dbName, String tableName) {
        Database database = dataBaseMap.get(dbName);
        if (null == database) {
            throw new DbException("Database: " + dbName + " dose not exists.");
        }

        return database.getTable(tableName);
    }

    protected long generateId(String tableName) {
        Long currentId = idMap.getOrDefault(tableName, 0L);
        long nextId = currentId + 1;

        idMap.put(tableName, nextId);
        return nextId;
    }

    private static class InstanceHolder {
        private static DbManager instance;

        static {
            instance = new DbManager();
        }
    }


}
