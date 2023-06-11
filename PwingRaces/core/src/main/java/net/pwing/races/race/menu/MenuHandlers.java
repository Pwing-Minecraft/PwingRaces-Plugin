package net.pwing.races.race.menu;

import net.pwing.races.util.menu.IMenuClickHandler;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class MenuHandlers {

    public static IMenuClickHandler simple(Runnable runnable) {
        return (player, action, item) -> runnable.run();
    }

    public static IMenuClickHandler simple(Consumer<Player> consumer) {
        return (player, action, item) -> consumer.accept(player);
    }
}
