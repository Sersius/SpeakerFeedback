package edu.upc.citm.android.speakerfeedback;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowAllUsersActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListenerRegistration user_registration_listener;

    private Adapter adapter;
    private RecyclerView view_user_list;
    private List<String> all_users;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_all_users);

        adapter = new Adapter();
        all_users = new ArrayList<>();

        view_user_list = findViewById(R.id.user_list_recycler_view);
        view_user_list.setLayoutManager(new LinearLayoutManager(this));
        view_user_list.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        user_registration_listener = db.collection("users").whereEqualTo("room","testroom").addSnapshotListener(usersListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        user_registration_listener.remove();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView view_users;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view_users = itemView.findViewById(R.id.user_view);
        }
    }

    private EventListener<QuerySnapshot> usersListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e != null) {
                Log.e("SpeakerFeedback","Error in ShowAllUserActivity",e);
                return;
            }

            all_users.clear();

            for (DocumentSnapshot doc : documentSnapshots)
                all_users.add(doc.getString("name"));

            adapter.notifyDataSetChanged();
        }
    };

    class Adapter extends RecyclerView.Adapter<ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.user_view_activity, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.view_users.setText(all_users.get(position));
        }

        @Override
        public int getItemCount() {
            return all_users.size();
        }
    }

}
