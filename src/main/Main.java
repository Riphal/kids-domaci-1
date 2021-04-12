package main;

import commands.Command;
import crawler.DirectoryCrawler;
import jobs.DirectoryCrawlerJob;
import jobs.Job;
import jobs.JobDispatcher;
import jobs.WebCrawlerJob;
import results.FileScannerResult;
import results.Result;
import results.ResultRetriever;
import results.WebScannerResult;
import scanner.file.FileScanner;
import util.PropertiesLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private static final CopyOnWriteArrayList<String> directoryCrawlerPaths = new CopyOnWriteArrayList<>();

    private static final BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();

    private static final BlockingQueue<DirectoryCrawlerJob> fileScannerJobQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<WebCrawlerJob> webScannerJobQueue = new LinkedBlockingQueue<>();

    private static final Map<String, FileScannerResult> fileScannerResults = new HashMap<>();
    private static final Map<String, WebScannerResult> webScannerResults = new HashMap<>();

    private static final Map<String, Map<String, Integer>> cacheFileSummery = new HashMap<>();
    private static final Map<String, Map<String, Integer>> cacheWebSummery = new HashMap<>();

    private static ResultRetriever resultRetriever;
    private static DirectoryCrawler directoryCrawler;
    private static JobDispatcher jobDispatcher;
    private static FileScanner fileScanner;

    public static void main (String[] args) {
        PropertiesLoader.getInstance();

        initResultRetriever();
        initDirectoryCrawler();
        initJobDispatcher();
        initFileScanner();

        forever();
    }

    private static void initResultRetriever() {
        resultRetriever = new ResultRetriever(
                resultQueue,
                fileScannerResults,
                webScannerResults,
                cacheFileSummery,
                cacheWebSummery
        );
        Thread thread = new Thread(resultRetriever, "ResultRetriever");
        thread.start();
    }

    private static void initDirectoryCrawler () {
        directoryCrawler = new DirectoryCrawler(directoryCrawlerPaths, jobQueue);
        Thread thread = new Thread(directoryCrawler, "DirectoryCrawler");
        thread.start();
    }

    private static void initJobDispatcher() {
        jobDispatcher = new JobDispatcher(jobQueue, fileScannerJobQueue, webScannerJobQueue);
        Thread thread = new Thread(jobDispatcher, "JobDispatcher");
        thread.start();
    }

    private static void initFileScanner() {
        fileScanner = new FileScanner(fileScannerJobQueue, resultQueue);
        Thread thread = new Thread(fileScanner, "FileScanner");
        thread.start();
    }

    private static void forever () {
        Scanner sc = new Scanner(System.in);

        Processor processor = new Processor(
                resultRetriever,
                directoryCrawler,
                jobDispatcher,
                fileScanner,
                directoryCrawlerPaths,
                fileScannerResults,
                webScannerResults,
                cacheFileSummery,
                cacheWebSummery
        );

        while (true) {
            String args = sc.nextLine();

            Command command = processor.getCommand(args);
            command.execute();

            if (command.isFatal()) {
                break;
            }
        }

        sc.close();
        System.out.println("Main shutting down");
    }
}
