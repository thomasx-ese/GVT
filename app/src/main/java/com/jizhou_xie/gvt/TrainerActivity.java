package com.jizhou_xie.gvt;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jizhou_xie.gvt.logic.Result;
import com.jizhou_xie.gvt.logic.WordList;
import com.jizhou_xie.gvt.logic.old.Word;

public class TrainerActivity extends AppCompatActivity {

    private Word.WordPurpose currenstPurpose;
    private WordList wordList;

    private TextView textViewDe = (TextView)findViewById(R.id.text_de);
    private TextView textViewZh = (TextView)findViewById(R.id.text_zh);
    private TextView textViewGender = (TextView)findViewById(R.id.text_gender);
    private TextView textViewPlural = (TextView)findViewById(R.id.text_pl);

    private Button leftButton = (Button)findViewById(R.id.button_left);
    private Button middleButton = (Button)findViewById(R.id.button_middle);
    private Button rightButton = (Button)findViewById(R.id.button_right);

    private final View.OnClickListener listener_learn_right = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.RIGHT);
        }
    };
    private final View.OnClickListener listener_learn_next = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.NEXT);
        }
    };
    private final View.OnClickListener listener_learn_postpone = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.POSTPONE);
        }
    };
    private final View.OnClickListener listener_review_right = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.RIGHT);
        }
    };
    private final View.OnClickListener listener_review_wrong = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.WRONG);
        }
    };
    private final View.OnClickListener listener_review_discard = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.RIGHT.DISCARD);
        }
    };
    private final View.OnClickListener listener_test_right = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.RIGHT);
        }
    };;
    private final View.OnClickListener listener_test_wrong = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.WRONG);
        }
    };
    private final View.OnClickListener listener_test_discard = new View.OnClickListener() {
        public void onClick(View v) {
            wordList.processResult(Result.DISCARD);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /* >> Initialization */

        /* Initialization << */

        //Temp!!
        this.middleButton.setText("Right");
        this.rightButton.setText("Wrong");
        this.leftButton.setText("Discard");

        //Temp!!

        //TextView displayPurpose = (TextView)findViewById(R.id.display_purpose);

        //Intent intent = this.getIntent();

        //Word.WordPurpose purpose = (Word.WordPurpose) intent.getExtras().getSerializable(ExtraKeys.EXTRA_MAIN_TO_TRAINER_PURPOSE);
        //displayPurpose.setText(purpose.name());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trainer_menu, menu);

        return true;
    }

    public void requestAndDisplayNextWord() {

        WordList.NextFeedback next_feedback = this.wordList.next();
        switch (next_feedback) {
            case SUCCESS:
                this.displayCoveredWord(this.wordList.get(),this.currenstPurpose);
            break;
            case EMPTY_LIST:

            break;
            case EMPTY_SUBLIST:

            break;
        }
    }

    public void displayCoveredWord(Word word, Word.WordPurpose purpose) {
        this.textViewDe.setText(word.getSpelling());
        this.textViewZh.setText(word.getMeaning());
        if(word.getType() == Word.WordType.NOUN) {
            this.textViewGender.setText(word.getGender().getOutput());
            this.textViewPlural.setText(word.getGender().getOutput());
        }
    }
}
