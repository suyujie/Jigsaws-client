package com.sw.jigsaws.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sw.jigsaws.R;

public class Tutorial extends Activity {

    private TextView tutorialView;
    private Button tutorialButton;
    private int tutorialIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        tutorialView = (TextView) this.findViewById(R.id.tutorial_desc);
        tutorialButton = (Button) this.findViewById(R.id.tutorial_button);

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Log.d("----------","-----"+tutorialIndex);

                tutorialIndex++;
                if (tutorialIndex <= 3) {
                    tutorialView.setText(getResources().getIdentifier("tutorial_desc_" + tutorialIndex, "string", getPackageName()));
                    if (tutorialIndex == 3) {
                        tutorialButton.setText(R.string.tutorial_button_join);
                    }
                } else {

                    Log.d("----------","-----"+tutorialIndex);

                    Intent intent = new Intent(Tutorial.this, Home.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


    }


}
