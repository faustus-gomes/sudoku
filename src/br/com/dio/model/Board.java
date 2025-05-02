package br.com.dio.model;

import java.util.Collection;
import java.util.List;

import static br.com.dio.Main.BOARD_LIMIT;
import static br.com.dio.model.GameStatusEnum.COMPLETE;
import static br.com.dio.model.GameStatusEnum.INCOMPLETE;
import static br.com.dio.model.GameStatusEnum.NON_STARTED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus(){
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))){
            return NON_STARTED;
        }

        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    public boolean hasErrors(){
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Integer actual = spaces.get(i).get(j).getActual();
                if (actual != null && !isValid(i, j, actual)) {
                    System.out.println("Erro na posição [" + i + "," + j + "] - Valor: " + actual);
                    return true;
                }
            }
        }
        return false;
        /*8if(getStatus() == NON_STARTED){
            return false;
        }

        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));*/
    }
    // Em Board.java, altere o método isValid:
    private boolean isValid(int row, int col, int value) {
        // Remove a verificação do valor esperado se quiser apenas checar regras básicas
        return checkRow(row, value) && checkColumn(col, value) && checkBlock(row, col, value);
    }

    private boolean checkRow(int row, int value) {
        // Verifica se o valor já existe na linha
        for (int col = 0; col < BOARD_LIMIT; col++) {
            Integer current = spaces.get(row).get(col).getActual();
            if (current != null && current == value) {
                return false; // Encontrou valor repetido na linha
            }
        }
        return true;
    }

    private boolean checkColumn(int col, int value) {
        // Verifica se o valor já existe na coluna
        for (int row = 0; row < BOARD_LIMIT; row++) {
            Integer current = spaces.get(row).get(col).getActual();
            if (current != null && current == value) {
                return false; // Encontrou valor repetido na coluna
            }
        }
        return true;
    }

    private boolean checkBlock(int row, int col, int value) {
        // Verifica o bloco 3x3
        int blockStartRow = (row / 3) * 3;
        int blockStartCol = (col / 3) * 3;

        for (int i = blockStartRow; i < blockStartRow + 3; i++) {
            for (int j = blockStartCol; j < blockStartCol + 3; j++) {
                Integer current = spaces.get(i).get(j).getActual();
                if (current != null && current == value) {
                    return false; // Encontrou valor repetido no bloco
                }
            }
        }
        return true;
    }

    public boolean isValidMove(int row, int col, int value) {
        // Verifica se é uma jogada válida
        return value >= 1 && value <= 9 &&
                checkRow(row, value) &&
                checkColumn(col, value) &&
                checkBlock(row, col, value);
    }

    public boolean changeValue(final int col, final int row, final int value){
        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }

        space.setActual(value);
        return true;
    }

    public boolean clearValue(final int col, final int row){
        var space = spaces.get(col).get(row);
        if (space.isFixed()){
            return false;
        }

        space.clearSpace();
        return true;
    }

    public void reset(){
        spaces.forEach(c -> c.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished(){
        return !hasErrors() && getStatus().equals(COMPLETE);
    }
}
