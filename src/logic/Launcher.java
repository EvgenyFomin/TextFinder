package logic;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launcher {
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void search(File fileRoot, DefaultMutableTreeNode root, String text, String extensionFieldText) {
        CreateChildNodes ccn = new CreateChildNodes(fileRoot, root, text, extensionFieldText);
        runSearch(ccn);
    }

    private static void runSearch(Runnable createChildNodes) {
        pool.submit(createChildNodes);
    }
}
