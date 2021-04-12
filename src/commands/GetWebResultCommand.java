package commands;

import results.WebScannerResult;

import java.util.Map;

public class GetWebResultCommand implements Command {
    private String args;

    private final Map<String, WebScannerResult> webScannerResults;
    private final Map<String, Map<String, Integer>> cacheWebSummery;

    public GetWebResultCommand (
            Map<String, WebScannerResult> webScannerResults,
            Map<String, Map<String, Integer>> cacheWebSummery
    ) {
        this.webScannerResults = webScannerResults;
        this.cacheWebSummery = cacheWebSummery;
    }

    @Override
    public void execute() {
        System.out.println("Not implemented!");
    }

    @Override
    public void setArguments(String args) {
        this.args = args;
    }

    @Override
    public String getCommandName() {
        return "web";
    }

    @Override
    public boolean isFatal() {
        return false;
    }
}
