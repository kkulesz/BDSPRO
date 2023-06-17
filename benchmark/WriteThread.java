public class WriteThread extends Thread {

    private int writeFrequency;
    private Database db;
    private String[] queries;

    public WriteThread(int writeFrequency, Database db, String[]  queries) {
        super();
        this.db = db;
        this.writeFrequency = writeFrequency;
        this.queries = queries;
    }
    public void run(){
        for (int i=0; i<queries.length; i++) {
            long start = System.nanoTime();
            db.runStatement(queries[i]);
            long finish = System.nanoTime();
            long elapsedTime = finish - start;
            //TODO: log time and return
            try {
                Thread.sleep(writeFrequency);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
