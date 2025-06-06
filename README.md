# ServerEvents

**ServerEvents** is a support library for Fabric server development, designed to enhance the Fabric API's limited event system. It offers a Bukkit-like event framework while adhering to Fabric's minimalist philosophy.

- [Javadoc](https://sucj.github.io/ServerEvents/2025.6.1/)
- [Modrinth](https://modrinth.com/mod/serverevents)

The mod doesn't wrap `CommandRegistrationCallback` and `DynamicRegistrySetupCallback` from Fabric API.

## Installation
1. Import this package to your project, more information at [jitpack.io](https://jitpack.io/#icu.suc/serverevents).
2. Add `serverevents` to your mod depends.

## Usage
**ServerEvents** provides a simple API for registering and processing events.

Here is an example of a player modifying broadcast information and giving an apple when joining:

```java
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import icu.suc.mc.serverevents.ServerEvents;

public class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerEvents.Player.MODIFY_JOIN_MESSAGE.register((player, message) -> {
            player.getInventory().add(Items.APPLE.getDefaultInstance());
            return Component.literal("[+] ").append(player.getName());
        });
    }
}
``` 

## License

This project is licensed under the [MIT License](/LICENSE.txt) © 2025 sucj.