public class LookUpTable {
    private int[][] board;

    public LookUpTable(int size) {
        if (size == 4)
            board = fourBoard();
        if (size == 6)
            board = sixBoard();
        if (size == 8)
            board = eightBoard();
    }

    public int[][] fourBoard() {
        return new int[][] {
                new int[] { 2, -1, -1, 2 },
                new int[] { -1, 0, 0, -1 },
                new int[] { -1, 0, 0, -1 },
                new int[] { 2, -1, -1, 2 }
        };
    }

    public int[][] sixBoard(){
        return new int[][] {
            new int[] {4, -2, 2, 2, -2, 4},
            new int[] {-2, -4, 0, 0, -4, -2},
            new int[] {2, 0, 0, 0, 0, 2},
            new int[] {2, 0, 0, 0, 0, 2},
            new int[] {-2, -4, 0, 0, -4, -2},
            new int[] {4, -2, 2, 2, -2, 4}            
        };
    }

    public int[][] eightBoard() {
        return new int[][] {
                new int[] { 4, -3, 2, 2, 2, 2, -3, 4 },
                new int[] { -3, -4, -1, -1, -1, -1, -4, -3 },
                new int[] { 2, -1, 1, 0, 0, 1, -1, 2},
                new int[] { 2, -1, 0, 1, 1, 0, -1, 2 },
                new int[] { 2, -1, 0, 1, 1, 0, -1, 2 },
                new int[] { 2, -1, 1, 0, 0, 1, -1, 2},
                new int[] { -3, -4, -1, -1, -1, -1, -4, -3 },
                new int[] { 4, -3, 2, 2, 2, 2, -3, 4 }
        };
    }

    public int[][] getBoard() {
        return board;
    }



    /*
     * board = new int[size][size];
     * int half = size / 2;
     * 
     * int layer = half + 1;
     * int points;
     * 
     * int isChangingFromTop = 0; //we want 1 two times before we change top layer
     * to false.
     * boolean isTop = true;
     * 
     * 
     * for (int row = 0; row < board.length; row++) {
     * points = half + 1;
     * 
     * if (isTop && layer-1 != 0) //THERE IS SOMETHING WHEN WE ARE CHANGING TO THE
     * BOTTOM LAYER!!!??!??!?!?!
     * layer--;
     * else if (layer-1 != 0){
     * layer++;
     * }
     * 
     * if (isChangingFromTop == 2) {
     * isChangingFromTop = 0;
     * isTop = false;
     * layer++;
     * //System.out.println("IS TOP: LAYER: " + layer);
     * }
     * 
     * 
     * 
     * int sameNumber = 0;
     * for (int col = 0; col < board.length; col++) {
     * 
     * //System.out.println("LAYER: " + layer + ", POINTS: " + points);
     * if (isCorner(col, row))
     * board[row][col] = half + 2;
     * else if (isBesidesCorner(col, row))
     * board[row][col] = half/2+1;
     * else {
     * if (!isLeftHalf(col)) {
     * if (sameNumber != layer) {
     * sameNumber++;
     * board[row][col] = points;
     * }
     * 
     * else {
     * points++;
     * board[row][col] = points;
     * }
     * } else {
     * if (layer < points) {
     * points--;
     * }
     * board[row][col] = points;
     * }
     * 
     * }
     * }
     * 
     * if (layer == 1) {
     * isChangingFromTop++;
     * //System.out.println("IS CHANG: LAYER: " + layer + ", " + isChangingFromTop);
     * }
     * 
     * }
     * 
     * System.out.println(printBoard());
     * 
     * }
     * 
     * public int[][] getBoard() {
     * return board;
     * }
     * 
     * public void setBoard(int[][] board) {
     * this.board = board;
     * }
     * 
     * private boolean isCorner(int col, int row) {
     * return (row == 0 && col == 0) || (row == 0 && col == board.length - 1) ||
     * (row == board.length - 1 && col == 0)
     * || (row == board.length - 1 && col == board.length - 1);
     * }
     * 
     * private boolean isBesidesCorner(int col, int row) {
     * return (row == 0 && col == 1) || (row == 1 && col == 0) || (row == 0 && col
     * == board.length - 2) || (row == 1 && col == board.length - 1) || (row ==
     * board.length - 2 && col == 0) || (row == board.length - 1 && col == 1)
     * || (row == board.length - 2 && col == board.length - 1) || (row ==
     * board.length - 1 && col == board.length - 2);
     * }
     * 
     * private String printBoard() {
     * String s = "";
     * for (int r = 0; r < board.length; r++) {
     * for (int c = 0; c < board[r].length; c++) {
     * s += " " + board[r][c] + " ";
     * }
     * s += "\n";
     * }
     * return s;
     * }
     * 
     * private boolean isLeftHalf(int col) {
     * return col < board.length / 2;
     * }
     */
}
