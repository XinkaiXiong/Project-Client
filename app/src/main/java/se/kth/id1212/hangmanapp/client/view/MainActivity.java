package se.kth.id1212.hangmanapp.client.view;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import se.kth.id1212.hangmanapp.R;
import se.kth.id1212.hangmanapp.client.net.Connect;
import se.kth.id1212.hangmanapp.client.net.HandleServerResponse;

public class MainActivity extends AppCompatActivity {

    private Connect server = new Connect();
    private boolean connect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        submitButtonListener();
        newGameButtonListener();
        quitButtonListener();

        new NetworkHandler("Connect").execute();
    }

    private void quitButtonListener() {

        Button quitButton = (Button) findViewById(R.id.quit_button);


        assert quitButton != null;
        quitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void newGameButtonListener() {

        Button newGameButton = (Button) findViewById(R.id.new_game_button);


        assert newGameButton != null;
        newGameButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        new NetworkHandler("New Game").execute();
                    }
                }
        );
    }

    private void submitButtonListener() {

        Button submitButton = (Button) findViewById(R.id.submit_button);


        assert submitButton != null;
        submitButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        EditText userGuess = (EditText) findViewById(R.id.user_guess);
                        userGuess.setGravity(Gravity.CENTER_HORIZONTAL);
                        String userGuessString = userGuess.getText().toString();
                        userGuess.getText().clear();
                        new NetworkHandler(userGuessString).execute();
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            server.disconnect("Quit");
        } catch (IOException e) {
            e.printStackTrace();
        }
        connect = false;
    }

    private class NetworkHandler extends AsyncTask<Void, Void, Void> {

        private String message;

        public NetworkHandler(String message) {
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... params) {


            if (!connect) {
                try {
                    server.connect("130.229.178.234", 8080, new Printer());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connect = true;
            }

            server.sendMessage(message);


            return null;
        }
    }

    private class Printer implements HandleServerResponse {

        @Override
        public void handleResponse(final String response) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView wordText = (TextView) findViewById(R.id.word_text);

                    wordText.setGravity(Gravity.CENTER_HORIZONTAL);

                    if (response.contains("#")) {
                        TextView numGuesses = (TextView) findViewById(R.id.num_guesses);
                        TextView score = (TextView) findViewById(R.id.score);

                        SplitResponse parser = new SplitResponse(response);
                        wordText.setText(parser.getMessage());
                        numGuesses.setText(parser.getNumOfGuesses());

                        if (parser.getScore() != null) {
                            score.setText(parser.getScore());
                        }
                    } else wordText.setText(response);
                }
            });
        }
    }


}
