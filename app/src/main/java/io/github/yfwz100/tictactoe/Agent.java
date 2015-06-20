package io.github.yfwz100.tictactoe;

import java.util.Arrays;

/**
 * The TicTacToe Model.
 *
 * @author Wang Zhi
 */
public class Agent {

    private final static Agent agent = new Agent();

    public static class Choice {
        private int x;
        private int y;
        private int utility;

        public Choice() {
        }

        public Choice(int x, int y, int utility) {
            this.x = x;
            this.y = y;
            this.utility = utility;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getUtility() {
            return utility;
        }

        public void setUtility(int utility) {
            this.utility = utility;
        }

        @Override
        public String toString() {
            return "Choice{" +
                    "x=" + x +
                    ", y=" + y +
                    ", utility=" + utility +
                    '}';
        }
    }

    private Agent() {
        // singleton.
    }

    protected int getChancesToWin(Board board, Board.Mark mark) {
        int chances = 0;
        // row first.
        for (int i = 0; i < 3; i++) {
            boolean isPossible = true;
            for (int j = 0; j < 3; j++) {
                if (board.get(i, j) == mark.getOpponent()) {
                    isPossible = false;
                    break;
                }
            }
            if (isPossible) {
                chances += 1;
            }
        }
        // column first.
        for (int i = 0; i < 3; i++) {
            boolean isPossible = true;
            for (int j = 0; j < 3; j++) {
                if (board.get(j, i) == mark.getOpponent()) {
                    isPossible = false;
                    break;
                }
            }
            if (isPossible) {
                chances += 1;
            }
        }
        // cross.
        if (board.flat(0) != mark.getOpponent()
                && board.flat(4) != mark.getOpponent() && board.flat(8) != mark.getOpponent()) {
            chances += 1;
        }
        if (board.flat(2) != mark.getOpponent()
                && board.flat(4) != mark.getOpponent() && board.flat(6) != mark.getOpponent()) {
            chances += 1;
        }
        return chances;
    }

    protected int getMinimumStepsToWin(Board board, Board.Mark mark) {
        int minSteps = 3;
        // Check row.
        for (int i = 0; i < 3; i++) {
            int steps = 3;
            for (int j = 0; j < 3; j++) {
                if (board.get(i, j) == mark.getOpponent()) {
                    steps = 3;
                    break;
                }
                if (board.get(i, j) == mark) {
                    steps -= 1;
                }
            }
            if (minSteps > steps) {
                minSteps = steps;
            }
        }
        // Check column.
        for (int i = 0; i < 3; i++) {
            int steps = 3;
            for (int j = 0; j < 3; j++) {
                if (board.get(j, i) == mark.getOpponent()) {
                    steps = 3;
                    break;
                }
                if (board.get(j, i) == mark) {
                    steps -= 1;
                }
            }
            if (minSteps > steps) {
                minSteps = steps;
            }
        }
        // Check crossing.
        for (Iterable<Integer> list :
                Arrays.asList(Arrays.asList(0, 4, 8), Arrays.asList(2, 4, 6))) {
            int steps = 3;
            for (int i : list) {
                if (board.flat(i) == mark.getOpponent()) {
                    steps = 3;
                    break;
                }
                if (board.flat(i) == mark) {
                    steps -= 1;
                }
            }
            if (minSteps > steps) {
                minSteps = steps;
            }
        }
        return minSteps;
    }

    protected int getUtility(Board board, Board.Mark mark, int x, int y, int steps) {
        if (board.get(x, y) == Board.Mark.NA) {
            // Make a copy to ensure not to pollute the original board.
            board = board.clone();
            board.place(x, y, mark);
            if (steps == 1) {
                int opponentChances = 10 - getChancesToWin(board, mark.getOpponent());
                int myChances = getChancesToWin(board, mark);
                int opponentMinSteps = getMinimumStepsToWin(board, mark.getOpponent());
                int myMinSteps = 10 - getMinimumStepsToWin(board, mark);
                return myMinSteps * 1000 + opponentMinSteps * 100 + opponentChances * 10 + myChances;
            } else {
                Choice opponent = getBestChoice(board, mark.getOpponent(), steps - 1);
                board.place(opponent.getX(), opponent.getY(), mark.getOpponent());
                Choice mine = getBestChoice(board, mark, steps - 1);
                return mine.getUtility();
            }
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public Choice getBestChoice(Board board, Board.Mark mark, int steps) {
        int maxUtility = Integer.MIN_VALUE;
        Board.BoardCell position = null;
        for (Board.BoardCell pos : board) {
            int utility = getUtility(board, mark, pos.getX(), pos.getY(), steps);
            if (maxUtility < utility) {
                maxUtility = utility;
                position = pos;
            }
        }
        if (position != null) {
            return new Choice(position.getX(), position.getY(), maxUtility);
        } else {
            return null;
        }
    }

    public Choice getBestChoice(Board board, Board.Mark mark) {
        return getBestChoice(board, mark, 1);
    }

    public static Agent getInstance() {
        return agent;
    }

}
