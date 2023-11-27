package net.knsh.cyclic.library.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class ConfigTemplate {
    public CommentedFileConfig setup(final String modid) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(FabricLoader.getInstance().getConfigDir().resolve(modid + ".toml"))
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        return configData;
    }

    public static ForgeConfigSpec.Builder builder() {
        return new ForgeConfigSpec.Builder();
    }
}
