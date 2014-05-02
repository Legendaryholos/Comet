package com.cometproject.server.storage.queries.rooms;

import com.cometproject.server.game.rooms.types.Room;
import com.cometproject.server.game.rooms.types.RoomData;
import com.cometproject.server.game.rooms.types.RoomModel;
import com.cometproject.server.storage.SqlHelper;
import javolution.util.FastMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomDao {
    public static ArrayList<RoomModel> getModels() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList<RoomModel> data = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM room_models", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                data.add(new RoomModel(resultSet));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return data;
    }

    public static Room getRoomById(int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                return new Room(new RoomData(resultSet));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return null;
    }

    public static Map<Integer, Room> getRoomsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, Room> rooms = new FastMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE owner_id = ?", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                rooms.put(resultSet.getInt("id"), new Room(new RoomData(resultSet)));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return rooms;
    }

    public static List<RoomData> getRoomsByQuery(String query) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<RoomData> rooms = new ArrayList<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            if (query.startsWith("owner:")) {
                preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE owner = ?", sqlConnection);
                preparedStatement.setString(1, query.split("owner:")[1]);
            } else {
                preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE name LIKE ? LIMIT 150", sqlConnection);
                preparedStatement.setString(1, "%" + query + "%");
            }

            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                rooms.add(new RoomData(resultSet));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return rooms;
    }

    public static int createRoom(String name, String model, int userId, String username) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("INSERT into rooms (`owner_id`, `owner`, `name`, `model`) VALUES(?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, model);

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            while(resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return 0;
    }

    public static void saveRoomData(RoomData data) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("UPDATE rooms SET name = ?, description = ?, owner_id = ?, owner = ?, category = ?, max_users = ?, access_type = ?, password = ?, score = ?, tags = ?, " +
            "decorations = ?, model = ?, hide_walls = ?, thickness_wall = ?, thickness_floor = ?, allow_walkthrough = ? WHERE id = ?", sqlConnection, true);


            preparedStatement.setString(1, data.getName());
            preparedStatement.setString(2, data.getDescription());
            preparedStatement.setInt(3, data.getOwnerId());
            preparedStatement.setString(4, data.getOwner());
            preparedStatement.setInt(5, data.getCategory().getId());
            preparedStatement.setInt(6, data.getMaxUsers());
            preparedStatement.setString(7, data.getAccess());
            preparedStatement.setString(8, data.getPassword());
            preparedStatement.setInt(9, data.getScore());

            String tagString = "";

            for (int i = 0; i < data.getTags().length; i++) {
                if (i != 0) {
                    tagString += ",";
                }

                tagString += data.getTags()[i];
            }

            preparedStatement.setString(10, tagString);

            String decorString = "";

            for (Map.Entry<String, String> decoration : data.getDecorations().entrySet()) {
                decorString += decoration.getKey() + "=" + decoration.getValue() + ",";
            }

            preparedStatement.setString(11, decorString.substring(0, decorString.length() - 1));
            preparedStatement.setString(12, data.getModel());
            preparedStatement.setString(13, data.getHideWalls() ? "1" : "0");
            preparedStatement.setInt(14, data.getWallThickness());
            preparedStatement.setInt(15, data.getFloorThickness());
            preparedStatement.setString(16, data.getAllowWalkthrough() ? "1" : "0");
            preparedStatement.setInt(17, data.getId());


            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

}
