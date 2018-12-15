package iezv.jmm.mybookshelfproject.Firebase;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import iezv.jmm.mybookshelfproject.SQLite.BookViewModel;
import iezv.jmm.mybookshelfproject.SQLite.DBLibro;

import static android.support.constraint.Constraints.TAG;
import static java.lang.String.valueOf;

public class firebase {

    DatabaseReference rootRef;
    static FirebaseAuth firebaseAuth;
    DatabaseReference userRef;
    DatabaseReference readingsRef;
    DatabaseReference authorRef;
    StorageReference storeRef;
    BookViewModel BVM;

    private FirebaseUser firebaseUser;

    public firebase() {
        rootRef = FirebaseDatabase.getInstance().getReference().child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        storeRef = FirebaseStorage.getInstance().getReference();
    }

    public boolean signIn (String email , String password) {
        final boolean[] ret = new boolean[1];
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ZZZ", "signInWithEmail:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    ret[0] = true;
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    ret[0] = false;
                }
            }
        });
        return ret[0];
    }

    public void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            firebaseUser = task.getResult().getUser();
                            saveUser(firebaseUser);
                        } else {
                            Log.d("ZZZ", task.getException().toString());
                        }
                    }
                });
    }

    private void saveUser(FirebaseUser user) {
        Map<String, Object> saveUser = new HashMap<>();
        saveUser.put("/user/" + user.getUid(), user.getEmail());
        rootRef.updateChildren(saveUser);
                /*addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", task.getResult().toString());
                        } else {
                            Log.d("TAG", task.getException().toString());
                        }
                    }
                });*/
    }

    public Uri uploadPic(Uri pic){

        final StorageReference userRef = storeRef.child("images/"+firebaseAuth.getCurrentUser().getEmail()+ pic.getLastPathSegment());
        UploadTask uploadTask = userRef.putFile(pic);

        final Uri[] result = new Uri[1];

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                result[0] = null;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                result[0] = userRef.getDownloadUrl().getResult();
            }
        });
        return result[0];
    }

    public File downloadPic(Uri pic) throws IOException {

        StorageReference finalRef = storeRef.child("images/"+firebaseAuth.getCurrentUser().getEmail()+pic.getLastPathSegment());

        File localFile = File.createTempFile("images", "jpg");

        finalRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        return localFile;
    }

    public void download(final BookViewModel BVM){

        this.BVM = BVM;
        userRef = rootRef.child(firebaseAuth.getCurrentUser().getUid());
        readingsRef = userRef.child("readings");
        userRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded (@NonNull DataSnapshot dataSnapshot , @Nullable String s) {

                Log.v(TAG, "downloading started");
                int count = 1;
                int id, rating, status;
                String title, author, summary, img, stDate, enDate;
                while(dataSnapshot.hasChild(valueOf(count))){
                    id = (int) (long) dataSnapshot.child(valueOf(count)).child("bid").getValue();
                    rating= (int) (long) dataSnapshot.child(valueOf(count)).child("rating").getValue();
                    status= (int) (long) dataSnapshot.child(valueOf(count)).child("readingStatus").getValue();
                    title = (String) dataSnapshot.child(valueOf(count)).child("title").getValue();
                    author = (String) dataSnapshot.child(valueOf(count)).child("author").getValue();
                    summary = (String) dataSnapshot.child(valueOf(count)).child("summary").getValue();
                    img = (String) dataSnapshot.child(valueOf(count)).child("cover").getValue();
                    stDate = (String) dataSnapshot.child(valueOf(count)).child("startDate").getValue();
                    enDate = (String) dataSnapshot.child(valueOf(count)).child("endDate").getValue();
                    DBLibro book =  new DBLibro(id, title, author, img, stDate, enDate, summary, status, rating );
                    BVM.insert(book);
                    count++;
                }
            }

            @Override
            public void onChildChanged (@NonNull DataSnapshot dataSnapshot , @Nullable String s) {

            }

            @Override
            public void onChildRemoved (@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved (@NonNull DataSnapshot dataSnapshot , @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userRef.child("test").setValue("yup");
    }

    public void upload(DBLibro... books) {
        userRef = rootRef.child(firebaseAuth.getCurrentUser().getUid());
        readingsRef = userRef.child("readings");
        new uploadAsyncTask(readingsRef).execute(books);
    }



    /*private static class downloadAsyncTask extends AsyncTask<DBLibro, Void, Void> {

        private DatabaseReference ref;
        private BookViewModel BVM;

        downloadAsyncTask(DatabaseReference ref, BookViewModel BVM){
            this.ref = ref;
            this.BVM = BVM;

        }

        @Override
        protected Void doInBackground(final DBLibro... params){
            DatabaseReference finalRef;
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 1;
                    int id, rating, status;
                    String title, author, summary, img, stDate, enDate;
                    while(dataSnapshot.hasChild(valueOf(count))){
                        id = (int) dataSnapshot.child(valueOf(count)+"/bid").getValue();
                        rating= (int) dataSnapshot.child(valueOf(count)+"/rating").getValue();
                        status= (int) dataSnapshot.child(valueOf(count)+"/readingStatus").getValue();
                        title = (String) dataSnapshot.child(valueOf(count)+"/title").getValue();
                        author = (String) dataSnapshot.child(valueOf(count)+"/author").getValue();
                        summary = (String) dataSnapshot.child(valueOf(count)+"/summary").getValue();
                        img = (String) dataSnapshot.child(valueOf(count)+"/cover").getValue();
                        stDate = (String) dataSnapshot.child(valueOf(count)+"/startDate").getValue();
                        enDate = (String) dataSnapshot.child(valueOf(count)+"/endDate").getValue();
                        DBLibro book =  new DBLibro(id, title, author, img, stDate, enDate, summary, status, rating );
                        BVM.insert(book);
                        count++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }
    }*/

    private static class uploadAsyncTask extends AsyncTask<DBLibro, Void, Void> {

        private DatabaseReference ref;

        uploadAsyncTask(DatabaseReference reference) {
            ref = reference;
        }

        @Override
        protected Void doInBackground(final DBLibro... params) {

            for (DBLibro book : params){
                ref.child(valueOf(book.getBid())).setValue(book);
            }
            return null;
        }
    }
}