package net.azisaba.rcitemlogging.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ConditionChainer {
    List<Supplier<Boolean>> conditions = new ArrayList<>();
    private Runnable finallyFunc;
    public ConditionChainer() {}

    public ConditionChainer then(Supplier<Boolean> conditionFunc) {
        conditions.add(conditionFunc);
        return this;
    }

    /**
     * CAUTION: this method overwrites previous finallyFunc.
     * @param finallyFunc Runnable, which runs at last
     * @return this
     */
    public ConditionChainer atLast(Runnable finallyFunc) {
        this.finallyFunc = finallyFunc;
        return this;
    }

    public void run() {
        for(var f: conditions) {
            if(!f.get()) {
                break;
            }
        }
        if(finallyFunc != null) finallyFunc.run();
    }
}
