package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.List;

public class Solution {
    private static final Lee lee = new Lee(15, 15);

    public static Direction solve(Board board){
        Point apple = board.getApples().get(0);
        List<Point> obstacles = board.getBarriers();
        Point start = board.getHead();
        Point stone = board.getStones().get(0);

        if(lee.trace(start, apple, obstacles).isPresent()){
            List<LeePoint> path = lee.trace(start, apple, obstacles).get();
            LeePoint nextMove = path.size() > 1 ? path.get(1) : new LeePoint(0,0);
            if(nextMove.y() == start.getY() && nextMove.x() > start.getX()) return Direction.RIGHT;
            else if(nextMove.y() == start.getY() && nextMove.x() < start.getX()) return Direction.LEFT;
            else if(nextMove.x() == start.getX() && nextMove.y() < start.getY()) return Direction.DOWN;
            else return Direction.UP;
        }
        else if(board.getSnake().size() >= 55 && lee.trace(start, stone, board.getBarriersWithoutStone()).isPresent()){
            List<LeePoint> path = lee.trace(start, stone, board.getBarriersWithoutStone()).get();
            LeePoint nextMove = path.size() > 1 ? path.get(1) : new LeePoint(0,0);
            if(nextMove.y() == start.getY() && nextMove.x() > start.getX()) return Direction.RIGHT;
            else if(nextMove.y() == start.getY() && nextMove.x() < start.getX()) return Direction.LEFT;
            else if(nextMove.x() == start.getX() && nextMove.y() < start.getY()) return Direction.DOWN;
            else return Direction.UP;
        }
        else{
            LeePoint nextMove = lee.nonTarget(start, obstacles);
            if(nextMove.y() == start.getY() && nextMove.x() > start.getX()) return Direction.RIGHT;
            else if(nextMove.y() == start.getY() && nextMove.x() < start.getX()) return Direction.LEFT;
            else if(nextMove.x() == start.getX() && nextMove.y() < start.getY()) return Direction.DOWN;
            else return Direction.UP;
        }
    }
}
