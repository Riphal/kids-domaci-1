package jobs;

import util.Logger;
import util.Poisonable;

public interface Job extends Poisonable, Logger {
    public void accept(Visitor jobVisitor);
}
