package jobs;

import util.Stoppable;

import java.util.concurrent.BlockingQueue;

public class JobDispatcher implements Runnable, Stoppable {
    private volatile boolean forever = true;

    private final BlockingQueue<Job> jobQueue;

    private final JobVisitor jobVisitor;

    public JobDispatcher (
            BlockingQueue<Job> jobQueue,
            BlockingQueue<DirectoryCrawlerJob> fileScannerJobQueue,
            BlockingQueue<WebCrawlerJob> webScannerJobQueue
    ) {
        this.jobQueue = jobQueue;

        this.jobVisitor = new JobVisitor(fileScannerJobQueue, webScannerJobQueue);
    }

    @Override
    public void run() {
        while (this.forever) {
            try {
                Job job = this.jobQueue.take();

                if (job.isPoisonous()) {
                    break;
                }

                job.accept(this.jobVisitor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Job dispatcher shutting down");
    }

    @Override
    public void stop() {
        this.forever = false;

        new DirectoryCrawlerJob().accept(this.jobVisitor);
        new WebCrawlerJob().accept(this.jobVisitor);
    }
}
