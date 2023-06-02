package datasets;

/**
 * util enum that simplifies creating tables, inserting data etc. for various datasets.
 */
public enum ColumnType {
    TIMESTAMP,
    NUMERIC,
    STRING
    // it should be enough -I believe we can flatten every column into this small set, we do not care about most of the values anyway
}

