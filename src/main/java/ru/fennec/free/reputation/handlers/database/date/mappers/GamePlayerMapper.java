package ru.fennec.free.reputation.handlers.database.date.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import ru.fennec.free.reputation.common.interfaces.IGamePlayer;
import ru.fennec.free.reputation.handlers.players.GamePlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GamePlayerMapper implements RowMapper<IGamePlayer> {
    @Override
    public IGamePlayer map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new GamePlayer(
                rs.getLong("id"),
                UUID.fromString(rs.getString("uuid")),
                rs.getLong("reputation"));
    }
}
