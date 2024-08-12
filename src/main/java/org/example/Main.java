package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {

    private static Path currentDirectory = Paths.get(System.getProperty("user.home"));

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command;


        // getting user input
        while (true) {
            System.out.println("\nCurrent Directory: " + currentDirectory.toString());
            System.out.println("Commands: [list, cd, copy, move, delete, mkdir, rmdir, search, exit]");
            System.out.print("Enter command: ");
            command = scanner.nextLine().trim().toLowerCase();

            try {
                switch (command) {
                    case "list":
                        listDirectoryContents();
                        break;
                    case "cd":
                        changeDirectory(scanner);
                        break;
                    case "copy":
                        copyFile(scanner);
                        break;
                    case "move":
                        moveFile(scanner);
                        break;
                    case "delete":
                        deleteFile(scanner);
                        break;
                    case "mkdir":
                        createDirectory(scanner);
                        break;
                    case "rmdir":
                        deleteDirectory(scanner);
                        break;
                    case "search":
                        searchFiles(scanner);
                        break;
                    case "exit":
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid command. Please try again.");
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    // listing the content of the current directory
    private static void listDirectoryContents() {
        try (Stream<Path> stream = Files.list(currentDirectory)) {
            System.out.println("Name\tSize (bytes)\tLast Modified");
            stream.forEach(entry -> {
                try {
                    String size = Files.isDirectory(entry) ? "" : String.valueOf(Files.size(entry));
                    String lastModified = Files.getLastModifiedTime(entry).toString();
                    System.out.println(entry.getFileName() + "\t" + size + "\t" + lastModified);
                } catch (IOException e) {
                    System.err.println("Error accessing file: " + e.getMessage());
                }
            });
        } catch (AccessDeniedException e) {
            System.err.println("Access denied: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error listing directory: " + e.getMessage());
        }
    }
    // cding into the directory specified (you need to type in the entire path)
    private static void changeDirectory(Scanner scanner) {
        System.out.print("Enter directory path to change to: ");
        Path newPath = Paths.get(scanner.nextLine().trim());

        if (Files.isDirectory(newPath)) {
            currentDirectory = newPath;
            System.out.println("Directory changed to " + currentDirectory);
        } else {
            System.out.println("Invalid directory path.");
        }
    }
    // copying the file (if you want to copy into the same directory you need to make sure that you rename it)
    private static void copyFile(Scanner scanner) throws IOException {
        System.out.print("Enter source file path: ");
        Path sourcePath = Paths.get(scanner.nextLine().trim());
        System.out.print("Enter destination file path: ");
        Path destPath = Paths.get(scanner.nextLine().trim());

        if (Files.exists(sourcePath) && Files.isRegularFile(sourcePath)) {
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully.");
        } else {
            System.out.println("Source file does not exist or is not a regular file.");
        }
    }
    // basically "cutting" a file from a directory and putting it into another
    private static void moveFile(Scanner scanner) throws IOException {
        System.out.print("Enter source file path: ");
        Path sourcePath = Paths.get(scanner.nextLine().trim());
        System.out.print("Enter destination file path: ");
        Path destPath = Paths.get(scanner.nextLine().trim());

        if (Files.exists(sourcePath) && Files.isRegularFile(sourcePath)) {
            Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File moved successfully.");
        } else {
            System.out.println("Source file does not exist or is not a regular file.");
        }
    }
    // deleting a file from the directory specified
    private static void deleteFile(Scanner scanner) throws IOException {
        System.out.print("Enter file path to delete: ");
        Path filePath = Paths.get(scanner.nextLine().trim());

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            Files.delete(filePath);
            System.out.println("File deleted successfully.");
        } else {
            System.out.println("File does not exist or is not a regular file.");
        }
    }
    // creating directory from the current directory your in
    private static void createDirectory(Scanner scanner) throws IOException {
        System.out.print("Enter directory name to create (relative to current directory): ");
        Path dirPath = currentDirectory.resolve(scanner.nextLine().trim());

        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
                System.out.println("Directory created successfully.");
            } else {
                System.out.println("Directory already exists.");
            }
        } catch (AccessDeniedException e) {
            System.err.println("Access denied: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error creating directory: " + e.getMessage());
        }
    }
    // rm dir from the current directory
    private static void deleteDirectory(Scanner scanner) throws IOException {
        System.out.print("Enter directory name to delete (relative to current directory): ");
        Path dirPath = currentDirectory.resolve(scanner.nextLine().trim());

        try {
            if (Files.exists(dirPath) && Files.isDirectory(dirPath) && isDirectoryEmpty(dirPath)) {
                Files.delete(dirPath);
                System.out.println("Directory deleted successfully.");
            } else {
                System.out.println("Directory does not exist, is not a directory, or is not empty.");
            }
        } catch (AccessDeniedException e) {
            System.err.println("Access denied: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error deleting directory: " + e.getMessage());
        }
    }
    // needed to add this to ensure the directory would not be empty
    private static boolean isDirectoryEmpty(Path dirPath) throws IOException {
        try (Stream<Path> entries = Files.list(dirPath)) {
            return !entries.findAny().isPresent();
        }
    }
    // searching files/directories in the diretory your currently in;
    private static void searchFiles(Scanner scanner) {
        System.out.print("Enter search term (file name or extension): ");
        String searchTerm = scanner.nextLine().trim();
        Path searchPath = currentDirectory;  // Search within the current directory

        try (Stream<Path> stream = Files.list(searchPath)) {
            System.out.println("Found items:");
            stream
                    .filter(path -> path.getFileName().toString().contains(searchTerm)) // Filter based on search term
                    .forEach(path -> System.out.println(path.toAbsolutePath()));
        } catch (Exception e) {
            System.err.println("An error occurred during search: " + e.getMessage());
        }
    }
}
