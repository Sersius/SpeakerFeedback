package edu.upc.citm.android.speakerfeedback;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App  extends Application {
    public static final String CHANNEL_ID = "SpeakerFeeback";

    static class Room
    {
        private String name;
        private String id;

        public Room(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }
    }

    public List<Room> recent_rooms;

    @Override
    public void onCreate() {
        recent_rooms = new ArrayList<>();
        loadRecentRoomsList();
        createNotificationChannels();
        super.onCreate();
    }


    public void Save() {
        saveRecentRoomList();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "SpeakerFeedbackChannel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public boolean addRecentRoom(Room room)
    {
        for (Room currentRoom : recent_rooms) {
            if (currentRoom.id.equals(room.id))
                return false;
        }

        // We use this for limit the number of recent rooms,
        if (recent_rooms.size() == 4)
            recent_rooms.remove(3);

        recent_rooms.add(room);
        return true;
    }

    public boolean deleteRecentRoom(Room room)
    {
        if (!recent_rooms.contains(room))
            return false;

        recent_rooms.remove(room);
        return true;
    }

    private void saveRecentRoomList() {
        try {
            FileOutputStream outputStream = openFileOutput("recentRoomList.txt", MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            for (int i = 0; i < recent_rooms.size(); i++) {
                Room room = recent_rooms.get(i);
                writer.write(String.format("%s;%s\n", room.getName(), room.getId()));
            }
            writer.close();
        }
        catch (FileNotFoundException e) {
            Log.e("SpeakerFeedback", "saveRecentRoomsList: FileNotFoundException");
        }
        catch (IOException e) {
            Log.e("SpeakerFeedback", "saveRecentRoomsList: IOException when write");
        }
    }

    private void loadRecentRoomsList() {
        try {
            FileInputStream inputStream = openFileInput("recentRoomsList.txt");
            InputStreamReader reader = new InputStreamReader(inputStream);
            Scanner scanner = new Scanner(reader);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";");
                recent_rooms.add(new Room(parts[0], parts[1]));
            }
        }
        catch (FileNotFoundException e) {
            Log.e("SpeakerFeedback", "readRoomsList: FileNotFoundException");
        }
    }
}
