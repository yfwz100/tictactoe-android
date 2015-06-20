package io.github.yfwz100.tictactoe.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.yfwz100.tictactoe.Agent;
import io.github.yfwz100.tictactoe.Board;
import io.github.yfwz100.tictactoe.R;

/**
 * The main activity.
 *
 * @author yfwz100
 */
public class MainActivity extends AppCompatActivity implements Board.BoardCellUpdateNotifier {

    private final Agent agent = Agent.getInstance();

    private final Button[] cellButtons = new Button[9];
    private TextView messageText;

    private Board board;

    private int firstPlayer = 0;

    public void initGame() {
        board = new Board();
        board.addCellNotifier(this);

        for (Button btn : cellButtons) {
            btn.setText(R.string.empty_text);
        }

        enableBoardCells();

        messageText.setText(R.string.welcome_text);

        if (firstPlayer == 1) {
            new AgentChoiceTask().execute();
        }
    }

    protected void enableBoardCells() {
        for (Button btn : cellButtons) {
            if (btn.getText().equals(getString(R.string.empty_text))) {
                btn.setEnabled(true);
            }
        }
    }

    protected void disableBoardCells() {
        for (Button btn : cellButtons) {
            btn.setEnabled(false);
        }
    }

    protected class GameStatusTask extends AsyncTask<Void, Integer, Board.Status> {

        @Override
        protected void onPreExecute() {
            disableBoardCells();
        }

        @Override
        protected Board.Status doInBackground(Void... params) {
            return board.getStatus();
        }

        @Override
        protected void onPostExecute(Board.Status status) {
            switch (status) {
                case A_WIN: {
                    messageText.setText(R.string.player_win_text);
                    disableBoardCells();
                    break;
                }
                case B_WIN: {
                    messageText.setText(R.string.computer_win_text);
                    disableBoardCells();
                    break;
                }
                case END: {
                    messageText.setText(R.string.none_win_text);
                    disableBoardCells();
                    break;
                }
                default:
                    onGameContinues();
                    enableBoardCells();
            }
        }

        protected void onGameContinues() {
        }
    }

    protected class AgentChoiceTask extends AsyncTask<Void, Integer, Agent.Choice> {

        @Override
        protected Agent.Choice doInBackground(Void... params) {
            return agent.getBestChoice(board, Board.Mark.B);
        }

        @Override
        protected void onPreExecute() {
            disableBoardCells();
        }

        @Override
        protected void onPostExecute(Agent.Choice choice) {
            if (choice != null) {
                board.place(choice.getX(), choice.getY(), Board.Mark.B);
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_choices_info, Toast.LENGTH_LONG).show();
            }

            enableBoardCells();

            new GameStatusTask().execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View.OnClickListener btnClickedListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btn = ((Button) v);
                for (int i = 0; i < cellButtons.length; i++) {
                    if (btn == cellButtons[i]) {
                        int x = i / 3, y = i % 3;
                        board.place(x, y, Board.Mark.A);

                        new GameStatusTask() {
                            @Override
                            protected void onGameContinues() {
                                new AgentChoiceTask().execute();
                            }
                        }.execute();

                        break;
                    }
                }
            }
        };

        ArrayList<Button> btnList = new ArrayList<>();
        for (int btnId : Arrays.asList(R.id.cell0, R.id.cell1, R.id.cell2,
                R.id.cell3, R.id.cell4, R.id.cell5, R.id.cell6, R.id.cell7, R.id.cell8)) {
            Button btn = ((Button) findViewById(btnId));
            btn.setText(R.string.empty_text);
            btn.setOnClickListener(btnClickedListener);
            btnList.add(btn);
        }
        btnList.toArray(this.cellButtons);

        messageText = ((TextView) findViewById(R.id.message));

        initGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setView(getLayoutInflater().inflate(R.layout.activity_about, null))
                        .setCancelable(true);
                builder.create().show();
                break;
            }
            case R.id.action_settings: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle(R.string.first_player_setting)
                        .setSingleChoiceItems(R.array.player_list, firstPlayer, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firstPlayer = which;
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                initGame();
                            }
                        })
                        .setCancelable(true);
                builder.create().show();
                break;
            }
            case R.id.action_new: {
                initGame();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void notifyChanged(final int x, final int y, final Board.Mark mark) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button btn = cellButtons[x * 3 + y];
                btn.setText(mark == Board.Mark.A ? R.string.cell_a_check : R.string.cell_b_check);
                btn.setEnabled(false);
            }
        });
    }
}
