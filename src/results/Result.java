package results;

import util.Logger;
import util.Poisonable;

import java.util.Map;

public interface Result extends Poisonable, Logger {
    public void accept(Visitor resultVisitor);
    public Map<String, Integer> getResult();
    public Map<String, Integer> queryResult();
}
