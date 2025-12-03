package com.artillexstudios.axafkzone;

import com.artillexstudios.axafkzone.commands.CommandManager;
import com.artillexstudios.axafkzone.listeners.WandListeners;
import com.artillexstudios.axafkzone.listeners.WorldListeners;
import com.artillexstudios.axafkzone.schedulers.TickZones;
import com.artillexstudios.axafkzone.utils.FileUtils;
import com.artillexstudios.axafkzone.utils.NumberUtils;
import com.artillexstudios.axafkzone.zones.Zone;
import com.artillexstudios.axafkzone.zones.Zones;
import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.dvs.versioning.BasicVersioning;
import com.artillexstudios.axapi.libs.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;

import java.io.File;

public final class AxAFKZone extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;

    public static AxPlugin getInstance() {
        return instance;
    }

    public void enable() {
        instance = this;

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("version")).build());

        NumberUtils.reload();
        TickZones.tick();

        MESSAGEUTILS = new MessageUtils(LANG.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

        CommandManager.load();
        FileUtils.loadAll();

        getServer().getPluginManager().registerEvents(new WandListeners(), this);
        getServer().getPluginManager().registerEvents(new WorldListeners(), this);
    }

    public void updateFlags() {
        FeatureFlags.USE_LEGACY_HEX_FORMATTER.set(true);
    }

    public void disable() {
        TickZones.stop();
        for (Zone zone : Zones.getZones().values()) {
            zone.disable();
        }
    }
}
