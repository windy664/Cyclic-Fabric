package net.knsh.cyclic.util.forgeport;

public class FluidFabricToForge  {
    public static long toMiliBuckets(long droplets) {
        return Math.round((float) droplets / 81);
    }

    public static long toDroplets(long miliBuckets) {
        return miliBuckets * 81;
    }
}
