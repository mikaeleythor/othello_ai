public class LookUpTable {
    private int[][] board;

    public LookUpTable(int size) {
        board = new int[size][size];
        int half = size / 2;

        int layer = half + 1;
        int points;

        int isChangingFromTop = 0; //we want 1 two times before we change top layer to false.
        boolean isTop = true;
        boolean isLeft = true;

        for (int row = 0; row < board.length; row++) {
            points = half + 1;

            if (isTop && layer-1 != 0) //THERE IS SOMETHING WHEN WE ARE CHANGING TO THE BOTTOM LAYER!!!??!??!?!?!
                layer--;
            else if (layer-1 != 0){
                layer++;
            }
                
            if (isChangingFromTop == 2) {
                isChangingFromTop = 0;
                isTop = false;
                layer++;
                //System.out.println("IS TOP: LAYER: " + layer);
            }
                
           

            int sameNumber = 0;
            for (int col = 0; col < board.length; col++) {
               
                //System.out.println("LAYER: " + layer + ", POINTS: " + points);
                if (isCorner(col, row))
                    board[row][col] = half + 1;
                else {
                    if (!isLeftHalf(col)) {
                        if (sameNumber != layer) {
                            sameNumber++;
                            board[row][col] = points;
                        }

                        else {
                            points++;
                            board[row][col] = points;
                        }
                    } else {
                        if (layer < points) {
                            points--;
                        }
                        board[row][col] = points;
                    }

                }
            }

            if (layer == 1) {
                isChangingFromTop++;
                //System.out.println("IS CHANG: LAYER: " + layer + ", " + isChangingFromTop);
            }
                
        }

        System.out.println(printBoard());

    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    private boolean isCorner(int col, int row) {
        return (row == 0 && col == 0) || (row == 0 && col == board.length - 1) || (row == board.length - 1 && col == 0)
                || (row == board.length - 1 && col == board.length - 1);
    }

    private String printBoard() {
        String s = "";
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                s += " " + board[r][c] + " ";
            }
            s += "\n";
        }
        return s;
    }

    private boolean isLeftHalf(int col) {
        return col < board.length / 2;
    }

    private boolean isTopHalf(int row) {
        return row < board.length / 2;
    }

}
