package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Point;

import java.util.*;
import java.util.stream.Collectors;

public class Lee {
    private final static List<LeePoint> deltas = new ArrayList<LeePoint>(){{
        add(new LeePoint(0,-1));
        add(new LeePoint(1,0));
        add(new LeePoint(0,1));
        add(new LeePoint(-1,0));
    }};
    private final static int OBSTACLE = -10;
    private final static int START = -1;
    private final int dimX;
    private final int dimY;
    private final int[][] board;

    public Lee(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.board = new int[dimY][dimX];
    }

    public String formatted(LeePoint point, List<LeePoint> path) {
        int val = get(point);
        if (val == OBSTACLE) return " XX ";
        if (path.isEmpty()) return String.format("%3d ", val);
        if (path.contains(point)) return String.format("%3d ", val);
        return " __ ";
    }

    public void printMe(List<LeePoint> path) {
        for (int row = 0; row < dimY; row++) {
            for (int col = 0; col < dimX; col++) {
                LeePoint p = new LeePoint(col, row);
                System.out.print(formatted(p, path));
            }
            System.out.println();
        }
        System.out.println();
    }

    int get(LeePoint p) {
        return this.board[p.y()][p.x()];
    }

    void set(LeePoint p, int val) {
        this.board[p.y()][p.x()] = val;
    }

    boolean isOnBoard(LeePoint p) {
        return p.x()>= 0 && p.x() < dimX && p.y()>=0 && p.y()< dimY;
    }

    boolean isUnvisited(LeePoint p) {
        return get(p) == 0;
    }

    Set<LeePoint> neighbors(LeePoint point) {
        return deltas.stream()
                .map(d -> d.move(point))
                .filter(this::isOnBoard)
                .collect(Collectors.toSet());
    }

    Set<LeePoint> neighborsUnvisited(LeePoint point) {
        return neighbors(point).stream()
                .filter(this::isUnvisited)
                .collect(Collectors.toSet());
    }

    LeePoint neighborByValue(LeePoint point, int value) {
        return neighbors(point).stream()
                .filter(p -> get(p) == value)
                .findFirst()
                .orElse(null);
    }

    Optional<LeePoint> findByValue(int val){
        for (int i = 0; i < dimX; i++)
            for (int j = 0; j < dimY; j++)
                if(board[j][i] == val) return Optional.of(new LeePoint(i, j));
        return Optional.empty();
    }

    private void initBoard(List<LeePoint> obstacles) {
        for (int i = 0; i < dimX; i++)
            for (int j = 0; j < dimY; j++)
                board[j][i] = 0;
        obstacles.forEach(p -> set(p, OBSTACLE));
    }

    public Optional<List<LeePoint>> trace(Point start, Point finish, List<Point> obstacles) {
        return trace(start, finish, obstacles, false);
    }

    public Optional<List<LeePoint>> trace(Point start, Point finish, List<Point> obstacles, boolean debug) {
        return trace1(
                new LeePoint(start),
                new LeePoint(finish),
                obstacles.stream().map(LeePoint::new).collect(Collectors.toList()),
                debug);
    }

    public Optional<List<LeePoint>> trace1(LeePoint start, LeePoint finish, List<LeePoint> obstacles, boolean debug) {
        initBoard(obstacles);
        boolean found = false;
        set(start, START);
        Set<LeePoint> curr = new HashSet<>();
        int[] counter = {0};
        curr.add(start);
        while (!curr.isEmpty() && !found) {
            counter[0]++;
            Set<LeePoint> next = curr.stream()
                    .map(this::neighborsUnvisited)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            next.forEach(p -> set(p, counter[0]));
            if (next.contains(finish)) {
                found = true;
            }
            if (debug) printMe(new ArrayList<>());
            curr.clear();
            curr.addAll(next);
            next.clear();
        }
        if (!found) return Optional.empty();
        set(start, 0);
        ArrayList<LeePoint> path = new ArrayList<>();
        path.add(finish);
        LeePoint curr_p = finish;
        while (counter[0] > 0) {
            counter[0]--;
            LeePoint prev_p = neighborByValue(curr_p, counter[0]);
            path.add(prev_p);
            curr_p = prev_p;
        }
        Collections.reverse(path);
        return Optional.of(path);
    }

    public LeePoint nonTarget(Point start, List<Point> obstacles){
        return nonTarget(
                new LeePoint(start),
                obstacles.stream().map(LeePoint::new).collect(Collectors.toList()));
    }

    public LeePoint nonTarget(LeePoint start, List<LeePoint> obstacles){
        initBoard(obstacles);
        set(start, START);
        Set<LeePoint> curr = new HashSet<>();
        int[] counter = {0};
        curr.add(start);
        while (!curr.isEmpty()) {
            counter[0]++;
            Set<LeePoint> next = curr.stream()
                    .map(this::neighborsUnvisited)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            next.forEach(p -> set(p, counter[0]));
            curr.clear();
            curr.addAll(next);
            next.clear();
        }
        set(start, 0);
        ArrayList<LeePoint> path = new ArrayList<>();
        LeePoint finish = findByValue(--counter[0]).orElse(null);
        path.add(finish);
        LeePoint curr_p = finish;
        while (counter[0] > 0) {
            counter[0]--;
            LeePoint prev_p = neighborByValue(curr_p, counter[0]);
            path.add(prev_p);
            curr_p = prev_p;
        }
        Collections.reverse(path);
        return dfs(path.get(0));
    }

    public LeePoint dfs(LeePoint from) {
        for(int i = 3; i >= 0; i--)
        {
            if(get(deltas.get(i).move(from)) != OBSTACLE && isOnBoard(deltas.get(i).move(from))) return deltas.get(i).move(from);
        }
        return new LeePoint(0, 0);
    }
}
