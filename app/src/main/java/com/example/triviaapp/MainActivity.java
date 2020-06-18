package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.data.AnswerListAsyncResponse;
import com.example.triviaapp.data.QuestionBank;
import com.example.triviaapp.model.Question;
import com.example.triviaapp.model.Score;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView, questionCounterTextView,scoreTextView,highestScoreTextView;
    private Button trueButton, falseButton;
    private ImageButton prevButton, nextButton;
    private int currentQuestionIndex = 0;
    private static final String MESSAGE_ID = "messages_prefs" ;
    private List<Question> questionList = new ArrayList<>();

    private int scoreCounter = 0;
    private Prefs prefs;
    private Score score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score(); //score object
        prefs = new Prefs(MainActivity.this);


        questionCounterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textView);
        trueButton = findViewById(R.id.buttonTrue);
        falseButton = findViewById(R.id.buttonFalse);
        prevButton = findViewById(R.id.preButton);
        nextButton = findViewById(R.id.nextButton);
        scoreTextView = findViewById(R.id.score);
        highestScoreTextView = findViewById(R.id.highestScore);

        nextButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);

        highestScoreTextView.setText(MessageFormat.format(" Highest Score: {0}", String.valueOf(prefs.getHighScore())));

        currentQuestionIndex = prefs.getState();

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public ArrayList<Question> processFinished(ArrayList<Question> questionArrayList) {

                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAns());
                questionCounterTextView.setText(currentQuestionIndex + " / " + questionArrayList.size()); // 0 / 234
                Log.d("Inside", "processFinished: " + questionArrayList);

                return questionArrayList;

            }
        });


    }

    private void shareScore() {

        String message = "My current score is " + score.getScore() + " and "
                + "My highest score is " + prefs.getHighScore();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "I am Playing Trivia");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(intent);

    }

    public void addPoints()
    {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

    }

    public void deductPoints()
    {
        scoreCounter -= 100;
        if (scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        }else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
            //Log.d("Score Bad", "deductPoints: " + score.getScore());
        }

    }



    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.buttonTrue:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.buttonFalse:
                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.preButton:
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.nextButton:
                goNext();
                break;
        }

    }

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerIsTrue) {

            fadeView();
            addPoints();
            toastMessageId = R.string.correct_answer;

        } else {
            shakeAnimation();
            deductPoints();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void fadeView() {
        final CardView cardView = findViewById(R.id.radius);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void updateQuestion() {
        if(questionList != null){
        String question = questionList.get(currentQuestionIndex).getAns();
        questionTextView.setText(question);
        questionCounterTextView.setText(currentQuestionIndex + " / " + questionList.size()); }
        else {
            Toast.makeText(this,"Question list is null",Toast.LENGTH_SHORT).show();
        }

    }

    private void shakeAnimation()
    {
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.radius);
        cardView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
               goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void goNext()
    { currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }

}
