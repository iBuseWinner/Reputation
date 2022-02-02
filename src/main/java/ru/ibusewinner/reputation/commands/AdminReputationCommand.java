package ru.ibusewinner.reputation.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ibusewinner.plugin.buseapi.BuseAPI;
import ru.ibusewinner.plugin.buseapi.command.ICommand;
import ru.ibusewinner.reputation.Reputation;
import ru.ibusewinner.reputation.data.items.User;

import java.util.ArrayList;
import java.util.List;

public class AdminReputationCommand extends ICommand {
    public AdminReputationCommand(JavaPlugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    public void execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.hasPermission("reputation.admin")) {
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("reload")) {
                        //ToDo reload logic
                    } else {
                        commandSender.sendMessage(
                                BuseAPI.getMessageManager().convertMessage(
                                        Reputation.getSettings().getConfig().getString("message.help").replace("\\n","\n")
                                )
                        );
                    }
                    break;
                case 3:
                    String nickname = args[1];
                    try {
                        long rep = Long.parseLong(args[2]);
                        User target = Reputation.getCachedUser(nickname);
                        boolean updateData = false;
                        if (target == null) {
                            target = Reputation.getMySQL().getUserByNickname(nickname);
                            if (target == null) {
                                commandSender.sendMessage(
                                        BuseAPI.getMessageManager().convertMessage(
                                                Reputation.getSettings().getConfig().getString("message.target-not-found")
                                        )
                                );
                            } else {
                                updateData = true;
                            }
                        }
                        if (args[0].equalsIgnoreCase("give")) {
                            target.addRep(rep);
                            commandSender.sendMessage(
                                    BuseAPI.getMessageManager().convertMessage(
                                            Reputation.getSettings().getConfig().getString("message.added-to-target")
                                                    .replace("%target-name%", target.getNickname()
                                                    .replace("%target-rep%", String.valueOf(target.getRep())
                                                    .replace("%command-arg2%", String.valueOf(rep))))
                                    )
                            );
                        } else {
                            target.removeRep(rep);
                            commandSender.sendMessage(
                                    BuseAPI.getMessageManager().convertMessage(
                                            Reputation.getSettings().getConfig().getString("message.removed-from-target")
                                                    .replace("%target-name%", target.getNickname()
                                                    .replace("%target-rep%", String.valueOf(target.getRep())
                                                    .replace("%command-arg2%", String.valueOf(rep))))
                                    )
                            );
                        }
                        if (updateData) {
                            Reputation.getMySQL().updateUser(target);
                        }
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(
                                BuseAPI.getMessageManager().convertMessage(
                                        Reputation.getSettings().getConfig().getString("message.not-number")
                                                .replace("%command-arg2%", args[2])
                                )
                        );
                    }
                    break;
                default:
                    commandSender.sendMessage(
                            BuseAPI.getMessageManager().convertMessage(
                                    Reputation.getSettings().getConfig().getString("message.help").replace("\\n","\n")
                            )
                    );
                    break;
            }
        } else {
            commandSender.sendMessage(
                    BuseAPI.getMessageManager().convertMessage(
                            Reputation.getSettings().getConfig().getString("message.no-permissions")
                    )
            );
        }
    }

    public List<String> complete(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("reputation.admin")) {
            if (args.length == 1) {
                return List.of("reload","give","take");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
                List<String> nicks = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    nicks.add(player.getName());
                }
                return nicks;
            }
        }
        return List.of();
    }
}
