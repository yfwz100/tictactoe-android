package io.github.yfwz100.tictactoe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static io.github.yfwz100.tictactoe.Board.Mark.*;

/**
 * The board of the TicTacToe game.
 *
 * @author yfwz100
 */
public class Board implements Iterable<Board.BoardCell>, Cloneable, Serializable {

    /**
     * Notifies the changes of board cell.
     *
     * @author yfwz100
     */
    public interface BoardCellChangeListener {
        void notifyChanged(int x, int y, Mark mark);
    }

    /**
     * The iterator of the board cell.
     *
     * @author yfwz100
     */
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

    /**
     * The board cell. It records the position of the cell and the mark.
     *
     * @author yfwz100
     */
    public static class BoardCell {
        private int x;
        private int y;
        private Mark mark;

        /**
         * Construct an empty cell.
         */
        public BoardCell() {
        }

        /**
         * Construct a board cell.
         *
         * @param x    the position x.
         * @param y    the position y.
         * @param mark the mark.
         */
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

    /**
     * The mark of a cell.
     *
     * @author yfwz100
     */
    public enum Mark {
        A, B, NA;

        /**
         * A convenient method to get the opponent. `NA` maps to `NA` itself.
         *
         * @return the mark of the opponent.
         */
        public Mark getOpponent() {
            if (this == A) {
                return B;
            } else if (this == B) {
                return A;
            } else {
                return NA;
            }
        }
    }

    /**
     * The status of the game.
     *
     * @author yfwz100
     */
    public enum Status {
        RUNNING, A_WIN, B_WIN, END
    }

    private transient List<BoardCellChangeListener> notifiers = new ArrayList<>();

    private Mark[] data = {NA, NA, NA, NA, NA, NA, NA, NA, NA};

    /**
     * Register board cell change listener to the board.
     *
     * @param listener the listener.
     */
    public void addCellNotifier(BoardCellChangeListener listener) {
        notifiers.add(listener);
    }

    /**
     * Remove board cell change listener from the board.
     *
     * @param listener the listener.
     */
    public void removeCellNotifier(BoardCellChangeListener listener) {
        notifiers.remove(listener);
    }

    /**
     * Get the list of the board cell change listeners.
     *
     * @return the unmodifiable collection of listeners.
     */
    public List<BoardCellChangeListener> getNotifiers() {
        return Collections.unmodifiableList(notifiers);
    }

    /**
     * Place the mark on the given position.
     *
     * @param x the position x.
     * @param y the position y.
     * @param mark the mark.
     */
    public void place(int x, int y, Mark mark) {
        this.data[x * 3 + y] = mark;
        for (BoardCellChangeListener notifier : notifiers) {
            notifier.notifyChanged(x, y, mark);
        }
    }

    /**
     * Place the mark on the given position, using the encapsulated {@link BoardCell} object.
     *
     * @see #place(int, int, Mark)
     * @param pos the cell to replace.
     */
    public void place(BoardCell pos) {
        place(pos.x, pos.y, pos.mark);
    }

    /**
     * Get the mark on the given position.
     *
     * @param x the position x.
     * @param y the position y.
     * @return the mark.
     */
    public Mark get(int x, int y) {
        return data[x * 3 + y];
    }

    /**
     * Get the mark according to the flatted index (in row order).
     *
     * @param i the index.
     * @return the mark.
     */
    public Mark flat(int i) {
        return data[i];
    }

    /**
     * Get the status of the game.
     *
     * @return the status.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<BoardCell> iterator() {
        return new BoardCellIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board clone() {
        Board cloned = new Board();
        cloned.data = this.data.clone();
        return cloned;
    }

}
