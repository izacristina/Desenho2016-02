package com.mathheals.meajuda.view.topics;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mathheals.meajuda.R;
import com.mathheals.meajuda.dao.DownloadImageTask;
import com.mathheals.meajuda.exception.UserException;
import com.mathheals.meajuda.model.Comment;
import com.mathheals.meajuda.model.Topic;
import com.mathheals.meajuda.model.TopicEvaluation;
import com.mathheals.meajuda.model.User;
import com.mathheals.meajuda.presenter.CommentPresenter;
import com.mathheals.meajuda.presenter.TopicEvaluationPresenter;
import com.mathheals.meajuda.presenter.TopicPresenter;
import com.mathheals.meajuda.presenter.UserPresenter;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

import java.util.List;

public class TopicView extends Fragment implements View.OnClickListener {

    private TextView nameAuthorTextView;
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView topicEvaluationTextView;
    private TextView classification;
    private ImageView photo;
    private ImageView topicImage;

    private Button playAudioButton;

    Integer idTopic = 0;
    Integer idCategory = 0;

    private Topic currentTopic;

    private Integer topicEvaluation;

    public TopicView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View topicView = inflater.inflate(R.layout.fragment_topic_view, container, false);

        Bundle args = this.getArguments();

        idTopic = args.getInt("idTopic", 0);
        Log.d("Topic id: ", idTopic + "");

        assert(idTopic != 0);

        RecyclerView recyclerView = (RecyclerView) topicView.findViewById
                (R.id.recycler_view_topics);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CommentPresenter commentPresenter = CommentPresenter.getInstance(getContext());
        List<Comment> comments = commentPresenter.getCommentsOfTopic
                (idTopic);

        Log.d("qtd elementos: ", comments.size() + "");

        CommentListAdapter commentListAdapter = new CommentListAdapter(getContext(), comments);

        ImageView upEvaluate = (ImageView) topicView.findViewById(R.id.up_evaluation);
        upEvaluate.setOnClickListener(this);

        ImageView downEvaluate = (ImageView) topicView.findViewById(R.id.down_evaluation);
        downEvaluate.setOnClickListener(this);

        photo = (ImageView) topicView.findViewById(R.id.user_profile_photo);
        classification = (TextView) topicView.findViewById(R.id.classification_description);

        recyclerView.setAdapter(commentListAdapter);

        setViews(topicView);
        setTopicInfo();

