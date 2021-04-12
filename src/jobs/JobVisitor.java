package jobs;

import java.util.concurrent.BlockingQueue;

public class JobVisitor implements Visitor {
    private final BlockingQueue<DirectoryCrawlerJob> fileScannerJobQueue;
    private final BlockingQueue<WebCrawlerJob> webScannerJobQueue;

    public JobVisitor (
            BlockingQueue<DirectoryCrawlerJob> fileScannerJobQueue,
            BlockingQueue<WebCrawlerJob> webScannerJobQueue
    ) {
        this.fileScannerJobQueue = fileScannerJobQueue;
        this.webScannerJobQueue = webScannerJobQueue;
    }

    @Override
    public void dispatch(DirectoryCrawlerJob directoryCrawlerJob) {
        fileScannerJobQueue.add(directoryCrawlerJob);
    }

    @Override
    public void dispatch(WebCrawlerJob webCrawlerJob) {
        webScannerJobQueue.add(webCrawlerJob);
    }
}
