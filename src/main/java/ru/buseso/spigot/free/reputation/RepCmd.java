package ru.buseso.spigot.free.reputation;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.buseso.spigot.free.reputation.Utils.RepPlayer;
import ru.buseso.spigot.free.reputation.Utils.RepSender;
import ru.buseso.spigot.free.reputation.Utils.RepTop;

import java.util.ArrayList;
import java.util.List;

public class RepCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(s instanceof Player) {
            if(Reputation.config.cooldownsEnabled()) {
                if(!s.hasPermission("reputation.bypass.cd")) {
                    if (!Reputation.config.cooldownsOnlyAddRemove()) {
                        if (Reputation.repCD.cd.containsKey(((Player) s).getUniqueId())) {
                            RepSender.send(s, Reputation.config.playerErrorsWaitCd()
                                    .replaceAll("%time%", Reputation.repCD.cd.get(((Player) s).getUniqueId()).toString()));
                            return false;
                        }
                    }
                }
            }
        }

        if(a.length == 0) {
            for(String st : Reputation.config.playerHelp()) {
                RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("&","§"));
            }
            if(s.hasPermission("reputation.admin")) {
                for(String st : Reputation.config.adminHelp()) {
                    RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix())
                            .replaceAll("&","§"));
                }
            }

            if(s instanceof Player) {
                if (Reputation.config.cooldownsEnabled()) {
                    if (!Reputation.config.cooldownsOnlyAddRemove()) {
                        if (!s.hasPermission("reputation.bypass.cd")) {
                            for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                if(s.hasPermission("reputation.cooldown."+permission)) {
                                    Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                }
                            }
                        }
                    }
                }
            }
        } else if(a.length == 1) {
            if(a[0].equalsIgnoreCase("papi")) {
                s.sendMessage(PlaceholderAPI.setPlaceholders((Player)s, ""));
            } else
            if(a[0].equalsIgnoreCase("get")) {
                System.out.println("sender used command `get`");
                if (s instanceof Player) {
                    System.out.println("Sender's uuid is "+((Player)s).getUniqueId());
                    for (RepPlayer pp : Reputation.rps) {
                        System.out.println("Checking player "+pp.getUuid());
                        if (pp.getUuid().equals(s.getName())) {
                            System.out.println("pp.getUuid() equals sender's uuid");
                            String reps = pp.getReps() + "";
                            RepSender.sendToPlayer(((Player) s), Reputation.config.playerSelfReps()
                                    .replaceAll("%reputation%",reps).replaceAll("&","§"));
                        }
                    }

                    if(Reputation.config.cooldownsEnabled()) {
                        if (!Reputation.config.cooldownsOnlyAddRemove()) {
                            if(!s.hasPermission("reputation.bypass.cd")) {
                                for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                    if(s.hasPermission("reputation.cooldown."+permission)) {
                                        Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    RepSender.send(s, "&4Command only for players!");
                }
            } else if(a[0].equalsIgnoreCase("admin")) {
                if(s.hasPermission("reputation.admin")) {
                    for(String st : Reputation.config.adminHelp()) {
                        RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix())
                                .replaceAll("&","§"));
                    }
                } else {
                    RepSender.send(s,Reputation.config.noPerm()
                            .replaceAll("%prefix%",Reputation.config.prefix())
                            .replaceAll("&","§"));
                }

                if(s instanceof Player) {
                    if (Reputation.config.cooldownsEnabled()) {
                        if (!Reputation.config.cooldownsOnlyAddRemove()) {
                            if (!s.hasPermission("reputation.bypass.cd")) {
                                for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                    if(s.hasPermission("reputation.cooldown."+permission)) {
                                        Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                    }
                                }
                            }
                        }
                    }
                }
            } else if(a[0].equalsIgnoreCase("top")) {
                List<RepTop> rt;
                if(Reputation.config.dataType().equalsIgnoreCase("mysql")) rt = Reputation.requests.getTopPlayers();
                else rt = Reputation.getTopPlayers();

                int count = 0;
                for(RepTop repTop : rt) {
                    if(count < Reputation.config.topLimit()) {
                        count++;
                        RepSender.send(s, Reputation.config.playerSuccTopPlayers()
                                .replaceAll("%place%", "" + count)
                                .replaceAll("%player%", repTop.name)
                                .replaceAll("%reputation%", "" + repTop.reps));
                    }
                }

                if(s instanceof Player) {
                    if (Reputation.config.cooldownsEnabled()) {
                        if (!Reputation.config.cooldownsOnlyAddRemove()) {
                            if (!s.hasPermission("reputation.bypass.cd")) {
                                for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                    if(s.hasPermission("reputation.cooldown."+permission)) {
                                        Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                String name = a[0];

                String reps = Reputation.getRepsByNick(name);
                if(reps.equalsIgnoreCase("notfound")) {
                    RepSender.send(s, Reputation.config.playerErrorsTargetNotFound()
                            .replaceAll("%prefix%",Reputation.config.prefix())
                            .replaceAll("&","§"));
                } else {
                    RepSender.send(s, Reputation.config.playerOtherReps()
                            .replaceAll("%prefix%",Reputation.config.prefix())
                            .replaceAll("&","§").replaceAll("%player%",name)
                            .replaceAll("%reputation%",reps));

                    if(Reputation.config.cooldownsEnabled()) {
                        if (!Reputation.config.cooldownsOnlyAddRemove()) {
                            if(!s.hasPermission("reputation.bypass.cd")) {
                                for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                    if(s.hasPermission("reputation.cooldown."+permission)) {
                                        Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if(a.length == 2 && (a[0].equalsIgnoreCase("+") || a[0].equalsIgnoreCase("add")
            || a[0].equalsIgnoreCase("-")
            || a[0].equalsIgnoreCase("take"))) {
            if(s instanceof Player) {
                if(Reputation.config.cooldownsEnabled()) {
                    if(!s.hasPermission("reputation.bypass.cd")) {
                        if (Reputation.config.cooldownsOnlyAddRemove()) {
                            if (Reputation.repCD.cd.containsKey(((Player) s).getUniqueId())) {
                                RepSender.send(s, Reputation.config.playerErrorsWaitCd()
                                        .replaceAll("%time%", Reputation.repCD.cd.get(((Player) s).getUniqueId()).toString()));
                                return false;
                            }
                        }
                    }
                }

                if(a[0].equalsIgnoreCase("+") || a[0].equalsIgnoreCase("add")) {
                    RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                    if(p.getRepp() == null) {
                        System.out.println("Sender's getRepp is null so creating it");
                        p.setRepp(new ArrayList<>());
                    }

                    System.out.println("Sender's data: "+p.toString());

                    if(!Reputation.config.canUnlimitedReps()) {
                        if(p.getRepp().size() == 1) {
                            System.out.println("Can't unlimited reps (option from config) and player already given repp");
                            RepSender.send(s, Reputation.config.playerErrorsAlreadyRepPlus()
                                    .replaceAll("%prefix%", Reputation.config.prefix()).replaceAll("&", "§"));
                            return false;
                        }
                    }

                    String name = a[1].toLowerCase();
                    if(p.getRepp().contains(name)) {
                        System.out.println("Sender already repped this player");
                        RepSender.send(s, Reputation.config.playerErrorsAlreadyRepPlus()
                                .replaceAll("%prefix%", Reputation.config.prefix()).replaceAll("&", "§"));
                        return false;
                    } else {
                        RepPlayer pp = Reputation.getRepPlayerByNick(a[1]);

                        if (pp == null) {
                            System.out.println("Target not found by this nick");
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound()
                                    .replaceAll("%prefix%", Reputation.config.prefix())
                                    .replaceAll("&", "§"));
                        } else {
                            if (!p.getUuid().equals(pp.getUuid())) {
                                System.out.println("Trying to add repp");
                                pp.setReps(pp.getReps() + 1);
                                RepSender.send(s, Reputation.config.playerSuccRepPlus()
                                        .replaceAll("%prefix%", Reputation.config.prefix())
                                        .replaceAll("%player%", name).replaceAll("&", "§"));
                                List<String> repp = p.getRepp();
                                repp.add(name);
                                p.setRepp(repp);
                                if (Reputation.config.cooldownsEnabled()) {
                                    if (Reputation.config.cooldownsOnlyAddRemove()) {
                                        if (!s.hasPermission("reputation.bypass.cd")) {
                                            for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                                if(s.hasPermission("reputation.cooldown."+permission)) {
                                                    System.out.println("Cooldowns enabled, sender doesn't have permission `reputation.bypass.cd` and has permission " +
                                                            "`reputation.cooldown."+permission+"` so adding him cd");
                                                    Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                RepSender.send(s, Reputation.config.playerErrorsOwnNick());
                            }
                        }
                    }
                } else if(a[0].equalsIgnoreCase("-") || a[0].equalsIgnoreCase("take")) {
                    RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                    if(p.getRepm() == null) {
                        p.setRepm(new ArrayList<>());
                    }

                    if(!Reputation.config.canUnlimitedReps()) {
                        if(p.getRepm().size() == 1) {
                            RepSender.send(s, Reputation.config.playerErrorsAlreadyRepMinus()
                                    .replaceAll("%prefix%", Reputation.config.prefix()).replaceAll("&", "§"));
                            return false;
                        }
                    }

                    String name = a[1].toLowerCase();
                    if(p.getRepm().contains(name)) {
                        RepSender.send(s, Reputation.config.playerErrorsAlreadyRepMinus()
                                .replaceAll("%prefix%", Reputation.config.prefix())
                                .replaceAll("&", "§"));
                        return false;
                    } else {
                        RepPlayer pp = Reputation.getRepPlayerByNick(a[1]);

                        if(pp == null) {
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound()
                                    .replaceAll("%prefix%", Reputation.config.prefix())
                                    .replaceAll("&", "§"));
                        } else {
                            if (!p.getUuid().equals(pp.getUuid())) {
                                pp.setReps(pp.getReps() - 1);
                                RepSender.send(s, Reputation.config.playerSuccRepMinus()
                                        .replaceAll("%prefix%", Reputation.config.prefix())
                                        .replaceAll("%player%", name).replaceAll("&", "§"));
                                List<String> repm = p.getRepm();
                                repm.add(name);
                                p.setRepm(repm);
                                if (Reputation.config.cooldownsEnabled()) {
                                    if (Reputation.config.cooldownsOnlyAddRemove()) {
                                        if (!s.hasPermission("reputation.bypass.cd")) {
                                            for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                                if(s.hasPermission("reputation.cooldown."+permission)) {
                                                    Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                RepSender.send(s, Reputation.config.playerErrorsOwnNick());
                            }
                        }
                    }
                }
            } else {
                RepSender.send(s, "&4Command only for players!");
            }
        }

            /* else if(a[0].equalsIgnoreCase("-") || a[0].equalsIgnoreCase("take")) {
                    RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                    if(p.getRepm() == null) {
                        p.setRepm("no");
                    }

                    if(!Reputation.config.canUnlimitedReps()) {
                        if (p.getRepm().equals("no")) {
                            String name = a[1];
                            RepPlayer pp = Reputation.getRepPlayerByNick(name);

                            if (pp == null) {
                                RepSender.send(s, Reputation.config.playerErrorsTargetNotFound()
                                        .replaceAll("%prefix%", Reputation.config.prefix())
                                        .replaceAll("&", "§"));
                            } else {
                                if (!p.getUuid().equals(pp.getUuid())) {
                                    pp.setReps(pp.getReps() - 1);
                                    RepSender.send(s, Reputation.config.playerSuccRepMinus()
                                            .replaceAll("%prefix%", Reputation.config.prefix())
                                            .replaceAll("%player%", name).replaceAll("&", "§"));
                                    p.setRepm(pp.getUuid());
                                    if (Reputation.config.cooldownsEnabled()) {
                                        if (Reputation.config.cooldownsOnlyAddRemove()) {
                                            if (!s.hasPermission("reputation.bypass.cd")) {
                                                Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec());
                                            }
                                        }
                                    }
                                } else {
                                    RepSender.send(s, Reputation.config.playerErrorsOwnNick());
                                }
                            }
                        } else {
                            RepSender.send(s, Reputation.config.playerErrorsAlreadyRepMinus()
                                    .replaceAll("%prefix%", Reputation.config.prefix())
                                    .replaceAll("&", "§"));
                        }
                    } else {
                        String name = a[1];
                        RepPlayer pp = Reputation.getRepPlayerByNick(name);

                        if (pp == null) {
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound()
                                    .replaceAll("%prefix%", Reputation.config.prefix())
                                    .replaceAll("&", "§"));
                        } else {
                            if (!p.getUuid().equals(pp.getUuid())) {
                                pp.setReps(pp.getReps() - 1);
                                RepSender.send(s, Reputation.config.playerSuccRepMinus()
                                        .replaceAll("%prefix%", Reputation.config.prefix())
                                        .replaceAll("%player%", name).replaceAll("&", "§"));
                                List<String> repm = p.getRepm();

                                p.setRepm(Arrays.asList(pp.getRepm()).add(Arrays.asList(pp.getUuid())));
                                if (Reputation.config.cooldownsEnabled()) {
                                    if (Reputation.config.cooldownsOnlyAddRemove()) {
                                        if (!s.hasPermission("reputation.bypass.cd")) {
                                            Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec());
                                        }
                                    }
                                }
                            } else {
                                RepSender.send(s, Reputation.config.playerErrorsOwnNick());
                            }
                        }
                    }
                } else if(a[0].equalsIgnoreCase("undo")) {
                    if(a[1].equalsIgnoreCase("+") || a[1].equalsIgnoreCase("add")) {
                        RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                        if(p.getRepp() == null) {
                            p.setRepp(Arrays.asList("no"));
                        }

                        if(p.getRepp().equals("no")) {
                            RepSender.send(s, Reputation.config.playerErrorsNothingUndo().
                                    replaceAll("%prefix%",Reputation.config.prefix())
                                    .replaceAll("&","§"));
                        } else {
                            List<String> target = p.getRepp();
                            RepPlayer pp = Reputation.getRepPlayerByNick(target.get(0));

                            p.setRepp(Arrays.asList("no"));
                            pp.setReps(pp.getReps()-1);

                            RepSender.send(s, Reputation.config.playerSuccUndo()
                                    .replaceAll("%prefix%",Reputation.config.prefix())
                                    .replaceAll("&","§"));

                            if(Reputation.config.cooldownsEnabled()) {
                                if (Reputation.config.cooldownsOnlyAddRemove()) {
                                    if(!s.hasPermission("reputation.bypass.cd")) {
                                        Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec());
                                    }
                                }
                            }
                        }
                    } else if(a[1].equalsIgnoreCase("-") || a[1].equalsIgnoreCase("take")) {
                        RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                        if(p.getRepm() == null) {
                            p.setRepm(Arrays.asList("no"));
                        }

                        if(p.getRepm().equals("no")) {
                            RepSender.send(s, Reputation.config.playerErrorsNothingUndo()
                                    .replaceAll("%prefix%",Reputation.config.prefix())
                                    .replaceAll("&","§"));
                        } else {
                            List<String> target = p.getRepp();
                            RepPlayer pp = Reputation.getRepPlayerByNick(target.get(0));

                            p.setRepm(Arrays.asList("no"));
                            pp.setReps(pp.getReps()+1);

                            RepSender.send(s, Reputation.config.playerSuccUndo()
                                    .replaceAll("%prefix%",Reputation.config.prefix())
                                    .replaceAll("&","§"));

                            if(Reputation.config.cooldownsEnabled()) {
                                if (Reputation.config.cooldownsOnlyAddRemove()) {
                                    if(!s.hasPermission("reputation.bypass.cd")) {
                                        Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec());
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                RepSender.send(s, "&4Command only for players!");
            }
        }*/ else if(a.length == 2 && a[0].equalsIgnoreCase("admin") && a[1].equalsIgnoreCase("reload")) {
            if(!s.hasPermission("reputation.admin")) {
                RepSender.send(s, Reputation.config.noPerm().replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("&","§"));
            } else {
                Reputation.reloadCfg();
                RepSender.send(s, Reputation.config.adminSuccReload().replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("&","§"));
            }

            if(s instanceof Player) {
                if (Reputation.config.cooldownsEnabled()) {
                    if (!Reputation.config.cooldownsOnlyAddRemove()) {
                        if (!s.hasPermission("reputation.bypass.cd")) {
                            for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                if(s.hasPermission("reputation.cooldown."+permission)) {
                                    Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                }
                            }
                        }
                    }
                }
            }
        } else if(a.length == 3) {
            if(a[0].equalsIgnoreCase("admin")) {
                if(s.hasPermission("reputation.admin")) {
                    if(a[1].equalsIgnoreCase("reset")) {
                        String name = a[2];
                        RepPlayer pp = Reputation.getRepPlayerByNick(name);

                        if(pp == null) {
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound()
                                    .replaceAll("%prefix%",Reputation.config.prefix())
                                    .replaceAll("&","§"));
                        } else {
                            pp.setReps(0);
                            RepSender.send(s, Reputation.config.adminSuccReset()
                                    .replaceAll("%prefix%",Reputation.config.prefix())
                                    .replaceAll("%player%",name).replaceAll("&","§"));
                        }
                    } else {
                        for(String st : Reputation.config.adminHelp()) {
                            RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix())
                                    .replaceAll("&","§"));
                        }
                    }
                } else {
                    RepSender.send(s, Reputation.config.noPerm()
                            .replaceAll("%prefix%",Reputation.config.prefix())
                            .replaceAll("&","§"));
                }
            } else {
                for(String st : Reputation.config.playerHelp()) {
                    RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix())
                            .replaceAll("&","§"));
                }
            }

            if(s instanceof Player) {
                if (Reputation.config.cooldownsEnabled()) {
                    if (!Reputation.config.cooldownsOnlyAddRemove()) {
                        if (!s.hasPermission("reputation.bypass.cd")) {
                            for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                if(s.hasPermission("reputation.cooldown."+permission)) {
                                    Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                }
                            }
                        }
                    }
                }
            }
        } else if(a.length == 4) {
            if(a[0].equalsIgnoreCase("admin")) {
                if(s.hasPermission("reputation.admin")) {
                    String name = a[2];
                    int count = 0;
                    try {
                        count = Integer.parseInt(a[3]);
                        RepPlayer pp = Reputation.getRepPlayerByNick(name);
                        if(pp == null) {
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound()
                                    .replaceAll("%prefix%",Reputation.config.prefix())
                                    .replaceAll("&","§"));
                        } else {
                            if (a[1].equalsIgnoreCase("set")) {
                                pp.setReps(count);
                                RepSender.send(s, Reputation.config.adminSuccSet()
                                        .replaceAll("%prefix%",Reputation.config.prefix())
                                        .replaceAll("%player%",name)
                                        .replaceAll("%reputation%",""+count)
                                        .replaceAll("&","§"));
                            } else if (a[1].equalsIgnoreCase("take")) {
                                pp.setReps(pp.getReps()-count);
                                RepSender.send(s, Reputation.config.adminSuccTake()
                                        .replaceAll("%prefix%",Reputation.config.prefix())
                                        .replaceAll("%player%",name)
                                        .replaceAll("%reputation%",""+count)
                                        .replaceAll("&","§"));
                            } else if (a[1].equalsIgnoreCase("add")) {
                                pp.setReps(pp.getReps()+count);
                                RepSender.send(s, Reputation.config.adminSuccAdd()
                                        .replaceAll("%prefix%",Reputation.config.prefix())
                                        .replaceAll("%player%",name)
                                        .replaceAll("%reputation%",""+count)
                                        .replaceAll("&","§"));
                            } else {
                                for (String st : Reputation.config.adminHelp()) {
                                    RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix())
                                            .replaceAll("&","§"));
                                }
                            }
                        }
                    }catch (NumberFormatException e) {
                        RepSender.send(s, Reputation.config.adminError()
                                .replaceAll("%prefix%",Reputation.config.prefix())
                                .replaceAll("&","§"));
                        if(Reputation.config.debugMode()) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    RepSender.send(s, Reputation.config.noPerm()
                            .replaceAll("%prefix%",Reputation.config.prefix())
                            .replaceAll("&","§"));
                }
            } else {
                for(String st : Reputation.config.playerHelp()) {
                    RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix())
                            .replaceAll("&","§"));
                }
            }

            if(s instanceof Player) {
                if (Reputation.config.cooldownsEnabled()) {
                    if (!Reputation.config.cooldownsOnlyAddRemove()) {
                        if (!s.hasPermission("reputation.bypass.cd")) {
                            for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                if(s.hasPermission("reputation.cooldown."+permission)) {
                                    Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for(String st : Reputation.config.playerHelp()) {
                RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix())
                        .replaceAll("&","§"));
            }

            if(s instanceof Player) {
                if (Reputation.config.cooldownsEnabled()) {
                    if (!Reputation.config.cooldownsOnlyAddRemove()) {
                        if (!s.hasPermission("reputation.bypass.cd")) {
                            for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                                if(s.hasPermission("reputation.cooldown."+permission)) {
                                    Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                                }
                            }
                        }
                    }
                }
            }
        }

        if(s instanceof Player) {
            if (Reputation.config.cooldownsEnabled()) {
                if (!Reputation.config.cooldownsOnlyAddRemove()) {
                    if (!s.hasPermission("reputation.bypass.cd")) {
                        for(String permission : Reputation.config.cooldownsTimeInSec().keySet()) {
                            if(s.hasPermission("reputation.cooldown."+permission)) {
                                Reputation.repCD.cd.put(((Player) s).getUniqueId(), Reputation.config.cooldownsTimeInSec().get(permission));
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
