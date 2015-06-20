package io.github.yfwz100.tictactoe;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @SmallTest
    public void testModel() {
        Board board = new Board();
        board.place(0, 0, Board.Mark.A);
        Agent agent = Agent.getInstance();
        Agent.Choice choice = agent.getBestChoice(board, Board.Mark.B);
        Log.e(getClass().getName(), choice.toString());
    }
}