        return topicView;
    }

    private void setViews(View view) {
        nameAuthorTextView = (TextView) view.findViewById(R.id.nameAuthor);

        titleTextView = (TextView) view.findViewById(R.id.title);
        titleTextView.setTypeface(null, Typeface.BOLD);

        topicEvaluationTextView = (TextView) view.findViewById(R.id.topicEvaluation);

        contentTextView = (TextView) view.findViewById(R.id.content);

        FloatingActionButton createCommentButton = (FloatingActionButton)
                view.findViewById(R.id.create_comment);
        createCommentButton.setOnClickListener(this);

        SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(
                getContext());
        if(!session.getBoolean("IsLoggedIn", false)){
            createCommentButton.setVisibility(View.INVISIBLE);
        }

        playAudioButton = (Button) view.findViewById(R.id.topicViewPlayAudio);
        playAudioButton.setOnClickListener(this);

        topicImage = (ImageView) view.findViewById(R.id.topicViewImage);
    }

    private void setTopicInfo() {
        Log.d("id do topico: ", idTopic + "");

        TopicPresenter topicPresenter = TopicPresenter.getInstance(getContext());

        currentTopic = topicPresenter.getTopicById(idTopic);
        idCategory = currentTopic.getIdCategory();

        String imageURL = currentTopic.getImageURL();

        if(!imageURL.trim().equals("N")) {
            topicPresenter.showImage(topicImage, imageURL);

            new DownloadImageTask(topicImage).execute(imageURL);
        }

        String audioURL = currentTopic.getAudioURL();

        if(!audioURL.trim().equals("N")) {
            playAudioButton.setVisibility(View.VISIBLE);
        }

        nameAuthorTextView.setText(currentTopic.getNameOwner());
        titleTextView.setText(currentTopic.getTitle());
        contentTextView.setText(currentTopic.getDescription());

        TopicEvaluationPresenter topicEvaluationPresenter = TopicEvaluationPresenter.
                getInstance(getContext());

        try {
            topicEvaluation = topicEvaluationPresenter.getTopicEvaluation(idTopic);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        topicEvaluationTextView.setText(topicEvaluation + "");

        UserPresenter userPresenter = UserPresenter.getInstance(getContext());
        User user = null;
        try{
            user = userPresenter.getUser(currentTopic.getIdOwner());
        } catch(UserException e){
            e.printStackTrace();
        } catch(JSONException e){
            e.printStackTrace();
        }

        Drawable userPhoto = userPresenter.getClassificationIcon(user.getIdClassification());
        photo.setImageDrawable(userPhoto);
        classification.setText(user.getClassification());
    }

    private void openFragment(Fragment fragmentToBeOpen){
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                this.getActivity().getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.layout_main, fragmentToBeOpen);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        TopicEvaluationPresenter topicEvaluationPresenter =
                TopicEvaluationPresenter.getInstance(getContext());

        final Integer POSITIVE_EVALUATION = 1;
        final Integer NEGATIVE_EVALUATION = -1;

        SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(getContext());
        int loggedUserId = session.getInt("id", -1);

        switch (v.getId()) {
            case R.id.up_evaluation:
                try {
                    Integer currentEvaluation = topicEvaluationPresenter.
                            getTopicEvaluationByUserId(idTopic, loggedUserId);

                    Log.d("current evaluation", currentEvaluation + "");

                    if(currentEvaluation == 0 || currentEvaluation == -1) {

                        topicEvaluationPresenter.evaluateTopic(currentTopic.getIdTopic(),
                                currentTopic.getIdCategory(), currentTopic.getIdOwner(),
                                POSITIVE_EVALUATION, loggedUserId);

                    }else {
                        topicEvaluationPresenter.deleteTopicEvaluation(idTopic, loggedUserId);
                    }

                    if(currentEvaluation == 0) {
                        topicEvaluation++;
                    } else if(currentEvaluation == 1) {
                        topicEvaluation--;
                    } else if(currentEvaluation == -1) {
                        topicEvaluation += 2;
                    }

                    topicEvaluationTextView.setText(topicEvaluation + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.down_evaluation:
                try {
                    Integer currentEvaluation = topicEvaluationPresenter.
                            getTopicEvaluationByUserId(idTopic, loggedUserId);

                    Log.d("current evaluation", currentEvaluation + "");

                    if(currentEvaluation == 0 || currentEvaluation == 1) {
                        topicEvaluationPresenter.evaluateTopic(currentTopic.getIdTopic(),
                                currentTopic.getIdCategory(), currentTopic.getIdOwner(),
                                NEGATIVE_EVALUATION, loggedUserId);
                    }else {
                        topicEvaluationPresenter.deleteTopicEvaluation(idTopic, loggedUserId);
                    }

                    if(currentEvaluation == 0) {
                        topicEvaluation--;
                    } else if(currentEvaluation == 1) {
                        topicEvaluation -= 2;
                    } else if(currentEvaluation == -1) {
                        topicEvaluation++;
                    }

                    topicEvaluationTextView.setText(topicEvaluation + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.topicViewPlayAudio:
                TopicPresenter topicPresenter = TopicPresenter.getInstance(getContext());

                try {
                    topicPresenter.playAudio(idTopic);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.create_comment:
                CommentCreation commentCreation = new CommentCreation();

                Bundle bundle = new Bundle();

                bundle.putInt("idTopic", idTopic);
                bundle.putInt("idCategory", idCategory);

                commentCreation.setArguments(bundle);

                openFragment(commentCreation);

                break;

            default:
        }

    }

    @Override
    public void onDestroy(){
        if(this.getArguments().getBoolean("comeFromSearch")){
            getActivity().finish();
        }

        super.onDestroy();
    }

}
