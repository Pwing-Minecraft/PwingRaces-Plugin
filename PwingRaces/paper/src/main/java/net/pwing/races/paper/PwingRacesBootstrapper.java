package net.pwing.races.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.pwing.races.PwingRaces;
import net.pwing.races.command.RaceExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PwingRacesBootstrapper implements PluginBootstrap {
    private final PwingRacesPaper plugin = new PwingRacesPaper();

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {

    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new PwingRaces(this.plugin::onEnable, this.plugin::onDisable) {

            @Override
            public void registerCommands() {
                RaceExecutor raceExecutor = new RaceExecutor(this);
                Command command = new Command("race") {

                    @Override
                    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                        return raceExecutor.onCommand(sender, this, commandLabel, args);
                    }

                    @Override
                    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
                        return raceExecutor.onTabComplete(sender, this, alias, args);
                    }
                };

                this.getServer().getCommandMap().register("pwingraces", command);
            }
        };
    }
}
