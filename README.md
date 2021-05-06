## Todo

- ~~Respawn command~~
- ~~Team/all chats~~
- ~~Stop defenders from spawn-camping the attackers~~
- ~~Fix arrow team-killing~~
- ~~Move magic location values to map config file~~
- ~~Make it more user-friendly when you're moving around wood swords and better swords~~
- ~~Kill feed~~
- **End the game if the second time goes past first time? (Forfeit option?)**
- **Hardcode nether star location so it's not pushed around**
- Explanation of /all command
- Clean up random barriers around the map
- Attacker momentum would really stagnate for a while when they couldn't find an easy way into the base, maybe this can
  be fixed by adding/changing team buffs and by increasing the price of blocks.
- Durability
- Update skins on player join
- End building mode when people are out of coins
- Blow up/go under map
- Better tools
- Pick up flower
- Keep players from using old secret storage and actually add a secret storage tab in the shop

## Bugs

- ~~Gravel can't be broken when it falls into its location.~~
- ~~Building phase coin duping~~
- ~~Shop NPCs have capes~~
- ~~Sword duplication--replacing sword and adding to inventory~~
- ~~Building mode keep inventory~~
- ~~Not killing shops after game~~
- ~~Bow kills ding feedback gamer points~~
- ~~Tossing the book out allows you to join an instance multiple times~~
- Sometimes players aren't assigned to a team (maybe if there's an odd number of players?)
- Name colors aren't updating when new games start.
- Team buff effects are sometimes persisting in-between rounds(/games?)
- Cleaner time when it goes to 8 minutes
- Shops not looking in the right direction (attacker normal shop)
- Shop skins gone?
- Wood sword in secret storage = can't buy sword
- Double "join" message
- Some instances start where coins don't decrease when buying stuff
- Shops don't have player skins anymore?

## Roadmap

- Shops
    - Armor
    - Double jump
    - Strength?
- Multiple maps
- Game modifiers (infinite time)

## Refactoring

Buying items in a shop is very hap-hazard. There should be a standardized way of doing it. Have each `ItemStack` in an
inventory correspond to an object extending `ShopItem`. `ShopItem` has a `buyItem` method that adds it to the player's
inventory. `ShopItem::buyItem` just adds it to the player's inventory, but more advanced items extend `ShopItem` and
override `buyItem`, allowing more advanced functionality for stuff like tools.

Normal items can probably just be created in-line, you don't need a separate class for each item.

---

## Game Cycle

1. Everybody starts in a lobby world
2. Each player that wants to join the game signifies it by running a command or something, then they're added to a list
3. Once the game has enough players, the game instance is created, the map/players passed to it, and the game world
   created
4. All the players are moved to the game world (on the waiting platform)
5. The countdown starts
6. Teams are split, and sent to their defending base in build mode.
7. Once building time is up, the attackers start.
8. Once time is up, or the objective is reached, the teams switch sides.
9. Once time is up, or the objective is reached, the winner is decided, and everything goes bog wild.

---

#### Planning

Misc., inconsequential questions to answer in the future:

- Should health be visible to enemies?
- Are gamer points shared throughout entire team or kept by individual players?

---

#### Resources

[FastBoard](https://github.com/MrMicky-FR/FastBoard)