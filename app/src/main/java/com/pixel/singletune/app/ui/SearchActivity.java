package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.adapters.UserAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SearchActivity extends Activity {

    public static final String TAG = SearchActivity.class.getSimpleName();
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        Boolean followed;

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ImageView checkAvatarView = (ImageView) view.findViewById(R.id.selectedAvatarImageView);
//            TODO: Remember to use overlay image
            if (mGridView.isItemChecked(i)) {
                // mFriendsRelation.add(mUsers.get(i));

                ParseObject follow = new ParseObject("Follow");
                follow.put("from", ParseUser.getCurrentUser());
                follow.put("to", mUsers.get(i));
                follow.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        followed = true;

                    }
                });

                // Add Checkmark
                checkAvatarView.setVisibility(View.VISIBLE);

                // Send Push Notification REDUNDANT: Sends from cloud.
                // sendPushNotification(mUsers.get(i).getObjectId(), followed);

                Log.d(TAG, "Checked");
            } else {
                // New remove friend
                ParseQuery<ParseObject> followQuery = ParseQuery.getQuery(ParseConstants.CLASS_FOLLOW);
                followQuery.whereEqualTo("from", mCurrentUser);
                followQuery.whereEqualTo("to", mUsers.get(i));
                followQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject followObject, ParseException e) {
                        followObject.deleteInBackground();
                    }
                });

                // remove the friend
                //mFriendsRelation.remove(mUsers.get(i));
                followed = false;

                // Remove checkmark
                checkAvatarView.setVisibility(View.INVISIBLE);

                // Send push notification
                // sendPushNotification(mUsers.get(i).getObjectId(), followed);
            }
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
    };
    protected GridView mGridView;
    protected List<ParseUser> mUsers;
    @InjectView(R.id.search_editText)
    EditText mSearchText;
    @InjectView(R.id.search_button)
    ImageButton mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_search);

        mGridView = (GridView) findViewById(R.id.friendsGrid);

        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ButterKnife.inject(this);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QueryUser();
            }
        });
    }

    private void QueryUser() {
        EditText gUname;
        gUname = (EditText) findViewById(R.id.search_editText);
        setProgressBarIndeterminateVisibility(true);

        String gUsername = gUname.getText().toString();

//        TODO: Use cloud code

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(ParseConstants.KEY_USERNAME, gUsername);
        params.put(ParseConstants.KEY_CURRENT_USER_ID, mCurrentUser.getObjectId());

        ParseCloud.callFunctionInBackground(
                ParseConstants.CLOUD_FUNCTION_USER_SEARCH,
                params,
                new FunctionCallback<Object>() {

                @Override
                public void done(Object results, ParseException e) {
                    if (e == null){
                        Log.d(TAG, results.toString());
                    }
                }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("username", gUsername);
        query.whereNotEqualTo("objectId", mCurrentUser.getObjectId());
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e != null) {
                    Log.d("singleTune", "Nothing found");
                } else {
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(SearchActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mUsers);
                    }

                    friendFollowCheck();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private void friendFollowCheck() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < mUsers.size(); i++) {
                        ParseUser user = mUsers.get(i);
                        for (ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                //TODO: Change button style and text
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void sendPushNotification(String userId, Boolean b) {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER_ID, userId);

        JSONObject obj;

        try {
            obj = new JSONObject();
            if (b) {
                obj.put("msg", "Yay! " + ParseUser.getCurrentUser().getUsername() + " is now following you.");
            } else {
                obj.put("msg    ", "Aww! " + ParseUser.getCurrentUser().getUsername() + " has unfollowed you.");
            }
            obj.put("data", "You have a new activity");
            obj.put("action", "com.pixel.singletune.app.UPDATE_STATUS");
            obj.put("channel", "Activities");

            ParsePush push = new ParsePush();
            push.setQuery(query);
            push.setData(obj);
            push.sendInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
