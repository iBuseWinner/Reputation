package ru.buseso.spigot.free.reputation.Utils;

import org.bukkit.entity.Player;
import ru.buseso.spigot.free.reputation.Reputation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepPlayer {
    private String nick = "*@%";
    private int reps = 0;
    private List<String> repp = new ArrayList<>();
    private List<String> repm = new ArrayList<>();

    public RepPlayer(ResultSet rs) {
        try {
            if(rs.next()) {
                this.nick = rs.getString("nick");
                this.reps = rs.getInt("reps");
                String parseRepp = rs.getString("repp")
                        .replace("[","").replace("]","");
                String[] splitRepp = parseRepp.split(",");
                for(String repp : splitRepp) {
                    if(repp.length() > 0) {
                        this.repp.add(repp);
                    }
                }
                String parseRepm = rs.getString("repm")
                        .replace("[","").replace("]","");
                String[] splitRepm = parseRepm.split(",");
                for(String repp : splitRepm) {
                    if(repp.length() > 0) {
                        this.repm.add(repp);
                    }
                }
//                this.repp = Arrays.asList(rs.getString("repp")
//                        .replace("[","").replace("]","").split(","));
//                this.repm = Arrays.asList(rs.getString("repm")
//                        .replace("[","").replace("]","").split(", "));
            }
        }catch (SQLException ignored) { }
    }

    public RepPlayer(Player p) {
        this.nick = p.getName();
        this.reps = Reputation.players.getInt("players."+nick+".reps");
        this.repp = Reputation.players.getStringList("players."+nick+".repp");
        this.repm = Reputation.players.getStringList("players."+nick+".repm");
    }

    public RepPlayer(String nick) {
        this.nick = nick;
        this.reps = Reputation.players.getInt("players."+nick+".reps");
        this.repp = Reputation.players.getStringList("players."+nick+".repp");
        this.repm = Reputation.players.getStringList("players."+nick+".repm");
    }

    public String getUuid() {
        return nick;
    }

    public int getReps() {
        return reps;
    }

    public List<String> getRepm() {
        return repm;
    }

    public List<String> getRepp() {
        return repp;
    }

    public void setRepm(List<String> repm) {
        this.repm = repm;
    }

    public void setRepp(List<String> repp) {
        this.repp = repp;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    @Override
    public String toString() {
        return "RepPlayer{" +
                "nick='" + nick + '\'' +
                ", reps=" + reps +
                ", repp=" + repp +
                ", repm=" + repm +
                '}';
    }
}
