import java.util.*;

public class TeleportPathFinder {

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static class Teleport {
        Point entry, exit;

        Teleport(Point entry, Point exit) {
            this.entry = entry;
            this.exit = exit;
        }
    }

    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {1, 0, -1, 0};

    public static List<Point> findShortestPath(char[][] grid, Point start, Point end, List<Teleport> teleports) {
        Map<Point, Point> teleportMap = new HashMap<>();
        for (Teleport t : teleports) {
            teleportMap.put(t.entry, t.exit);
        }

        Set<Point> visitedStart = new HashSet<>();
        Set<Point> visitedEnd = new HashSet<>();
        Queue<Point> queueStart = new LinkedList<>();
        Queue<Point> queueEnd = new LinkedList<>();
        Map<Point, Point> parentStart = new HashMap<>();
        Map<Point, Point> parentEnd = new HashMap<>();

        queueStart.add(start);
        queueEnd.add(end);
        visitedStart.add(start);
        visitedEnd.add(end);

        while (!queueStart.isEmpty() && !queueEnd.isEmpty()) {
            if (step(grid, queueStart, visitedStart, visitedEnd, parentStart, teleportMap, true)) {
                return buildPath(parentStart, parentEnd, start, end);
            }
            if (step(grid, queueEnd, visitedEnd, visitedStart, parentEnd, teleportMap, false)) {
                return buildPath(parentStart, parentEnd, start, end);
            }
        }

        return Collections.emptyList();
    }

    private static boolean step(char[][] grid, Queue<Point> queue, Set<Point> visited, Set<Point> otherVisited,
                                Map<Point, Point> parent, Map<Point, Point> teleportMap, boolean isStart) {
        if (queue.isEmpty()) return false;
        Point current = queue.poll();

        for (int i = 0; i < 4; i++) {
            int nx = current.x + DX[i];
            int ny = current.y + DY[i];
            Point neighbor = new Point(nx, ny);

            if (isValidMove(grid, neighbor, visited)) {
                visited.add(neighbor);
                parent.put(neighbor, current);
                queue.add(neighbor);
                if (otherVisited.contains(neighbor)) {
                    return true;
                }
            }
        }

        if (teleportMap.containsKey(current)) {
            Point teleportExit = teleportMap.get(current);
            if (isValidMove(grid, teleportExit, visited)) {
                visited.add(teleportExit);
                parent.put(teleportExit, current);
                queue.add(teleportExit);
                if (otherVisited.contains(teleportExit)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isValidMove(char[][] grid, Point p, Set<Point> visited) {
        int rows = grid.length;
        int cols = grid[0].length;
        return p.x >= 0 && p.x < rows && p.y >= 0 && p.y < cols && grid[p.x][p.y] != '#' && !visited.contains(p);
    }

    private static List<Point> buildPath(Map<Point, Point> parentStart, Map<Point, Point> parentEnd, Point start, Point end) {
        List<Point> path = new LinkedList<>();
        Point meetingPoint = null;
        for (Point p : parentStart.keySet()) {
            if (parentEnd.containsKey(p)) {
                meetingPoint = p;
                break;
            }
        }

        if (meetingPoint == null) return Collections.emptyList();

        Point current = meetingPoint;
        while (current != null) {
            path.add(0, current);
            current = parentStart.get(current);
        }

        current = parentEnd.get(meetingPoint);
        while (current != null) {
            path.add(current);
            current = parentEnd.get(current);
        }

        return path;
    }
}
