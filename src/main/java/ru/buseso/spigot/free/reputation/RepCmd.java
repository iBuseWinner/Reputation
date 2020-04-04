package ru.buseso.spigot.free.reputation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.buseso.spigot.free.reputation.Utils.RepPlayer;
import ru.buseso.spigot.free.reputation.Utils.RepSender;

public class RepCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(a.length == 0) {
            for(String st : Reputation.config.playerHelp()) {
                RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
            }
            if(s.hasPermission("reputation.admin")) {
                for(String st : Reputation.config.adminHelp()) {
                    RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                }
            }
        } else if(a.length == 1) {
            if(a[0].equalsIgnoreCase("get")) {
                if (s instanceof Player) {
                    for (RepPlayer pp : Reputation.rps) {
                        if (pp.getUuid().equals(s.getName())) {
                            String reps = pp.getReps() + "";
                            RepSender.sendToPlayer(((Player) s), Reputation.config.playerSelfReps().replaceAll("%reputation%",reps).replaceAll("&","§"));
                        }
                    }
                } else {
                    RepSender.send(s, "&4Command only for players!");
                }
            } else if(a[0].equalsIgnoreCase("admin")) {
                if(s.hasPermission("reputation.admin")) {
                    for(String st : Reputation.config.adminHelp()) {
                        RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                    }
                } else {
                    RepSender.send(s,Reputation.config.noPerm().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                }
            } else {
                String name = a[0];

                String reps = Reputation.getRepsByNick(name);
                if(reps.equalsIgnoreCase("notfound")) {
                    RepSender.send(s, Reputation.config.playerErrorsTargetNotFound().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                }
            }
        } else if(a.length == 2 && (a[0].equalsIgnoreCase("+") || a[0].equalsIgnoreCase("add")
            || a[0].equalsIgnoreCase("undo") || a[0].equalsIgnoreCase("-")
            || a[0].equalsIgnoreCase("take"))) {
            if(s instanceof Player) {
                if (a[0].equalsIgnoreCase("+") || a[0].equalsIgnoreCase("add")) {
                    RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                    if(p.getRepp() == null) {
                        p.setRepp("no");
                    }

                    if(p.getRepp().equals("no")) {
                        String name = a[1];
                        RepPlayer pp = Reputation.getRepPlayerByNick(name);

                        if(pp == null) {
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        } else {
                            if(!p.getUuid().equals(pp.getUuid())) {
                                pp.setReps(pp.getReps() + 1);
                                RepSender.send(s, Reputation.config.playerSuccRepPlus().replaceAll("%prefix%", Reputation.config.prefix()).replaceAll("%player%", name).replaceAll("&", "§"));
                                p.setRepp(pp.getUuid());
                            } else {
                                RepSender.send(s, Reputation.config.playerErrorsOwnNick());
                            }
                        }
                    } else {
                        RepSender.send(s, Reputation.config.playerErrorsAlreadyRepPlus().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                    }
                } else if(a[0].equalsIgnoreCase("-") || a[0].equalsIgnoreCase("take")) {
                    RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                    if(p.getRepm() == null) {
                        p.setRepm("no");
                    }

                    if(p.getRepm().equals("no")) {
                        String name = a[1];
                        RepPlayer pp = Reputation.getRepPlayerByNick(name);

                        if(pp == null) {
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        } else {
                            if(!p.getUuid().equals(pp.getUuid())) {
                                pp.setReps(pp.getReps()-1);
                                RepSender.send(s, Reputation.config.playerSuccRepMinus().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("%player%",name).replaceAll("&","§"));
                                p.setRepm(pp.getUuid());
                            } else {
                                RepSender.send(s, Reputation.config.playerErrorsOwnNick());
                            }
                        }
                    } else {
                        RepSender.send(s, Reputation.config.playerErrorsAlreadyRepMinus().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                    }
                } else if(a[0].equalsIgnoreCase("undo")) {
                    if(a[1].equalsIgnoreCase("+") || a[1].equalsIgnoreCase("add")) {
                        RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                        if(p.getRepp() == null) {
                            p.setRepp("no");
                        }

                        if(p.getRepp().equals("no")) {
                            RepSender.send(s, Reputation.config.playerErrorsNothingUndo().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        } else {
                            String target = p.getRepp();
                            RepPlayer pp = Reputation.getRepPlayerByNick(target);

                            p.setRepp("no");
                            pp.setReps(pp.getReps()-1);

                            RepSender.send(s, Reputation.config.playerSuccUndo().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        }
                    } else if(a[1].equalsIgnoreCase("-") || a[1].equalsIgnoreCase("take")) {
                        RepPlayer p = Reputation.getRepPlayerByNick(s.getName());

                        if(p.getRepm() == null) {
                            p.setRepm("no");
                        }

                        if(p.getRepm().equals("no")) {
                            RepSender.send(s, Reputation.config.playerErrorsNothingUndo().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        } else {
                            String target = p.getRepp();
                            RepPlayer pp = Reputation.getRepPlayerByNick(target);

                            p.setRepm("no");
                            pp.setReps(pp.getReps()+1);

                            RepSender.send(s, Reputation.config.playerSuccUndo().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        }
                    }
                }
            } else {
                RepSender.send(s, "&4Command only for players!");
            }
        } else if(a.length == 2 && a[0].equalsIgnoreCase("admin") && a[1].equalsIgnoreCase("reload")) {
            if(!s.hasPermission("reputation.admin")) {
                RepSender.send(s, Reputation.config.noPerm().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
            } else {
                Reputation.reloadCfg();
                RepSender.send(s, Reputation.config.adminSuccReload().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
            }
        } else if(a.length == 3) {
            if(a[0].equalsIgnoreCase("admin")) {
                if(s.hasPermission("reputation.admin")) {
                    if(a[1].equalsIgnoreCase("reset")) {
                        String name = a[2];
                        RepPlayer pp = Reputation.getRepPlayerByNick(name);

                        if(pp == null) {
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        } else {
                            pp.setReps(0);
                            RepSender.send(s, Reputation.config.adminSuccReset().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("%player%",name).replaceAll("&","§"));
                        }
                    } else {
                        for(String st : Reputation.config.adminHelp()) {
                            RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        }
                    }
                } else {
                    RepSender.send(s, Reputation.config.noPerm().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                }
            } else {
                for(String st : Reputation.config.playerHelp()) {
                    RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
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
                            RepSender.send(s, Reputation.config.playerErrorsTargetNotFound().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        } else {
                            if (a[1].equalsIgnoreCase("set")) {
                                pp.setReps(count);
                                RepSender.send(s, Reputation.config.adminSuccSet().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("%player%",name).replaceAll("%reputation%",""+count).replaceAll("&","§"));
                            } else if (a[1].equalsIgnoreCase("take")) {
                                pp.setReps(pp.getReps()-count);
                                RepSender.send(s, Reputation.config.adminSuccTake().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("%player%",name).replaceAll("%reputation%",""+count).replaceAll("&","§"));
                            } else if (a[1].equalsIgnoreCase("add")) {
                                pp.setReps(pp.getReps()+count);
                                RepSender.send(s, Reputation.config.adminSuccAdd().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("%player%",name).replaceAll("%reputation%",""+count).replaceAll("&","§"));
                            } else {
                                for (String st : Reputation.config.adminHelp()) {
                                    RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                                }
                            }
                        }
                    }catch (NumberFormatException e) {
                        RepSender.send(s, Reputation.config.adminError().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                        if(Reputation.config.debugMode()) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    RepSender.send(s, Reputation.config.noPerm().replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                }
            } else {
                for(String st : Reputation.config.playerHelp()) {
                    RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
                }
            }
        } else {
            for(String st : Reputation.config.playerHelp()) {
                RepSender.send(s, st.replaceAll("%prefix%",Reputation.config.prefix()).replaceAll("&","§"));
            }
        }
        return false;
    }
}
