package ru.buseso.spigot.free.reputation.Utils;

import org.bukkit.entity.Player;
import ru.buseso.spigot.free.reputation.Reputation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class RepPlayer {
    private String nick;
    private int reps;
    private List<String> repp;
    private List<String> repm;

    public RepPlayer(ResultSet rs) {
        try {
            this.nick = rs.getString("nick");
            this.reps = rs.getInt("reps");
            this.repp = Arrays.asList(rs.getString("repp").split(","));
            this.repm = Arrays.asList(rs.getString("repm").split(", "));
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
}
