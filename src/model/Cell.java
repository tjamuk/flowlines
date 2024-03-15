package model;

import java.util.Objects;

public class Cell {
    private final int col;
    private final int row;

    public Cell(int col, int row)
    {
        this.col = col;
        this.row = row;
    }

    public int getCol()
    {
        return col;
    }

    public int getRow()
    {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return getCol() == cell.getCol() && getRow() == cell.getRow();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCol(), getRow());
    }

    @Override
    public String toString() {

        return "{" + col + "," + row + "}" ;
    }
}
