package net.knsh.cyclic.porting.neoforge.bus.api;

import net.knsh.cyclic.porting.neoforge.bus.api.ForgeEvent;

public interface IEventClassChecker {
    static void check(Class<? extends ForgeEvent> eventClass) throws IllegalArgumentException {

    }
}
