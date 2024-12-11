import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class TeleportPathFinderGUI extends JFrame {

    private final int rows = 10;
    private final int cols = 10;
    private final JButton[][] gridButtons = new JButton[rows][cols];
    private final char[][] grid = new char[rows][cols];
    private TeleportPathFinder.Point startPoint = null;
    private TeleportPathFinder.Point endPoint = null;
    private final List<TeleportPathFinder.Teleport> teleports = new ArrayList<>();

    public TeleportPathFinderGUI() {
        setTitle("Teleport Path Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
                JButton button = new JButton();
                button.setBackground(Color.WHITE);
                button.setPreferredSize(new Dimension(50, 50));
                button.setFocusPainted(false);
                final int x = i, y = j; // Финальные переменные для лямбд
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            handleCellClick(button, x, y);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            handleTeleportClick(x, y);
                        }
                    }
                });
                gridButtons[i][j] = button;
                gridPanel.add(button);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton findPathButton = new JButton("Find Path");
        findPathButton.addActionListener(e -> findPath());
        controlPanel.add(findPathButton);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleCellClick(JButton button, int x, int y) {
        if (startPoint == null) {
            startPoint = new TeleportPathFinder.Point(x, y);
            grid[x][y] = 'S';
            button.setBackground(Color.GREEN);
        } else if (endPoint == null) {
            endPoint = new TeleportPathFinder.Point(x, y);
            grid[x][y] = 'E';
            button.setBackground(Color.RED);
        } else {
            if (grid[x][y] == '.') {
                grid[x][y] = '#';
                button.setBackground(Color.BLACK);
            } else if (grid[x][y] == '#') {
                grid[x][y] = '.';
                button.setBackground(Color.WHITE);
            }
        }
    }

    private void handleTeleportClick(int x, int y) {
        if (grid[x][y] == '.') {
            if (teleports.size() % 2 == 0) {
                teleports.add(new TeleportPathFinder.Teleport(new TeleportPathFinder.Point(x, y), null));
                grid[x][y] = 'T';
                gridButtons[x][y].setBackground(Color.MAGENTA);
            } else {
                TeleportPathFinder.Teleport lastTeleport = teleports.get(teleports.size() - 1);
                lastTeleport.exit = new TeleportPathFinder.Point(x, y);
                grid[x][y] = 'T';
                gridButtons[x][y].setBackground(Color.MAGENTA);
            }
        } else if (grid[x][y] == 'T') {
            teleports.removeIf(teleport -> teleport.entry.equals(new TeleportPathFinder.Point(x, y)) ||
                    (teleport.exit != null && teleport.exit.equals(new TeleportPathFinder.Point(x, y))));
            grid[x][y] = '.';
            gridButtons[x][y].setBackground(Color.WHITE);
        }
    }


    private void findPath() {
        clearPath();
        if (startPoint == null || endPoint == null) {
            JOptionPane.showMessageDialog(this, "Start and end points must be selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<TeleportPathFinder.Point> path = TeleportPathFinder.findShortestPath(grid, startPoint, endPoint, teleports);
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No path found.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            for (TeleportPathFinder.Point p : path) {
                if (grid[p.x][p.y] == '.') {
                    gridButtons[p.x][p.y].setBackground(Color.CYAN);
                }
            }
            JOptionPane.showMessageDialog(this, "Path found! Length: " + (path.size() - 1), "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearPath() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gridButtons[i][j].getBackground().equals(Color.CYAN)) {
                    gridButtons[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TeleportPathFinderGUI::new);
    }
}
