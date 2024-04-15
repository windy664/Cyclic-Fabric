package com.lothrazar.cyclic.block.beaconpotion;

import com.google.common.collect.Lists;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import java.util.ArrayList;
import java.util.List;

public class BeamParams {
    public List<BeaconBeamSection> beamSections;
    public List<BeaconBeamSection> checkingBeamSections;
    public int lastCheckY;

    public BeamParams() {
        this(Lists.newArrayList(), Lists.newArrayList(), 0);
    }

    private BeamParams(ArrayList<BeaconBeamSection> newArrayList, ArrayList<BeaconBeamSection> newArrayList2, int i) {
        this.beamSections = newArrayList;
        this.checkingBeamSections = newArrayList2;
        this.lastCheckY = i;
    }
}
