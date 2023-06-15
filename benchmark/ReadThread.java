public class ReadThread  extends Thread{

    Database db;
    String[] queries;

    public ReadThread(Database db ,String[] queries){
        super();
        this.db = db;
        this.queries = queries;
    }

    public void run(){
        for (int i=0; i<queries.length; i++) {
            long start = System.nanoTime();
            db.runQuery(queries[i]);
            long finish = System.nanoTime();
            long elapsedTime = finish - start;
            //TODO: log time and return
        }
    }
}
