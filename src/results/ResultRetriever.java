package results;

import util.Stoppable;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ResultRetriever implements Runnable, Stoppable {
    private volatile boolean forever = true;

    private final BlockingQueue<Result> resultQueue;

    private final Map<String, FileScannerResult> fileScannerResults;
    private final Map<String, WebScannerResult> webScannerResults;

    private final Map<String, Map<String, Integer>> cacheFileSummery;
    private final Map<String, Map<String, Integer>> cacheWebSummery;

    private final ResultVisitor resultVisitor;

    public ResultRetriever(
            BlockingQueue<Result> resultQueue,
            Map<String, FileScannerResult> fileScannerResults,
            Map<String, WebScannerResult> webScannerResults,
            Map<String, Map<String, Integer>> cacheFileSummery,
            Map<String, Map<String, Integer>> cacheWebSummery
    ) {
        this.resultQueue = resultQueue;

        this.fileScannerResults = fileScannerResults;
        this.webScannerResults = webScannerResults;

        this.cacheFileSummery = cacheFileSummery;
        this.cacheWebSummery = cacheWebSummery;

        this.resultVisitor = new ResultVisitor(fileScannerResults, webScannerResults);
    }

    @Override
    public void run() {
        while (this.forever) {
            try {
                Result result = this.resultQueue.take();

                if (result.isPoisonous()) {
                    break;
                }

                result.accept(resultVisitor);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Result retriever shutting down");
    }

    @Override
    public void stop() {
        this.forever = false;
    }
}
