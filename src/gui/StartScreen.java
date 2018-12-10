package gui;

import logic.CreateChildNodes;
import logic.FileNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.*;

public class StartScreen extends Component {
    private final JFileChooser fileChooser;
    private String path = "";
    private File file;
    private JTree tree;
    private JPanel rootPanel;
    private JTextField directoryField;
    private JButton openButton;
    private JTextField extensionField;
    private JTextPane textPane;
    private JButton searchButton;
    private JScrollPane treeScrollPane;
    private JButton removeTabButton;
    private JTabbedPane tabbedPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Finder");
            frame.setContentPane(new StartScreen().rootPanel);

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            frame.setVisible(true);
        });
    }

    private StartScreen() {
        fileChooser = new JFileChooser();

        openButton.addActionListener(e -> {
            fileChooser.setDialogTitle("Select a directory");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(StartScreen.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                path = fileChooser.getSelectedFile().getAbsolutePath();
                directoryField.setText(path);
            }
        });

        searchButton.addActionListener(e -> new Thread(new Search()).start());

        removeTabButton.addActionListener(e -> tabbedPane.remove(tabbedPane.getSelectedComponent()));
    }


    private void createUIComponents() {
        tabbedPane = new JTabbedPane();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("EmptyTree");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
        tree.setEditable(true);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

            try {
                FileNode fileNode = (FileNode) selectedNode.getUserObject();
                if (fileNode.getFile().isFile()) {
                    file = fileNode.getFile();
                    new Thread(new FileReader()).start();
                }
            } catch (Exception ignore) {

            }

        });

        tree.setModel(new DefaultTreeModel(root));
    }

    class Search implements Runnable {
        @Override
        public void run() {
            File fileRoot = new File(path);
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(path.equals("") ? "EmptyTree" : new FileNode(fileRoot));
            CreateChildNodes createChildNodes = new CreateChildNodes(fileRoot, root, textPane.getText(), extensionField.getText());

            Thread createChildNodesThread = new Thread(createChildNodes);
            createChildNodesThread.start();
            try {
                createChildNodesThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (root.getChildCount() == 0)
                root = new DefaultMutableTreeNode("EmptyTree");

            tree.setModel(new DefaultTreeModel(root));
        }
    }

    class FileReader implements Runnable {
        java.util.List<String> lines;

        @Override
        public void run() {
            try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                StringBuilder str = new StringBuilder();

                int count;

                while ((count = inputStream.read()) != -1) {
                    str.append((char) count);
                }

                JTextPane txtPane = new JTextPane();
                txtPane.setText(str.toString());
                JPanel txtPanel = new JPanel();
                txtPanel.add(txtPane);

                tabbedPane.addTab(file.getName(), new JScrollPane(txtPanel));

            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
             * Чтение файлов быстрее, но некоторые файлы могут очень долго добавляться на форму.
             * По какой-то причине, добавление текста на форму после чтения BufferedInputStream'ом, работает всегда хорошо.
             * Однако, некоторые файлы очень долго добавляются, после чтения BufferedReader'ом или Files.readAllLines'ом.
             */

//            Path wiki_path = Paths.get(file.getAbsolutePath());
//
//            Charset charset = Charset.forName("UTF-8");
//            try {
//                lines = Files.readAllLines(wiki_path, charset);
//                StringBuilder str = new StringBuilder();
//
//                for (String currentString : lines) {
//                    str.append(currentString);
//                    str.append("\n");
//                }
//
//                JTextPane txtPane = new JTextPane();
//                txtPane.setText(str.toString());
//                JPanel txtPanel = new JPanel();
//                txtPanel.add(txtPane);
//
//                tabbedPane.addTab(file.getName(), new JScrollPane(txtPanel));
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
