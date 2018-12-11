package edu.upc.citm.android.speakerfeedback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreListenerService extends Service {

    private boolean connected = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SpeakerFeedback","FirestoreListenerService.onStartCommand");
        if(!connected) {
            createForegroundNotification();
        }

        db.collection("rooms").document("testroom")
                .collection("polls").whereEqualTo("open", true)
                .addSnapshotListener(polls_listener);

        return START_NOT_STICKY;
    }

    private void createForegroundNotification() {
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        //Crear una notificacion i cridar start Foreground (perque el servei segueixi funcionant)
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(String.format("Connectat a 'testroom"))
                .setSmallIcon(R.drawable.ic_message)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
        connected = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("SpeakerFeedback","FirestoreListener.onDestroy");
    }

    private EventListener<QuerySnapshot> polls_listener = new EventListener<QuerySnapshot>(){
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e != null) {
                Log.e("SpeakerFeedback", "Error al rebre polls", e);
                return;
            }
            for (DocumentSnapshot doc : documentSnapshots)
            {
                Poll poll = doc.toObject(Poll.class);
                if(poll.isOpen())
                {
                    Log.d("SpeakerFeedback", poll.getQuestion());
                }
            }
        }
    };
}
