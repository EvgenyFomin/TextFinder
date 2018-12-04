package logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Search {
    Map<String, List<String>> execute(String root, String fileExtension, String text) {
        List<File> resultFileList = new LinkedList<>();
        List<File> fileList = findAllInnerFiles(root, fileExtension);

        for (File currentFile : fileList) {
            if (fileContainsText(currentFile.getAbsolutePath(), text)) {
                resultFileList.add(currentFile);
            }
        }

        return createMyTreeModel(resultFileList, root);
    }


    private boolean fileContainsText(String fileName, String text) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName))).contains(text);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<File> findAllInnerFiles(String root, String fileExtension) {
        File rootDir = new File(root);
        List<File> result = new ArrayList<>();
        Queue<File> fileTree = new PriorityQueue<>();

        Collections.addAll(fileTree, Objects.requireNonNull(rootDir.listFiles()));

        while (!fileTree.isEmpty()) {
            File currentFile = fileTree.remove();

            if (currentFile.isDirectory()) {

                try {
                    Collections.addAll(fileTree, Objects.requireNonNull(currentFile.listFiles()));
                } catch (NullPointerException ignored) {
                }

            } else if (getFileExtension(currentFile.getAbsolutePath()).equals(fileExtension)) {
                result.add(currentFile);
            }

        }

        return result;
    }

    private Map<String, List<String>> createMyTreeModel(List<File> fileList, String rootDir) {
        Map<String, List<String>> treeModel = new HashMap<>();

        for (File currentFile : fileList) {
            File file = currentFile;

            while (!file.getAbsolutePath().equals(rootDir)) {
                File child = file;
                File parent = new File(file.getParent());

                if (!treeModel.containsKey(parent.getAbsolutePath())) {
                    treeModel.put(parent.getAbsolutePath(), new LinkedList<>());
                }

                if (!treeModel.get(parent.getAbsolutePath()).contains(child.getAbsolutePath()))
                    treeModel.get(parent.getAbsolutePath()).add(child.getAbsolutePath());

                file = new File(file.getParent());
            }
        }

        return treeModel;
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
