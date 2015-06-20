package io.github.yfwz100.tictactoe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static io.github.yfwz100.tictactoe.Board.Mark.*;

/**
 * The board of the TicTacToe game.
 */
public class Board implements Iterable<Board.BoardCell>, Cloneable, Serializable {

    public interface BoardCellUpdateNotifier {
        void notifyChanged(int x, int y, Mark mark);
    }

    private class BoardCellIterator implements Iterator<BoardCell> {
        private int index = -1;

        @Override
        public boolean hasNext() {
            return index < data.length - 1;
        }

        @Override
        public BoardCell next() {
            index += 1;
            return new BoardCell(index / 3, index % 3, data[index]);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Couldn't remove the position.");
        }
    }

    public static class BoardCell {
        private int x;
        private int y;
        private Mark mark;

        public BoardCell() {
        }

        public BoardCell(int x, int y, Mark mark) {
            this.x = x;
            this.y = y;
            this.mark = mark;
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

        public Mark getMark() {
            return mark;
        }

        public void setMark(Mark mark) {
            this.mark = mark;
        }
    }

    public enum Mark {
        A(1), B(-1), NA(0);
        private final int value;

        Mark(int value) {
            this.value = value;
        }

        public Mark getOpponent() {
            if (this == A) {
                return B;
            } else if (this == B) {
                return A;
            } else {
                return NA;
            }
        }

        public int getValue() {
            return value;
        }
    }

    public enum Status {
        RUNNING, A_WIN, B_WIN, END
    }

    private transient List<BoardCellUpdateNotifier> notifiers = new ArrayList<>();

    private Mark[] data = {NA, NA, NA, NA, NA, NA, NA, NA, NA};

    public void addCellNotifier(BoardCellUpdateNotifier notifier) {
        notifiers.add(notifier);
    }

    public void removeCellNotifier(BoardCellUpdateNotifier notifier) {
        notifiers.remove(notifier);
    }

    public List<BoardCellUpdateNotifier> getNotifiers() {
        return Collections.unmodifiableList(notifiers);
    }

    public void place(int x, int y, Mark mark) {
        this.data[x * 3 + y] = mark;
        for (BoardCellUpdateNotifier notifier : notifiers) {
            notifier.notifyChanged(x, y, mark);
        }
    }

    public void place(BoardCell pos) {
        place(pos.x, pos.y, pos.mark);
    }

    public Mark get(int x, int y) {
        return data[x * 3 + y];
    }

    public Mark flat(int i) {
        return data[i];
    }

    public Status getStatus() {
        if (Agent.getInstance().getMinimumStepsToWin(this, A) == 0) {
            return Status.A_WIN;
        } else if (Agent.getInstance().getMinimumStepsToWin(this, B) == 0) {
            return Status.B_WIN;
        }
        boolean isFull = true;
        for (Mark aData : data) {
            if (aData == NA) {
                isFull = false;
            }
        }
        if (isFull) {
            return Status.END;
        }
        return Status.RUNNING;
    }

    @Override
    public Iterator<BoardCell> iterator() {
        return new BoardCellIterator();
    }

    @Override
    public Board clone() {
        Board cloned = new Board();
        cloned.data = this.data.clone();
        return cloned;
    }

}
