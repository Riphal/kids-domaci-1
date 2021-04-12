package scanner.file;

import jobs.DirectoryCrawlerJob;
import results.FileScannerResult;
import results.Result;
import util.PropertiesLoader;
import util.Stoppable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FileScanner implements Runnable, Stoppable {
    private final Map<String, Object> properties;

    private final BlockingQueue<DirectoryCrawlerJob> fileScannerJobQueue;
    private final BlockingQueue<Result> resultQueue;

    private final ExecutorService threadPool;
    private final ExecutorCompletionService<Map<String, Integer>> completionService;

    private volatile boolean forever = true;

    public FileScanner (BlockingQueue<DirectoryCrawlerJob> fileScannerJobQueue, BlockingQueue<Result> resultQueue) {
        this.properties = PropertiesLoader.getInstance().getProperties();

        this.fileScannerJobQueue = fileScannerJobQueue;
        this.resultQueue = resultQueue;

        this.threadPool = Executors.newCachedThreadPool();
        this.completionService = new ExecutorCompletionService<>(this.threadPool);
    }

    @Override
    public void run() {
        while (this.forever) {
            try {
                DirectoryCrawlerJob crawlerJob = this.fileScannerJobQueue.take();

                if (crawlerJob.isPoisonous()) {
                    break;
                }

                divideWork(crawlerJob.getCorpusName(), crawlerJob.getAbsolutePath(), crawlerJob.isLoggerON());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.threadPool.shutdownNow();

        System.out.println("File scanner shutting down");
    }

    @Override
    public void stop() {
        this.forever = false;

        resultQueue.add(new FileScannerResult());
    }

    private void divideWork(String corpusName, String absolutePath, boolean loggerON) {
        List<File> files = new ArrayList<>();
        List<Future<Map<String, Integer>>> results = new ArrayList<>();

        if (loggerON) {
            System.out.println("Starting file scan for file|" + corpusName);
        }

        File[] childrenFiles = new File(absolutePath).listFiles();

        long fileScanningSizeLimit = (Integer) this.properties.get("file_scanning_size_limit");
        long currentSize = 0;

        if (childrenFiles != null) {

            for (File f : childrenFiles) {
                currentSize += f.length();
                files.add(f);

                if (currentSize > fileScanningSizeLimit) {
                    doWork(files, results);

                    currentSize = 0;
                    files.clear();
                }
            }

            if (!files.isEmpty()) {
                doWork(files, results);
            }

            FileScannerResult fileScannerResult = new FileScannerResult(corpusName, results, loggerON);
            resultQueue.add(fileScannerResult);
        }
    }

    private void doWork (List<File> files, List<Future<Map<String, Integer>>> results) {
        List<String> searchKeywords = (List<String>) properties.get("keywords");

        FileScannerWorker fileScannerWorker = new FileScannerWorker(searchKeywords, files);
        Future<Map<String, Integer>> result = this.completionService.submit(fileScannerWorker);

        results.add(result);
    }
}
