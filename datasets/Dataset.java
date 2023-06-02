package datasets;



import java.util.List;
import java.util.Map;

public interface Dataset {
    String getTableName();
    List<Map.Entry<String, ColumnType>> getColumnNamesWithTypes();
    String getCsvName();
    String getTimeStampColumnName();

}
