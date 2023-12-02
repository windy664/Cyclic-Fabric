package net.knsh.cyclic.block.battery;

import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class CapabilityProviderEnergyStack {
    EnergyStorage energy;

    public CapabilityProviderEnergyStack(int max) {
        energy = new SimpleEnergyStorage(max, max, max);
    }
}
