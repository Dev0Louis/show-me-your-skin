package nl.enjarai.showmeyourskin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import nl.enjarai.showmeyourskin.client.DummyClientPlayerEntity;
import nl.enjarai.showmeyourskin.gui.ArmorScreen;
import nl.enjarai.showmeyourskin.util.CombatLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class ModConfig {
    public static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir() + "/" + ShowMeYourSkin.MODID + ".json");
    public static ModConfig INSTANCE;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // Makes the json use new lines instead of being a "one-liner"
            .serializeNulls() // Makes fields with `null` value to be written as well.
            .disableHtmlEscaping() // We'll be able to use custom chars without them being saved differently
            .create();

    public long combatCooldown = 16;
    public final ArmorConfig global = new ArmorConfig();
    public final HashMap<UUID, ArmorConfig> overrides = new HashMap<>();


    public ArmorConfig getApplicable(UUID uuid) {
        var applicable = overrides.getOrDefault(uuid, global);
        return applicable.showInCombat && CombatLogger.INSTANCE.isInCombat(uuid) ? ArmorConfig.VANILLA_VALUES : applicable;
    }

    public ArmorConfig getOverride(UUID uuid) {
        return overrides.get(uuid);
    }

    public ArmorConfig getOrCreateOverride(UUID uuid) {
        var armorConfig = getOverride(uuid);

        if (armorConfig == null) {
            armorConfig = new ArmorConfig();
            overrides.put(uuid, armorConfig);
        }

        return armorConfig;
    }

    public void deleteOverride(UUID uuid) {
        overrides.remove(uuid);
    }

    public ArmorScreen getScreen(PlayerEntity player, Screen parent) {
        return new ArmorScreen(player, getOrCreateOverride(player.getUuid()), parent);
    }

    public ArmorScreen getGlobalScreen(Screen parent) {
        return new ArmorScreen(DummyClientPlayerEntity.getInstance(), global, parent);
    }


    public static void load() {
        INSTANCE = loadConfigFile(CONFIG_FILE);
    }

    public void save() {
        saveConfigFile(CONFIG_FILE);
    }

    /**
     * Loads config file.
     *
     * @param file file to load the config file from.
     * @return ConfigManager object
     */
    private static ModConfig loadConfigFile(File file) {
        ModConfig config = null;

        if (file.exists()) {
            // An existing config is present, we should use its values
            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                // Parses the config file and puts the values into config object
                config = GSON.fromJson(fileReader, ModConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Problem occurred when trying to load config: ", e);
            }
        }
        // gson.fromJson() can return null if file is empty
        if (config == null) {
            config = new ModConfig();
        }

        // Saves the file in order to write new fields if they were added
        config.saveConfigFile(file);
        return config;
    }

    /**
     * Saves the config to the given file.
     *
     * @param file file to save config to
     */
    private void saveConfigFile(File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
