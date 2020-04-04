package ru.buseso.spigot.free.reputation.Utils;

import org.bukkit.entity.Player;
import ru.buseso.spigot.free.reputation.Reputation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RepPlayer {
    private String nick;
    private int reps;
    private String repp;
    private String repm;

    public RepPlayer(ResultSet rs) {
        try {
            this.nick = rs.getString("nick");
            this.reps = rs.getInt("reps");
            this.repp = rs.getString("repp");
            this.repm = rs.getString("repm");
        }catch (SQLException ignored) { }
    }

    public RepPlayer(Player p) {
        this.nick = p.getName();
        this.reps = Reputation.players.getInt("players."+nick+".reps");
        this.repp = Reputation.players.getString("players."+nick+".repp");
        this.repm = Reputation.players.getString("players."+nick+".repm");
    }

    public RepPlayer(String nick) {
        this.nick = nick;
        this.reps = Reputation.players.getInt("players."+nick+".reps");
        this.repp = Reputation.players.getString("players."+nick+".repp");
        this.repm = Reputation.players.getString("players."+nick+".repm");
    }

    public String getUuid() {
        return nick;
    }

    public int getReps() {
        return reps;
    }

    public String getRepm() {
        return repm;
    }

    public String getRepp() {
        return repp;
    }

    public void setRepm(String repm) {
        this.repm = repm;
    }

    public void setRepp(String repp) {
        this.repp = repp;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }
}
