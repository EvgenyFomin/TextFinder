package logic;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateChildNodes implements Runnable {
    private String fileExtension;

    private DefaultMutableTreeNode root;

    private File fileRoot;

    private String text;

    CreateChildNodes(File fileRoot,
                     DefaultMutableTreeNode root,
                     String text,
                     String fileExtension) {
        this.fileRoot = fileRoot;
        this.root = root;
        this.text = text;
        this.fileExtension = fileExtension;
    }

    @Override
    public void run() {
        createChildren(fileRoot, root);
    }

    private void createChildren(File fileRoot, DefaultMutableTreeNode rootNode) {
        Map<String, List<String>> myTreeModel = new Search().execute(fileRoot.getAbsolutePath(), fileExtension, text);

        if (myTreeModel.size() == 0) {
            root = new DefaultMutableTreeNode("EmptyTree");
        } else {
            Map<String, DefaultMutableTreeNode> nodesMap = new HashMap<>();

            nodesMap.put(fileRoot.getAbsolutePath(), rootNode);

            for (Map.Entry<String, List<String>> myTreeModelEntry : myTreeModel.entrySet()) {
                String currentParent = myTreeModelEntry.getKey();

                if (!nodesMap.containsKey(currentParent)) {
                    nodesMap.put(currentParent, new DefaultMutableTreeNode(new FileNode(new File(currentParent))));
                }

                for (String currentChild : myTreeModelEntry.getValue()) {
                    if (nodesMap.containsKey(currentChild)) {
                        nodesMap.get(currentParent).add(nodesMap.get(currentChild));
                    } else {
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(new File(currentChild)));
                        nodesMap.put(currentChild, childNode);
                        nodesMap.get(currentParent).add(childNode);
                    }
                }
            }
        }
    }
}