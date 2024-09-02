package pl.epicserwer.czolg.papi.tnttag;

import java.lang.reflect.Field;
import java.util.HashMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.juriantech.tnttag.Arena;
import nl.juriantech.tnttag.Tnttag;
import nl.juriantech.tnttag.api.API;
import nl.juriantech.tnttag.managers.ArenaManager;
import nl.juriantech.tnttag.managers.GameManager;
import nl.juriantech.tnttag.runnables.StartRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TntTagExpansion extends PlaceholderExpansion {
   private static final HashMap<String, String> STATUS_MAP = getStatusHashMap();
   private API tntApi;
   private ArenaManager arenaManager;

   public boolean canRegister() {
      return Bukkit.getPluginManager().getPlugin("Tnttag") != null;
   }

   public boolean register() {
      System.out.println("Registering Tnttag");
      this.tntApi = Tnttag.getAPI();

      try {
         Field arenaManagerField = API.class.getDeclaredField("arenaManager");
         arenaManagerField.setAccessible(true);
         System.out.println(arenaManagerField);
         this.arenaManager = (ArenaManager)arenaManagerField.get(this.tntApi);
      } catch (IllegalAccessException | NoSuchFieldException var2) {
         var2.printStackTrace();
      }

      return super.register();
   }

   @NotNull
   public String getIdentifier() {
      return "tntTagPapi";
   }

   @NotNull
   public String getAuthor() {
      return "Czolg1";
   }

   @NotNull
   public String getVersion() {
      return "1.0";
   }

   @Nullable
   public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
      if (params == null) {
         $$$reportNull$$$0(0);
      }

      if (offlinePlayer == null) {
         return "";
      } else {
         Player player;
         if (params.equalsIgnoreCase("time2") && offlinePlayer instanceof Player) {
            player = (Player)offlinePlayer;
            Arena arena = this.arenaManager.getPlayerArena(player);
            if (arena == null) {
               return "";
            } else {
               GameManager gameManager = arena.getGameManager();
               if (gameManager == null) {
                  return "";
               } else {
                  StartRunnable startRunnable = gameManager.startRunnable;
                  return startRunnable == null ? "" : this.countdown(startRunnable);
               }
            }
         } else if (params.equalsIgnoreCase("time") && offlinePlayer instanceof Player) {
            player = (Player)offlinePlayer;
            return player.getLevel().makeConcatWithConstants<invokedynamic>(player.getLevel());
         } else {
            String name;
            if (params.startsWith("status")) {
               name = params.replace("status_", "");
               return (String)STATUS_MAP.get(name);
            } else if (params.startsWith("state")) {
               name = params.replace("state_", "");
               String state = this.tntApi.getArenaState(name);
               return (String)STATUS_MAP.getOrDefault(state, state);
            } else {
               return null;
            }
         }
      }
   }

   private String countdown(StartRunnable startRunnable) {
      try {
         Field field = StartRunnable.class.getDeclaredField("timeLeft");
         field.setAccessible(true);
         return String.valueOf((Integer)field.get(startRunnable));
      } catch (IllegalAccessException | NoSuchFieldException var3) {
         var3.printStackTrace();
         return "Error";
      }
   }

   private static HashMap<String, String> getStatusHashMap() {
      HashMap<String, String> statusHashMap = new HashMap();
      statusHashMap.put("IDLE", ChatColor.GREEN + "Oczekiwanie");
      statusHashMap.put("STARTING", ChatColor.GREEN + "Startowanie");
      statusHashMap.put("INGAME", ChatColor.YELLOW + "W grze");
      statusHashMap.put("ENDING", ChatColor.RED + "Zakańczanie");
      return statusHashMap;
   }

   // $FF: synthetic method
   private static void $$$reportNull$$$0(int var0) {
      throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "params", "pl/epicserwer/czolg/papi/tnttag/TntTagExpansion", "onRequest"));
   }
}
