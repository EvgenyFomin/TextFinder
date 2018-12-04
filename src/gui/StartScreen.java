package gui;

import logic.FileNode;
import logic.Launcher;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.*;

public class StartScreen extends Component {
    private final JFileChooser fileChooser;
    private String rootDir = "";
    private String path;
    private JTree tree;
    private JPanel rootPanel;
    private JTextField directoryField;
    private JButton openButton;
    private JTextField extensionField;
    private JTextPane textPane;
    private JButton searchButton;
    private JTextArea selectedFileContent;
    private JScrollPane scrollPane;
    private JScrollPane scrollTextPane;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Finder");
        frame.setContentPane(new StartScreen().rootPanel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        frame.setVisible(true);
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

        searchButton.addActionListener(e -> {
            rootDir = path;
            selectedFileContent.setText("");
            createUIComponents();
        });
    }

    private void createUIComponents() {
        File fileRoot = new File(rootDir);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootDir.equals("") ? "FreeTree" : new FileNode(fileRoot));
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        if (!rootDir.equals("")) {
            Launcher.search(fileRoot, root, textPane.getText(), extensionField.getText());
        }

        if (tree == null) {
            tree = new JTree(treeModel);
            tree.setEditable(true);
            tree.addTreeSelectionListener(e -> {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                try {
                    FileNode fileNode = (FileNode) selectedNode.getUserObject();
                    if (fileNode.getFile().isFile()) {
                        readFile(fileNode.getFile());
                    }
                } catch (Exception ignore) {

                }

            });
        } else {
            tree.setModel(new DefaultTreeModel(root));
        }
    }

    private void readFile(File file) {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            StringBuilder str = new StringBuilder();

            int count;

            while ((count = inputStream.read()) != -1) {
                str.append((char) count);
            }

            selectedFileContent.setText(str.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // При использовании buffered reader на больших файлах, приложение бесконечно висит.

//    private void readFile2(File file) {
//        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
//             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
//            StringBuilder str = new StringBuilder();
//
//            long start = System.currentTimeMillis();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                str.append(line);
//                str.append("\n");
//            }
//
//            long end = System.currentTimeMillis();
//
//            System.out.println(end - start);
//
//            selectedFileContent.setText(str.toString());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
