package commands;

import results.FileScannerResult;
import results.WebScannerResult;

import java.util.List;
import java.util.Map;

public class GetCommand implements Command {
    private String args;

    private final List<Command> subCommands;

    public GetCommand (
            Map<String, FileScannerResult> fileScannerResults,
            Map<String, WebScannerResult> webScannerResults,
            Map<String, Map<String, Integer>> cacheFileSummery,
            Map<String, Map<String, Integer>> cacheWebSummery
    ) {
        this.subCommands = List.of(
            new GetFileResultCommand(fileScannerResults, cacheFileSummery),
            new GetWebResultCommand(webScannerResults, cacheWebSummery)
        );
    }

    @Override
    public void execute() {
        String[] argsList = this.args.split("\\|");

        if (argsList.length < 2) {
            System.out.println("Wrong command params");
            return;
        }

        Command subCommand = new NotFoundCommand();
        for (Command c: subCommands) {
            if (c.getCommandName() != null && c.getCommandName().equals(argsList[0])) {
                subCommand = c;
                subCommand.setArguments(argsList[1]);
            }
        }

        subCommand.execute();
    }

    @Override
    public void setArguments(String args) {
        this.args = args;
    }

    @Override
    public String getCommandName() {
        return "get";
    }

    @Override
    public boolean isFatal() {
        return false;
    }
}
