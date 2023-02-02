import model.Person;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        readAndWrite();
        nonUTF();
        writingFiles();
        CSV();
        walkfFileTreePattern();
        walkPattern();
    }

    private static void readAndWrite() throws IOException {
        Path path = Path.of("files/sonnet-UTF8.txt");

        boolean exists = Files.exists(path);
        System.out.println("Exists: " + exists);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine();
            while (line != null) {
                System.out.println("Line = " + line);
                line = reader.readLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void nonUTF() throws IOException {
        Path path = Path.of("files/sonnet-ISO.txt");

        boolean exists = Files.exists(path);
        System.out.println("Exists: " + exists);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
            String line = reader.readLine();
            while (line != null) {
                System.out.println("Line = " + line);
                line = reader.readLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void writingFiles() throws IOException {
        Path path = Path.of("files/debug.log");

        try (BufferedWriter writer1 = Files.newBufferedWriter(path);
             BufferedWriter writer2 = new BufferedWriter(writer1);
             PrintWriter pw = new PrintWriter(writer2);)
        {
            writer1.write("WTF!!");

            pw.printf("\ni = %d\n", 12);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void CSV() {
        Path path = Path.of("files/data1.csv");

//        Function<String, Person> lineToPerson = line -> (Person) lineToPerson(line);

        try (Stream<String> lines = Files.lines(path);) {

            lines.filter(line -> !line.startsWith("#"))
                    .flatMap(Main::lineToPerson)
                    .forEach(System.out::println);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Stream<Person> lineToPerson(String line) {
        try {
            String[] elements = line.split(";");
            String name = elements[0];
            int age = Integer.parseInt(elements[1]);
            String city = elements[2];

            Person p = new Person(name, age, city);
            return Stream.of(p);
        } catch (Exception e) {

        }
        return Stream.empty();
    }

    public static void walkfFileTreePattern() throws IOException {
        Path path = Paths.get("files/media");

        var visitor = new FileVisitor<Path>() {
            private long count = 0L;
            private long dirs = 0L;
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dirs++;
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                count++;
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(path, visitor);

        System.out.println("FileCount = " + visitor.count + " Directory Count = " + visitor.dirs);
    }

    private static void walkPattern() throws IOException {
        Path path = Paths.get("files/media");

        Stream<Path> stream = Files.walk(path);

        long count = stream.count();

        long countDirs =
                Files.walk(path)
                        .filter(Files::isDirectory) // method reference
                        .count();

        long countFiles =
                Files.walk(path)
                        .filter(p -> Files.isRegularFile(p))
                        .count();

        System.out.println("Count = " + count);
        System.out.println("FileCount = " + countFiles + " Directory Count = " + countDirs);
    }
}