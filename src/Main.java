import java.text.ParseException;
import java.util.*;

public class Main {

    private static DbManager dbManager;
    private static Scanner scanner;
    private static Set<String> supportedTypes;

    static {
        dbManager = DbManager.getInstance();
        scanner = new Scanner(System.in);
        supportedTypes = new HashSet<>();
        supportedTypes.add(String.class.getSimpleName());
    }

    public static void main(String[] args) {

        while (true) {
            menu();

            System.out.println();
            System.out.print("Please choice:");
            int choice = scanner.nextInt();
            scanner.nextLine();

            execute(choice);
        }
    }

    private static void execute(int choice) {
        try {
            switch (choice) {
                case 1:
                    System.out.print("Please enter the database name: ");
                    String dbName = scanner.nextLine();
                    boolean result = dbManager.createDatabase(dbName);
                    if (result) {
                        System.out.println("Database: " + dbName + " has been created.");
                    }
                    break;
                case 2:
                    System.out.print("Please enter the database name: ");
                    dbName = scanner.nextLine();
                    System.out.print("Enter table name: ");
                    String tableName = scanner.nextLine();
                    createTable(dbName, tableName);
                    System.out.println("Table: " + tableName + " has been created.");
                    break;
                case 3:
                    System.out.print("Please enter the database name: ");
                    dbName = scanner.nextLine();
                    System.out.print("Enter table name: ");
                    tableName = scanner.nextLine();
                    insert(dbName, tableName);
                    System.out.println("Insert successfully.");
                    break;
                case 4:
                    System.out.print("Please enter the database name: ");
                    dbName = scanner.nextLine();
                    System.out.print("Enter table name: ");
                    tableName = scanner.nextLine();
                    delete(dbName, tableName);
                    break;
                case 5:
                    System.out.print("Please enter the database name: ");
                    dbName = scanner.nextLine();
                    System.out.print("Enter table name: ");
                    tableName = scanner.nextLine();
                    update(dbName, tableName);
                    break;
                case 10:
                    System.exit(0);
                    return;
                default:
                    break;

            }
        } catch (DbException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(String dbName, String tableName) {
        Set<Column> columnSet = new HashSet<>();
        System.out.println("Enter the columns(format: type=name), input exit to finish");
        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            Column column = parseColumn(input);
            if (null == column) {
                System.out.println("Invalid column input.");
            } else if (columnSet.contains(column)) {
                System.out.println("Duplicate column: " + input);
            } else {
                columnSet.add(column);
            }
        }

        dbManager.createTable(dbName, tableName, null);
    }

    private static void insert(String dbName, String tableName) throws ParseException {
        Table table = dbManager.getTable(dbName, tableName);
        int columnSize = table.getColumns().size();
        List<ColumnValue> columns = new ArrayList<>(columnSize);

        for (Column column : table.getColumns()) {
            System.out.print(String.format("Enter column value(type=%s,name=%s): ", column.getType().getSimpleName(), column.getName()));
            String value = scanner.nextLine();

            ColumnValue columnValue = new ColumnValue(column, DbManager.convert(column.getType(), value));
            columns.add(columnValue);
        }

        RowData<Comparable> rowData = new RowData<>(tableName, columns);
        table.insertRow(rowData);
    }

    private static void delete(String dbName, String tableName) {
        Table table = dbManager.getTable(dbName, tableName);
        System.out.print("Enter the record id: ");
        long id = Long.parseLong(scanner.nextLine());
        boolean result = table.deleteRow(id);
        if (result) {
            System.out.println("Delete successfully!");
        } else {
            System.out.println("Delete failed!");
        }
    }

    private static void update(String dbName, String tableName) throws ParseException {
        System.out.print("Enter the record id: ");
        long id = Long.parseLong(scanner.nextLine());

        Table table = dbManager.getTable(dbName, tableName);
        int columnSize = table.getColumns().size();
        List<ColumnValue> columns = new ArrayList<>(columnSize);

        for (Column column : table.getColumns()) {
            System.out.print(String.format("Enter column value(type=%s,name=%s): ", column.getType().getSimpleName(), column.getName()));
            String value = scanner.nextLine();

            ColumnValue columnValue = new ColumnValue(column, DbManager.convert(column.getType(), value));
            columns.add(columnValue);
        }

        RowData<Comparable> rowData = new RowData<>(tableName, columns);
        boolean result = table.updateRow(id, rowData);
        if (result) {
            System.out.println("Update successfully!");
        } else {
            System.out.println("Update failed!");
        }
    }

    private static void menu() {
        System.out.println();

        System.out.println("1-Create a database.");
        System.out.println("2-Create a table.");
        System.out.println("3-Insert row.");
        System.out.println("4-Delete Row.");
        System.out.println("5-Update Row.");
        System.out.println("6-Get All rows from a table.");
        System.out.println("7-Get specific count of rows from a table.");
        System.out.println("8-SORT by a column,and GET specific count of rows from table.");
        System.out.println("9-GroupBy a column, and GET specific count of rows from table. This is the aggregate function.");
        System.out.println("10-Quit.");
    }

    private static Column parseColumn(String input) {
        String[] split = input.split("=");
        if (2 != split.length) {
            return null;
        }

        Class type = DbManager.parseType(split[0]);
        String name = split[1];

        if (null == type) {
            return null;
        }
        return new Column(type, name);

    }


}
