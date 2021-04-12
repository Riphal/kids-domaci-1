package commands;

public class AWCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Not implemented!");
    }

    @Override
    public void setArguments(String args) {
    }

    @Override
    public String getCommandName() {
        return null;
    }

    @Override
    public boolean isFatal() {
        return false;
    }
}
