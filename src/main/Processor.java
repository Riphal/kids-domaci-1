package main;

import commands.*;
import crawler.DirectoryCrawler;
import jobs.JobDispatcher;
import results.FileScannerResult;
import results.ResultRetriever;
import results.WebScannerResult;
import scanner.file.FileScanner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Processor {
    private final List<Command> commands;

    public Processor (
            ResultRetriever resultRetriever,
            DirectoryCrawler directoryCrawler,
            JobDispatcher jobDispatcher,
            FileScanner fileScanner,
            CopyOnWriteArrayList<String> directoryCrawlerPaths,
            Map<String, FileScannerResult> fileScannerResults,
            Map<String, WebScannerResult> webScannerResults,
            Map<String, Map<String, Integer>> cacheFileSummery,
            Map<String, Map<String, Integer>> cacheWebSummery
    ) {
        commands = List.of(
            new ADCommand(directoryCrawlerPaths, directoryCrawler),
            new AWCommand(),
            new GetCommand(fileScannerResults, webScannerResults, cacheFileSummery, cacheWebSummery),
            new QueryCommand(fileScannerResults, webScannerResults, cacheFileSummery, cacheWebSummery),
            new CFSCommand(cacheFileSummery),
            new CWSCommand(cacheWebSummery),
            new StopCommand(resultRetriever, directoryCrawler, jobDispatcher, fileScanner)
        );
    }

    public Command getCommand (String line) {
        Command command = new NotFoundCommand();

        String[] args = line.split("\\s+");

        for (Command c: commands) {
            if (c.getCommandName() != null && c.getCommandName().equals(args[0])) {
                command = c;

                if (args.length > 1) {
                    command.setArguments(args[1]);
                }
            }
        }

        return command;
    }
}
