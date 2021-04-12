package crawler;

import jobs.DirectoryCrawlerJob;
import jobs.Job;
import util.PropertiesLoader;
import util.Stoppable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryCrawler implements Runnable, Stoppable {
    private final Map<String, Object> properties;

    private final CopyOnWriteArrayList<String> rememberedPaths;
    private final BlockingQueue<Job> jobQueue;

    private final HashMap<String, Long> lastModified = new HashMap<>();

    private volatile boolean forever = true;
    private volatile boolean sleeping = false;

    public DirectoryCrawler(CopyOnWriteArrayList<String> rememberedPaths, BlockingQueue<Job> jobQueue) {
        this.properties = PropertiesLoader.getInstance().getProperties();

        this.rememberedPaths = rememberedPaths;
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        while (this.forever) {
            try {
                sleeping = true;
                if (!this.rememberedPaths.isEmpty()) {
                    synchronized (this) {
                        wait((Long) this.properties.get("dir_crawler_sleep_time"));
                    }
                } else {
                    synchronized (this) {
                        wait(100);
                    }
                }
                sleeping = false;

                for (String rememberedPath: this.rememberedPaths) {
                    this.findCorpusAndCreateJob(rememberedPath, false);
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        System.out.println("Directory crawler shutting down");
    }

    @Override
    public void stop() {
        this.forever = false;
        jobQueue.add(new DirectoryCrawlerJob());

        if (sleeping) {
            Thread.currentThread().interrupt();
        }
    }

    public void findCorpusAndCreateJob(String path, boolean isLoggerON) {
        ArrayList<File> corpusDirectories = new ArrayList<>();

        // Check is maybe direct path is corpus
        File directory = new File(path);
        if (directory.isDirectory() && directory.getName().startsWith((String) this.properties.get("file_corpus_prefix"))) {
            corpusDirectories.add(directory);
        }

        if (isLoggerON) {
            System.out.println("Adding dir " + directory.getAbsolutePath());
        }

        // Find all corpus
        findCorpus(path, corpusDirectories);

        for (File corpusDirectory: corpusDirectories) {
            boolean valid = false;

            File[] corpusFiles = corpusDirectory.listFiles();

            if (corpusFiles != null) {
                // Check last modified time on all files in dir
                for (File corpusFile: corpusFiles) {
                    try {
                        Long newLastModifiedTime = Files.getLastModifiedTime(Path.of(corpusFile.getAbsolutePath())).toMillis();
                        Long oldLastModifiedTime = this.lastModified.get(corpusFile.getAbsolutePath());

                        this.lastModified.put(corpusFile.getAbsolutePath(), newLastModifiedTime);

                        if (!newLastModifiedTime.equals(oldLastModifiedTime)) {
                            valid = true;
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (valid) {
                jobQueue.add(new DirectoryCrawlerJob(corpusDirectory.getName(), corpusDirectory.getAbsolutePath(), isLoggerON));
            }
        }
    }

    private void findCorpus (String uri, ArrayList<File> corpusDirectories) {
        File directory = new File(uri);

        File[] dirFiles = directory.listFiles();

        if (dirFiles == null) {
            return;
        }

        for (File file: dirFiles) {
            if (file.isDirectory()) {
                if (file.getName().startsWith((String) this.properties.get("file_corpus_prefix"))) {
                    corpusDirectories.add(file);
                }

                findCorpus(file.getPath(), corpusDirectories);
            }
        }
    }
}
