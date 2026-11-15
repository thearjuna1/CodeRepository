package com.codenest.config;

import com.codenest.model.Lesson.CodeExample;

import java.util.List;
public final class CurriculumData {

    private CurriculumData() {
    }

    public static final String COURSE_SLUG = "java-programming";
    public static final String COURSE_TITLE = "Java Programming: From Zero to Backend";
    public static final String COURSE_LEVEL = "Beginner to Intermediate";
    public static final String COURSE_DESCRIPTION =
            "A complete path from your first Java program to object-oriented design, collections, streams, "
                    + "databases and your first REST API with Spring Boot.";


    public record LessonSeed(String title, int minutes, List<String> objectives, String explanation,
                             List<CodeExample> examples, List<String> notes, List<String> mistakes,
                             List<String> related) {
    }

    public record QuestionSeed(String text, int correctIndex, String explanation, List<String> options) {
    }

    public record QuizSeed(String title, String description, List<QuestionSeed> questions) {
    }

    public record PracticeSeed(String title, String difficulty, String description, String starterCode,
                               String hint, String solution, String expectedOutput) {
    }

    public record ModuleSeed(String title, String description, List<LessonSeed> lessons, QuizSeed quiz,
                             List<PracticeSeed> practice) {
    }

    /** All seven modules, in learning order. */
    public static List<ModuleSeed> modules() {
        return List.of(module1(), module2(), module3(), module4(), module5(), module6(), module7());
    }

    // ============================ helpers ============================

    private static LessonSeed lesson(String title, int minutes, List<String> objectives, String explanation,
                                     List<CodeExample> examples, List<String> notes, List<String> mistakes,
                                     List<String> related) {
        return new LessonSeed(title, minutes, objectives, explanation, examples, notes, mistakes, related);
    }

    private static CodeExample ex(String title, String code, String expectedOutput) {
        return new CodeExample(title, code, expectedOutput);
    }

    private static QuestionSeed q(String text, int correctIndex, String explanation, String... options) {
        return new QuestionSeed(text, correctIndex, explanation, List.of(options));
    }

    private static PracticeSeed practice(String title, String difficulty, String description, String starterCode,
                                         String hint, String solution, String expectedOutput) {
        return new PracticeSeed(title, difficulty, description, starterCode, hint, solution, expectedOutput);
    }



    static ModuleSeed module1() {
        List<LessonSeed> lessons = List.of(

                lesson("Introduction to Java", 8,
                        List.of("Explain what Java is and where it is used",
                                "Describe how Java source code becomes a running program"),
                        """
                        Java is a general-purpose, object-oriented programming language first released by Sun Microsystems in 1995 and now developed by Oracle and the OpenJDK community. It is used for web back ends, Android apps, banking and enterprise systems, big-data tools and much more.

                        A Java program goes through two steps. First the compiler (javac) turns your source code (.java files) into bytecode (.class files). Then the Java Virtual Machine (JVM) runs that bytecode. Every operating system has its own JVM, so the same compiled program runs on Windows, Linux and macOS. That is the idea behind "write once, run anywhere".
                        """,
                        List.of(ex("Compile and run a program", """
                                // File: Hello.java
                                public class Hello {
                                    public static void main(String[] args) {
                                        System.out.println("Hello from Java");
                                    }
                                }

                                // In a terminal:
                                //   javac Hello.java    (creates Hello.class)
                                //   java Hello          (runs it)
                                """, """
                                Hello from Java
                                """)),
                        List.of("Java is compiled to bytecode and then interpreted and optimised by the JVM at run time."),
                        List.of("Java and JavaScript are different languages that only share part of a name."),
                        List.of("Features of Java", "Installing the JDK")),

                lesson("Features of Java", 8,
                        List.of("List the main design goals of Java",
                                "Explain platform independence, garbage collection and multithreading at a high level"),
                        """
                        Java was designed with several goals. It is platform independent, because bytecode runs on any JVM. It is object-oriented, so programs are organised as objects that combine data and behaviour. It is relatively simple: there is no pointer arithmetic and no manual memory management.

                        Java is also robust. It is strongly typed, it has a structured exception-handling system, and the garbage collector frees memory you no longer use. It is secure, because bytecode is verified before it runs. It supports multithreading, so one program can do several things at once. Finally, the JIT (just-in-time) compiler turns hot bytecode into fast machine code, and the huge standard library and ecosystem mean you rarely start from nothing.
                        """,
                        List.of(ex("Check which Java version is running", """
                                public class VersionCheck {
                                    public static void main(String[] args) {
                                        System.out.println("Java version: " + System.getProperty("java.version"));
                                    }
                                }
                                """, """
                                Java version: 21.0.2
                                """)),
                        List.of("The version number printed depends on the JDK you installed; 21.x is expected for this course."),
                        List.of("Thinking that garbage collection means you never have to think about memory. Holding references to objects you no longer need can still waste memory."),
                        List.of("Introduction to Java")),

                lesson("Installing the JDK", 10,
                        List.of("Distinguish the JVM, JRE and JDK",
                                "Install JDK 21 and verify it from a terminal"),
                        """
                        The JVM runs bytecode. The JRE (Java Runtime Environment) is the JVM plus the standard libraries. The JDK (Java Development Kit) is the JRE plus the developer tools, including the compiler javac. To write Java you need the JDK.

                        This course uses Java 21, a long-term-support release. Download a JDK 21 build from a trusted provider such as Eclipse Adoptium (Temurin) or Oracle, run the installer, and open a new terminal. If the commands below are not found, add the JDK's bin folder to your PATH and set the JAVA_HOME environment variable to the JDK folder.
                        """,
                        List.of(ex("Verify the installation", """
                                java -version
                                javac -version
                                """, """
                                openjdk version "21.0.2" 2024-01-16
                                javac 21.0.2
                                """)),
                        List.of("The exact version number and date on your machine will differ. What matters is that both commands work and show 21.",
                                "IntelliJ IDEA can also download a JDK for you from the project settings."),
                        List.of("Installing only a JRE: the java command works but javac is missing, so you cannot compile.",
                                "Opening the terminal before installing and not restarting it, so the new PATH is not picked up."),
                        List.of("Setting up the development environment")),

                lesson("Setting up the development environment", 10,
                        List.of("Create and run a Java project in IntelliJ IDEA",
                                "Compile and run a class from the command line"),
                        """
                        An IDE (integrated development environment) gives you an editor, a compiler and a runner in one place. IntelliJ IDEA Community Edition is a popular free choice for Java. Visual Studio Code with the Java extensions is another.

                        In IntelliJ IDEA: choose New Project, pick Java, select your JDK 21, and create the project. Right-click the src folder, choose New, then Java Class, and name it Main. Type the program, then click the green Run arrow next to the main method. You can also work without an IDE by saving a .java file and using javac and java in a terminal.
                        """,
                        List.of(ex("A first project check", """
                                public class Main {
                                    public static void main(String[] args) {
                                        System.out.println("Environment ready");
                                    }
                                }
                                """, """
                                Environment ready
                                """)),
                        List.of("Keep each project in its own folder so class names from different exercises never clash."),
                        List.of("Naming the file differently from the public class, for example Main.java containing public class Hello."),
                        List.of("First Java program")),

                lesson("First Java program", 10,
                        List.of("Write, compile and run Hello World",
                                "Explain every part of the main method signature"),
                        """
                        Every Java application starts at a method called main. Reading its signature word by word: public means anyone can call it, static means it belongs to the class so the JVM can call it without creating an object, void means it returns nothing, and String[] args receives any command-line arguments.

                        System.out.println prints text and moves to a new line. System.out.print prints text and stays on the same line. Statements end with a semicolon, and blocks of code are wrapped in curly braces.
                        """,
                        List.of(ex("Hello, World!", """
                                public class HelloWorld {
                                    public static void main(String[] args) {
                                        System.out.println("Hello, World!");
                                        System.out.print("No new line here. ");
                                        System.out.println("But this one ends the line.");
                                    }
                                }
                                """, """
                                Hello, World!
                                No new line here. But this one ends the line.
                                """)),
                        List.of("Java is case-sensitive: System and system are different names."),
                        List.of("Writing string instead of String, or Main instead of main, which stops the program from starting.",
                                "Forgetting the semicolon at the end of a statement."),
                        List.of("Java program structure")),

                lesson("Java program structure", 10,
                        List.of("Identify the parts of a Java source file",
                                "Use comments and imports correctly"),
                        """
                        A source file can contain, in order: an optional package declaration, import statements, and one or more class declarations. At most one class may be public, and the file must be named after it. Code that runs lives inside methods, and methods live inside classes.

                        Comments are ignored by the compiler. Use // for a single line, /* ... */ for several lines, and /** ... */ for documentation comments. Imports let you use classes from other packages without writing their full names. Classes in java.lang, such as String and System, are imported automatically.
                        """,
                        List.of(ex("The parts of a source file", """
                                import java.util.Arrays;

                                public class Structure {

                                    // A field: data that belongs to the class
                                    static int total = 0;

                                    /* Execution begins in the main method */
                                    public static void main(String[] args) {
                                        int[] numbers = {3, 1, 2};
                                        Arrays.sort(numbers);
                                        System.out.println(Arrays.toString(numbers));
                                    }
                                }
                                """, """
                                [1, 2, 3]
                                """)),
                        List.of("Follow the convention: class names in PascalCase, methods and variables in camelCase."),
                        List.of("Putting statements directly inside a class body instead of inside a method."),
                        List.of("Variables", "Classes and objects")),

                lesson("Variables", 12,
                        List.of("Declare, initialise and update variables",
                                "Follow Java naming rules and use constants"),
                        """
                        A variable is a named place in memory that holds a value of a particular type. You declare it with its type and a name, and usually give it an initial value: int age = 20;. After that you can read it and assign new values to it.

                        Names may contain letters, digits, underscores and dollar signs, cannot start with a digit, and cannot be a reserved word such as class. Add the keyword final to make a constant that can be assigned only once; by convention constants are written in UPPER_CASE. Since Java 10, var lets the compiler infer the type of a local variable from its initial value.
                        """,
                        List.of(ex("Declaring variables", """
                                public class Variables {
                                    public static void main(String[] args) {
                                        int age = 20;
                                        String name = "Asha";
                                        double price = 99.5;
                                        final int MAX_ATTEMPTS = 3;
                                        var city = "Pune";   // inferred as String

                                        age = age + 1;
                                        System.out.println(name + " is " + age);
                                        System.out.println("Price: " + price + ", attempts: " + MAX_ATTEMPTS + ", city: " + city);
                                    }
                                }
                                """, """
                                Asha is 21
                                Price: 99.5, attempts: 3, city: Pune
                                """)),
                        List.of("A local variable must be given a value before you read it; Java does not guess a default for locals."),
                        List.of("Using a variable that was never initialised.",
                                "Trying to change a final variable after its first assignment."),
                        List.of("Primitive data types", "Type casting")),

                lesson("Primitive data types", 12,
                        List.of("Name Java's eight primitive types and their typical use",
                                "Recognise overflow and the need for the L and f suffixes"),
                        """
                        Java has eight primitive types. The integer types are byte (8 bits), short (16), int (32) and long (64). The decimal types are float (32) and double (64). char holds one UTF-16 character, and boolean holds true or false. In everyday code, int is the default for whole numbers and double for decimals.

                        Each type has a fixed range. When a calculation goes beyond it, the value silently wraps around, which is called overflow. A long literal needs the suffix L and a float literal needs the suffix f, because plain literals are int and double. Underscores can make big numbers readable: 3_000_000_000L.
                        """,
                        List.of(ex("The primitive types in action", """
                                public class Primitives {
                                    public static void main(String[] args) {
                                        byte b = 127;
                                        int big = Integer.MAX_VALUE;
                                        long population = 3_000_000_000L;
                                        double pi = 3.14159;
                                        float ratio = 2.5f;
                                        char grade = 'A';
                                        boolean passed = true;

                                        System.out.println(b + " " + big + " " + population);
                                        System.out.println(pi + " " + ratio + " " + grade + " " + passed);
                                        System.out.println(big + 1);   // overflow wraps around
                                    }
                                }
                                """, """
                                127 2147483647 3000000000
                                3.14159 2.5 A true
                                -2147483648
                                """)),
                        List.of("Use BigDecimal, not double, for money: binary floating point cannot represent values like 0.1 exactly."),
                        List.of("Writing long x = 3000000000; without the L, which is an int literal that is too large.",
                                "Comparing double values with == after arithmetic."),
                        List.of("Reference types", "Type casting")),

                lesson("Reference types", 12,
                        List.of("Explain the difference between primitives and references",
                                "Compare objects correctly with equals"),
                        """
                        A primitive variable stores the value itself. A reference variable stores the address of an object, and the object lives elsewhere in memory. Strings, arrays and instances of classes are all objects. A reference that points to nothing holds the special value null.

                        Because a reference holds an address, the == operator on two references asks whether they point to the same object, not whether the objects look alike. To compare content, use the equals method. Assigning one reference to another copies the address, so both names then refer to the same object.
                        """,
                        List.of(ex("References versus content", """
                                public class References {
                                    public static void main(String[] args) {
                                        String a = new String("java");
                                        String b = new String("java");
                                        String c = a;

                                        System.out.println(a == b);       // different objects
                                        System.out.println(a.equals(b));  // same text
                                        System.out.println(a == c);       // same reference

                                        String missing = null;
                                        System.out.println(missing == null);
                                    }
                                }
                                """, """
                                false
                                true
                                true
                                true
                                """)),
                        List.of("Calling a method on a null reference throws a NullPointerException."),
                        List.of("Using == to compare strings. It may seem to work with string literals, but it is not reliable."),
                        List.of("Arrays", "String fundamentals")),

                lesson("Type casting", 10,
                        List.of("Convert between numeric types safely",
                                "Predict the result of integer division"),
                        """
                        Java converts a smaller type to a larger one automatically (widening), for example int to double, because no information is lost. Going the other way (narrowing) can lose information, so you must ask for it explicitly with a cast: (int) price. Casting a decimal to an integer drops the fractional part; it does not round.

                        When both operands of a division are integers, the result is an integer and the remainder is discarded, so 7 / 2 is 3. To get 3.5, make at least one operand a double, for example by casting before dividing.
                        """,
                        List.of(ex("Widening, narrowing and division", """
                                public class Casting {
                                    public static void main(String[] args) {
                                        int count = 9;
                                        double widened = count;          // automatic
                                        double price = 3.99;
                                        int truncated = (int) price;     // explicit, drops the decimals

                                        System.out.println(widened);
                                        System.out.println(truncated);
                                        System.out.println(7 / 2);
                                        System.out.println((double) 7 / 2);
                                        System.out.println((char) 66);
                                    }
                                }
                                """, """
                                9.0
                                3
                                3
                                3.5
                                B
                                """)),
                        List.of("The cast applies before the division in (double) 7 / 2, which is why the result is 3.5."),
                        List.of("Writing (double) (7 / 2): the integer division happens first and the result is already 3.",
                                "Expecting a cast to round a number."),
                        List.of("Operators", "Primitive data types")),

                lesson("Operators", 12,
                        List.of("Use arithmetic, relational, logical and ternary operators",
                                "Explain prefix and postfix increment"),
                        """
                        Arithmetic operators are +, -, *, / and % (remainder). Relational operators (==, !=, <, >, <=, >=) produce a boolean. Logical operators combine booleans: && (and), || (or) and ! (not). The && and || operators short-circuit: the right side is evaluated only if it is needed.

                        The increment operator ++ adds 1. In postfix form (i++) the old value is used first and then the variable changes; in prefix form (++i) the variable changes first. The ternary operator condition ? a : b is a compact if-else that produces a value. Parentheses make precedence explicit and are always worth using when in doubt.
                        """,
                        List.of(ex("A tour of the operators", """
                                public class Operators {
                                    public static void main(String[] args) {
                                        int a = 10, b = 3;
                                        System.out.println(a + b);
                                        System.out.println(a / b);
                                        System.out.println(a % b);

                                        boolean inRange = a > 5 && b < 5;
                                        System.out.println(inRange);

                                        int i = 5;
                                        System.out.println(i++);   // prints 5, then i becomes 6
                                        System.out.println(++i);   // i becomes 7, then prints 7

                                        String result = (a > b) ? "a is larger" : "b is larger";
                                        System.out.println(result);
                                    }
                                }
                                """, """
                                13
                                3
                                1
                                true
                                5
                                7
                                a is larger
                                """)),
                        List.of("Use % to test divisibility: n % 2 == 0 means n is even."),
                        List.of("Writing = (assignment) when you meant == (comparison).",
                                "Relying on operator precedence instead of using parentheses."),
                        List.of("Conditional statements")),

                lesson("User input", 12,
                        List.of("Read text and numbers with Scanner",
                                "Avoid the leftover-newline problem"),
                        """
                        The Scanner class reads input from a source. Passing System.in makes it read from the keyboard. Use nextLine() to read a whole line, nextInt() for an integer and nextDouble() for a decimal. Scanner lives in java.util, so it must be imported, and it is good practice to close it when you are done.

                        There is one classic trap. nextInt() reads the number but leaves the Enter key (the newline) in the input. If you call nextLine() right afterwards, it reads that leftover newline and returns an empty string. The fix is to call nextLine() once to consume the rest of the line before reading text.
                        """,
                        List.of(ex("Reading a name and an age (sample run)", """
                                import java.util.Scanner;

                                public class Greeting {
                                    public static void main(String[] args) {
                                        Scanner scanner = new Scanner(System.in);

                                        System.out.print("Enter your name: ");
                                        String name = scanner.nextLine();

                                        System.out.print("Enter your age: ");
                                        int age = scanner.nextInt();

                                        System.out.println("Hello " + name + ", next year you will be " + (age + 1));
                                        scanner.close();
                                    }
                                }
                                """, """
                                Enter your name: Riya
                                Enter your age: 19
                                Hello Riya, next year you will be 20
                                """)),
                        List.of("The sample output shows a run where the user typed Riya and then 19.",
                                "If the user types text where a number is expected, nextInt() throws an InputMismatchException."),
                        List.of("Calling nextLine() right after nextInt() and getting an empty string."),
                        List.of("Exception handling")),

                lesson("Conditional statements", 12,
                        List.of("Choose between paths with if, else if and else",
                                "Combine conditions with logical operators"),
                        """
                        An if statement runs a block only when its condition is true. Add else if for further conditions and else for the case where none matched. Conditions are checked from top to bottom, and the first true branch wins, so order matters: put the most specific or highest threshold first.

                        Conditions can be combined with && and ||. If statements can be nested, but deep nesting is hard to read; prefer a flat chain of else if branches or an early return when possible. Always use curly braces, even for one-line bodies, so adding a second statement later does not silently change the logic.
                        """,
                        List.of(ex("Grading marks", """
                                public class Grades {
                                    public static void main(String[] args) {
                                        int marks = 72;
                                        String grade;

                                        if (marks >= 90) {
                                            grade = "A";
                                        } else if (marks >= 75) {
                                            grade = "B";
                                        } else if (marks >= 60) {
                                            grade = "C";
                                        } else {
                                            grade = "D";
                                        }
                                        System.out.println("Grade: " + grade);
                                    }
                                }
                                """, """
                                Grade: C
                                """)),
                        List.of("Compare strings with equals, never with ==."),
                        List.of("Putting a semicolon right after the if condition, which makes the body unconditional.",
                                "Ordering else if branches so a broad condition hides a narrower one."),
                        List.of("Switch statements", "Operators")),

                lesson("Switch statements", 12,
                        List.of("Use classic switch and modern switch expressions",
                                "Explain fall-through and how break prevents it"),
                        """
                        A switch statement picks one branch based on the value of an expression such as an int, a char, a String or an enum. In the classic form each case ends with break; without it, execution falls through into the next case, which is a common source of bugs.

                        Since Java 14, the arrow form is preferred. Arrow cases do not fall through, and a switch can be an expression that produces a value, as in the example. Always include a default branch to handle unexpected values.
                        """,
                        List.of(ex("Modern and classic switch", """
                                public class Days {
                                    public static void main(String[] args) {
                                        int day = 3;

                                        // Modern switch expression (Java 14+)
                                        String name = switch (day) {
                                            case 1 -> "Monday";
                                            case 2 -> "Tuesday";
                                            case 3 -> "Wednesday";
                                            default -> "Another day";
                                        };
                                        System.out.println(name);

                                        // Classic switch: break stops the fall-through
                                        switch (day) {
                                            case 1:
                                                System.out.println("Start of week");
                                                break;
                                            case 3:
                                                System.out.println("Midweek");
                                                break;
                                            default:
                                                System.out.println("Other");
                                        }
                                    }
                                }
                                """, """
                                Wednesday
                                Midweek
                                """)),
                        List.of("Switching on a String uses equals internally, so it is safe to use."),
                        List.of("Forgetting break in a classic switch and running into the next case."),
                        List.of("Conditional statements")),

                lesson("For loops", 12,
                        List.of("Write counting loops with for",
                                "Use the enhanced for loop to visit every element"),
                        """
                        A for loop has three parts in its header: an initialiser that runs once, a condition checked before each pass, and an update that runs after each pass. It is the natural choice when you know how many times to repeat.

                        The enhanced for loop (for-each) visits every element of an array or collection without an index: for (int m : marks). Use it when you only need the values. Use the classic form when you need the index or want to change which elements are visited. Loops can be nested, for example to print a table.
                        """,
                        List.of(ex("Counting and visiting", """
                                public class Loops {
                                    public static void main(String[] args) {
                                        int sum = 0;
                                        for (int i = 1; i <= 5; i++) {
                                            sum += i;
                                        }
                                        System.out.println("Sum: " + sum);

                                        int[] marks = {10, 20, 30};
                                        for (int m : marks) {
                                            System.out.println(m);
                                        }
                                    }
                                }
                                """, """
                                Sum: 15
                                10
                                20
                                30
                                """)),
                        List.of("The loop variable declared in the header exists only inside the loop."),
                        List.of("Off-by-one errors: using < when you need <= (or the reverse).",
                                "Changing the collection you are looping over inside a for-each loop."),
                        List.of("While loops", "Arrays")),

                lesson("While loops", 10,
                        List.of("Repeat work while a condition holds",
                                "Avoid infinite loops"),
                        """
                        A while loop checks its condition before every pass and stops as soon as the condition is false. It is the right tool when you do not know in advance how many repetitions you need, for example processing digits of a number or reading until the user types quit.

                        Something inside the loop must eventually make the condition false, otherwise the loop never ends. Before running a new loop, ask yourself what changes each pass and why that change leads to the end.
                        """,
                        List.of(ex("Countdown and digit sum", """
                                public class WhileDemo {
                                    public static void main(String[] args) {
                                        int n = 3;
                                        while (n > 0) {
                                            System.out.println(n);
                                            n--;
                                        }
                                        System.out.println("Liftoff!");

                                        int number = 1234, digitSum = 0;
                                        while (number > 0) {
                                            digitSum += number % 10;   // last digit
                                            number /= 10;              // drop the last digit
                                        }
                                        System.out.println("Digit sum: " + digitSum);
                                    }
                                }
                                """, """
                                3
                                2
                                1
                                Liftoff!
                                Digit sum: 10
                                """)),
                        List.of("number % 10 gives the last digit and number / 10 removes it."),
                        List.of("Forgetting to update the variable in the condition, which creates an infinite loop."),
                        List.of("Do-while loops", "For loops")),

                lesson("Do-while loops", 8,
                        List.of("Explain how do-while differs from while",
                                "Choose do-while for run-at-least-once situations"),
                        """
                        A do-while loop runs its body first and checks the condition afterwards, so the body always executes at least once. This suits menus, input validation and any situation where you must try something before you can decide whether to repeat it.

                        Note the semicolon after the closing condition: do { ... } while (condition);. Forgetting it is a compile error.
                        """,
                        List.of(ex("At least one pass", """
                                public class DoWhileDemo {
                                    public static void main(String[] args) {
                                        int n = 10;
                                        do {
                                            System.out.println("n = " + n);
                                            n++;
                                        } while (n < 5);   // false straight away, but the body already ran once

                                        int attempts = 0;
                                        do {
                                            attempts++;
                                        } while (attempts < 3);
                                        System.out.println("Attempts: " + attempts);
                                    }
                                }
                                """, """
                                n = 10
                                Attempts: 3
                                """)),
                        List.of("Use while when the body might need to run zero times, and do-while when it must run at least once."),
                        List.of("Leaving out the semicolon after the while condition."),
                        List.of("While loops")),

                lesson("Break and continue", 10,
                        List.of("Stop a loop early with break",
                                "Skip one iteration with continue"),
                        """
                        break ends the nearest enclosing loop (or switch) immediately and continues with the code after it. continue skips the rest of the current iteration and jumps to the next one: in a for loop, to the update step, and in a while loop, to the condition check.

                        Use them sparingly. They are handy for guard conditions, such as skipping invalid items or stopping once you have found what you were looking for, but too many of them make loops hard to follow. A labelled break (break outer;) can leave an outer loop from inside a nested one.
                        """,
                        List.of(ex("Skip even numbers and stop after 7", """
                                public class BreakContinue {
                                    public static void main(String[] args) {
                                        for (int i = 1; i <= 10; i++) {
                                            if (i % 2 == 0) {
                                                continue;     // skip even numbers
                                            }
                                            if (i > 7) {
                                                break;        // stop the loop completely
                                            }
                                            System.out.println(i);
                                        }
                                    }
                                }
                                """, """
                                1
                                3
                                5
                                7
                                """)),
                        List.of("In nested loops, break and continue affect only the innermost loop unless you use a label."),
                        List.of("Using continue in a while loop before the update statement, which skips the update and loops forever."),
                        List.of("For loops", "While loops"))
        );

        QuizSeed quiz = new QuizSeed("Java Fundamentals Quiz",
                "Check your understanding of compiling, types, operators and loops.",
                List.of(
                        q("Which tool compiles a .java file into bytecode?", 1,
                                "javac is the Java compiler. The java command runs compiled bytecode on the JVM.",
                                "java", "javac", "jar", "javadoc"),
                        q("What does 7 / 2 evaluate to when both operands are int?", 1,
                                "Integer division discards the remainder, so 7 / 2 is 3.",
                                "3.5", "3", "4", "3.0"),
                        q("Which of these is NOT a primitive type?", 2,
                                "String is a class, so it is a reference type. int, boolean and char are primitives.",
                                "int", "boolean", "String", "char"),
                        q("Which loop always runs its body at least once?", 2,
                                "A do-while loop checks its condition after the body, so the body runs at least once.",
                                "for", "while", "do-while", "enhanced for"),
                        q("Two Strings are created with new String(\"java\"). What does == compare?", 1,
                                "== compares references, so it asks whether both names point to the same object. Use equals to compare the text.",
                                "Their text content", "Whether they are the same object in memory", "Their lengths", "Their hash codes"),
                        q("Which keyword makes a variable a constant that cannot be reassigned?", 3,
                                "A final variable can be assigned only once.",
                                "static", "const", "constant", "final")
                ));

        List<PracticeSeed> practiceSet = List.of(

                practice("Even or odd", "EASY",
                        "Write a program that stores the number 17 in a variable and prints whether it is even or odd.",
                        """
                        public class EvenOdd {
                            public static void main(String[] args) {
                                int number = 17;
                                // TODO: print whether number is even or odd
                            }
                        }
                        """,
                        "A number is even when number % 2 equals 0.",
                        """
                        public class EvenOdd {
                            public static void main(String[] args) {
                                int number = 17;
                                if (number % 2 == 0) {
                                    System.out.println(number + " is even");
                                } else {
                                    System.out.println(number + " is odd");
                                }
                            }
                        }
                        """,
                        "17 is odd"),

                practice("Sum of 1 to 100", "EASY",
                        "Use a loop to add up all whole numbers from 1 to 100 and print the total.",
                        """
                        public class SumToHundred {
                            public static void main(String[] args) {
                                // TODO: add the numbers 1 to 100 and print "Sum: <total>"
                            }
                        }
                        """,
                        "Keep a running total in an int variable and add the loop counter to it on every pass.",
                        """
                        public class SumToHundred {
                            public static void main(String[] args) {
                                int sum = 0;
                                for (int i = 1; i <= 100; i++) {
                                    sum += i;
                                }
                                System.out.println("Sum: " + sum);
                            }
                        }
                        """,
                        "Sum: 5050"),

                practice("FizzBuzz", "MEDIUM",
                        "Print the numbers from 1 to 15, one per line. For multiples of 3 print Fizz instead of the number, for multiples of 5 print Buzz, and for multiples of both print FizzBuzz.",
                        """
                        public class FizzBuzz {
                            public static void main(String[] args) {
                                // TODO: loop from 1 to 15 and print the right text for each number
                            }
                        }
                        """,
                        "Test the multiple-of-both case (divisible by 15) first, otherwise it will be caught by the Fizz or Buzz check.",
                        """
                        public class FizzBuzz {
                            public static void main(String[] args) {
                                for (int i = 1; i <= 15; i++) {
                                    if (i % 15 == 0) {
                                        System.out.println("FizzBuzz");
                                    } else if (i % 3 == 0) {
                                        System.out.println("Fizz");
                                    } else if (i % 5 == 0) {
                                        System.out.println("Buzz");
                                    } else {
                                        System.out.println(i);
                                    }
                                }
                            }
                        }
                        """,
                        """
                        1
                        2
                        Fizz
                        4
                        Buzz
                        Fizz
                        7
                        8
                        Fizz
                        Buzz
                        11
                        Fizz
                        13
                        14
                        FizzBuzz
                        """)
        );

        return new ModuleSeed("Java Fundamentals",
                "Set up Java, write your first programs, and learn variables, types, operators, decisions and loops.",
                lessons, quiz, practiceSet);
    }
    // ====================================================================================
    //  MODULE 2: METHODS AND ARRAYS
    // ====================================================================================

    static ModuleSeed module2() {
        List<LessonSeed> lessons = List.of(

                lesson("Methods", 10,
                        List.of("Explain why programs are split into methods",
                                "Declare a method and call it from main"),
                        """
                        A method is a named block of code that does one job. Instead of repeating the same lines in several places, you write them once, give them a name, and call that name whenever you need the job done. Methods make programs shorter, easier to read and easier to fix, because a change in one place applies everywhere.

                        A method declaration has a return type, a name, a parameter list in parentheses and a body in braces. A method that returns nothing uses the return type void. For now we mark methods static so that main can call them directly; the lessons on object-oriented programming explain non-static methods. A method runs only when it is called, and when it finishes, execution continues right after the call.
                        """,
                        List.of(ex("Declaring and calling methods", """
                                public class Methods {

                                    static void greet() {
                                        System.out.println("Welcome to CodeNest!");
                                    }

                                    static void printBorder() {
                                        System.out.println("==========");
                                    }

                                    public static void main(String[] args) {
                                        printBorder();
                                        greet();
                                        printBorder();
                                    }
                                }
                                """, """
                                ==========
                                Welcome to CodeNest!
                                ==========
                                """)),
                        List.of("Give methods verb-like names that say what they do, such as printReport or calculateTotal."),
                        List.of("Writing the method name without parentheses when calling it: greet; does nothing useful.",
                                "Declaring one method inside the body of another. Java does not allow that."),
                        List.of("Method parameters", "Return values")),

                lesson("Method parameters", 12,
                        List.of("Pass values into a method through parameters",
                                "Explain that Java passes arguments by value",
                                "Use varargs for a variable number of arguments"),
                        """
                        Parameters are variables listed in the method declaration; arguments are the actual values you supply when you call it. Java always passes arguments by value: the method receives a copy. If you pass an int and the method changes its parameter, the caller's variable is unaffected.

                        With objects and arrays, the copy that is passed is a copy of the reference, so both the caller and the method point to the same object. The method cannot make the caller's variable point elsewhere, but it can change the contents of the shared object. A varargs parameter (int... numbers) lets a caller pass any number of values; inside the method it behaves like an array.
                        """,
                        List.of(ex("Pass by value, shared arrays and varargs", """
                                public class Parameters {

                                    static void tryToChange(int x) {
                                        x = 100;
                                    }

                                    static void changeFirst(int[] data) {
                                        data[0] = 100;
                                    }

                                    static int sum(int... numbers) {
                                        int total = 0;
                                        for (int n : numbers) {
                                            total += n;
                                        }
                                        return total;
                                    }

                                    public static void main(String[] args) {
                                        int n = 5;
                                        tryToChange(n);
                                        System.out.println(n);

                                        int[] arr = {1, 2, 3};
                                        changeFirst(arr);
                                        System.out.println(arr[0]);

                                        System.out.println(sum(1, 2, 3));
                                        System.out.println(sum());
                                    }
                                }
                                """, """
                                5
                                100
                                6
                                0
                                """)),
                        List.of("A varargs parameter must be the last parameter of the method, and a method can have only one."),
                        List.of("Expecting a method to change the caller's int or String variable by assigning to its parameter.",
                                "Passing arguments in a different order than the parameters are declared."),
                        List.of("Methods", "Arrays")),

                lesson("Return values", 10,
                        List.of("Return a value from a method with return",
                                "Match the return type to the value returned"),
                        """
                        A method with a return type other than void must hand a value back to the caller using the return statement, and the value must match the declared type. The call expression then stands for that value, so you can store it in a variable, print it, or use it in a bigger expression.

                        return also ends the method immediately, so any code after it in the same path is never reached. Every possible path through a non-void method must end in a return (or a thrown exception); otherwise the compiler reports "missing return statement". Methods that answer yes/no questions conventionally return boolean and are named like isEven or hasAccess.
                        """,
                        List.of(ex("Methods that return values", """
                                public class ReturnValues {

                                    static int square(int n) {
                                        return n * n;
                                    }

                                    static boolean isEven(int n) {
                                        return n % 2 == 0;
                                    }

                                    static double average(int a, int b) {
                                        return (a + b) / 2.0;
                                    }

                                    public static void main(String[] args) {
                                        System.out.println(square(6));
                                        System.out.println(isEven(7));
                                        System.out.println(average(3, 4));
                                    }
                                }
                                """, """
                                36
                                false
                                3.5
                                """)),
                        List.of("Dividing by 2.0 instead of 2 keeps the decimal part, so average(3, 4) is 3.5 rather than 3."),
                        List.of("Calling a method that returns a value and ignoring the result, as if it had changed something.",
                                "Forgetting a return on one branch of an if statement."),
                        List.of("Method overloading", "Type casting")),

                lesson("Method overloading", 10,
                        List.of("Define several methods with the same name",
                                "Explain how Java picks the right overload"),
                        """
                        Overloading means giving two or more methods the same name but different parameter lists, meaning a different number of parameters, different types, or a different order of types. It lets related operations share one natural name, like println working for int, String and double.

                        The compiler chooses the overload from the arguments you pass. The return type alone is not enough to tell overloads apart: two methods that differ only in return type will not compile. When there is no exact match, Java tries the nearest widening conversion, for example int to double.
                        """,
                        List.of(ex("Three versions of add", """
                                public class Overloading {

                                    static int add(int a, int b) {
                                        return a + b;
                                    }

                                    static double add(double a, double b) {
                                        return a + b;
                                    }

                                    static int add(int a, int b, int c) {
                                        return a + b + c;
                                    }

                                    public static void main(String[] args) {
                                        System.out.println(add(2, 3));
                                        System.out.println(add(2.5, 1.5));
                                        System.out.println(add(1, 2, 3));
                                    }
                                }
                                """, """
                                5
                                4.0
                                6
                                """)),
                        List.of("Overloading is decided at compile time from the argument types; overriding, covered later, is decided at run time."),
                        List.of("Trying to overload by changing only the return type.",
                                "Creating overloads that are so similar that a call becomes ambiguous."),
                        List.of("Methods", "Method overriding")),

                lesson("Arrays", 12,
                        List.of("Create arrays and access elements by index",
                                "Use length and avoid out-of-bounds errors"),
                        """
                        An array stores a fixed number of values of the same type in one variable. Elements are numbered from 0, so an array of length 5 has indexes 0 to 4. You can create one with values, int[] marks = {70, 85, 60};, or with a size, int[] zeros = new int[3];, in which case every element starts with a default value: 0 for numbers, false for boolean and null for objects.

                        The length is available as the field array.length (no parentheses, unlike String's length()). The length of an array can never change after it is created. Reading or writing an index outside 0 to length-1 throws an ArrayIndexOutOfBoundsException. The java.util.Arrays class has helpers such as toString, sort, fill and copyOf.
                        """,
                        List.of(ex("Working with an array", """
                                import java.util.Arrays;

                                public class ArrayBasics {
                                    public static void main(String[] args) {
                                        int[] marks = {70, 85, 60};
                                        marks[1] = 90;

                                        int total = 0;
                                        for (int m : marks) {
                                            total += m;
                                        }

                                        System.out.println(marks.length);
                                        System.out.println(total);
                                        System.out.println(Arrays.toString(marks));

                                        int[] zeros = new int[3];
                                        System.out.println(Arrays.toString(zeros));
                                    }
                                }
                                """, """
                                3
                                220
                                [70, 90, 60]
                                [0, 0, 0]
                                """)),
                        List.of("Printing an array directly with println shows a memory-style label, not the contents. Use Arrays.toString."),
                        List.of("Looping with i <= marks.length, which reads one position past the end.",
                                "Writing marks.length() with parentheses; for arrays it is a field."),
                        List.of("Multidimensional arrays", "For loops")),

                lesson("Multidimensional arrays", 12,
                        List.of("Create and read two-dimensional arrays",
                                "Loop over rows and columns"),
                        """
                        A two-dimensional array is an array of arrays, which you can picture as a table. int[][] grid = {{1, 2, 3}, {4, 5, 6}}; has two rows and three columns. grid[1][2] means "row 1, column 2". grid.length is the number of rows and grid[0].length is the number of columns in the first row.

                        Because each row is its own array, rows can have different lengths. These are called jagged arrays. To visit every element, use a loop inside a loop: the outer loop over rows and the inner loop over the values of one row.
                        """,
                        List.of(ex("A 2 x 3 grid", """
                                import java.util.Arrays;

                                public class Grid {
                                    public static void main(String[] args) {
                                        int[][] grid = {
                                            {1, 2, 3},
                                            {4, 5, 6}
                                        };

                                        System.out.println(grid[1][2]);

                                        for (int[] row : grid) {
                                            System.out.println(Arrays.toString(row));
                                        }

                                        System.out.println(grid.length + " x " + grid[0].length);
                                    }
                                }
                                """, """
                                6
                                [1, 2, 3]
                                [4, 5, 6]
                                2 x 3
                                """)),
                        List.of("Arrays.deepToString(grid) prints a whole two-dimensional array in one call."),
                        List.of("Mixing up the row and column index: it is always grid[row][column]."),
                        List.of("Arrays")),

                lesson("String fundamentals", 12,
                        List.of("Explain that Strings are immutable objects",
                                "Understand the string pool and why equals is needed"),
                        """
                        A String is an object that holds a sequence of characters. Strings are immutable: once created, their content never changes. Methods such as toUpperCase do not modify the original; they return a new String, so you must keep the result if you want it.

                        String literals written in double quotes are stored in a shared area called the string pool, so two identical literals refer to the same object. A String created with new is a separate object even when its text is the same. That is why == (which compares references) can give surprising answers, and why you should always compare text with equals. The + operator joins strings together, turning other values into text as needed.
                        """,
                        List.of(ex("Immutability and equality", """
                                public class StringBasics {
                                    public static void main(String[] args) {
                                        String s = "Java";
                                        s.toUpperCase();
                                        System.out.println(s);

                                        String upper = s.toUpperCase();
                                        System.out.println(upper);

                                        String a = "hi";
                                        String b = "hi";
                                        String c = new String("hi");
                                        System.out.println(a == b);
                                        System.out.println(a == c);
                                        System.out.println(a.equals(c));
                                    }
                                }
                                """, """
                                Java
                                JAVA
                                true
                                false
                                true
                                """)),
                        List.of("Because strings are immutable, they are safe to share between parts of a program and to use as map keys."),
                        List.of("Calling s.toUpperCase() and expecting s itself to change.",
                                "Using == to compare the text of two strings."),
                        List.of("Common String methods", "StringBuilder")),

                lesson("Common String methods", 14,
                        List.of("Use the most common String methods",
                                "Split and join text"),
                        """
                        The String class offers many ready-made methods. length() gives the number of characters, charAt(i) returns the character at an index, and substring(start) or substring(start, end) extracts a part (the end index is not included). indexOf finds where some text begins, or returns -1 if it is absent. trim removes spaces at both ends, toLowerCase and toUpperCase change case, replace swaps text, contains and startsWith answer yes/no questions, and equalsIgnoreCase compares ignoring case.

                        split breaks a string into an array around a separator, and String.join glues an array or list back together with a separator. Remember every one of these returns a new value and leaves the original string unchanged.
                        """,
                        List.of(ex("A tour of String methods", """
                                public class StringMethods {
                                    public static void main(String[] args) {
                                        String text = "  Hello, CodeNest  ";
                                        String t = text.trim();

                                        System.out.println(t.length());
                                        System.out.println(t.charAt(0));
                                        System.out.println(t.substring(7));
                                        System.out.println(t.indexOf("Code"));
                                        System.out.println(t.toLowerCase());
                                        System.out.println(t.replace("Code", "Java"));
                                        System.out.println(t.contains("Nest"));

                                        String csv = "red,green,blue";
                                        String[] parts = csv.split(",");
                                        System.out.println(parts.length);
                                        System.out.println(String.join(" | ", parts));
                                        System.out.println("Java".equalsIgnoreCase("JAVA"));
                                    }
                                }
                                """, """
                                15
                                H
                                CodeNest
                                7
                                hello, codenest
                                Hello, JavaNest
                                true
                                3
                                red | green | blue
                                true
                                """)),
                        List.of("substring(7) starts at index 7 and runs to the end; substring(0, 5) returns the characters at indexes 0 to 4."),
                        List.of("Asking for a substring or charAt index that is outside the string, which throws StringIndexOutOfBoundsException.",
                                "Forgetting that split takes a regular expression, so characters like | and . need special care."),
                        List.of("String fundamentals", "StringBuilder")),

                lesson("StringBuilder", 10,
                        List.of("Build text efficiently with StringBuilder",
                                "Choose between String and StringBuilder"),
                        """
                        Because strings are immutable, joining strings in a loop with + creates a brand-new String on every pass, which wastes time and memory. StringBuilder is a mutable sequence of characters: it changes in place and creates the final String only when you ask for it.

                        Common methods are append (add to the end, and it returns the builder so calls can be chained), insert, reverse, delete, setLength and toString. Use String for text that does not change, and StringBuilder when you assemble text piece by piece, especially inside loops. StringBuilder is not thread-safe; StringBuffer is the synchronised older cousin.
                        """,
                        List.of(ex("Appending, inserting and reversing", """
                                public class BuilderDemo {
                                    public static void main(String[] args) {
                                        StringBuilder sb = new StringBuilder("Code");
                                        sb.append("Nest");
                                        sb.insert(0, ">> ");
                                        System.out.println(sb.toString());

                                        sb.reverse();
                                        System.out.println(sb);

                                        sb.setLength(0);
                                        System.out.println(sb.length());

                                        StringBuilder digits = new StringBuilder();
                                        for (int i = 0; i < 5; i++) {
                                            digits.append(i);
                                        }
                                        System.out.println(digits);
                                    }
                                }
                                """, """
                                >> CodeNest
                                tseNedoC >>
                                0
                                01234
                                """)),
                        List.of("sb.setLength(0) empties a StringBuilder so it can be reused."),
                        List.of("Using += on a String inside a big loop instead of a StringBuilder.",
                                "Comparing two StringBuilder objects with equals, which does not compare their text. Compare their toString() results."),
                        List.of("String fundamentals", "Common String methods"))
        );

        QuizSeed quiz = new QuizSeed("Methods, Arrays and Strings Quiz",
                "Test what you know about methods, arrays, String and StringBuilder.",
                List.of(
                        q("What does a method declared with the return type void do?", 2,
                                "A void method performs its work but gives no value back to the caller.",
                                "Returns an int by default", "Returns null", "Returns no value", "Cannot take parameters"),
                        q("Which pair of methods is a valid example of overloading?", 1,
                                "Overloads must differ in their parameter lists. Changing only the return type is not enough.",
                                "int add(int a, int b) and double add(int a, int b)",
                                "int add(int a, int b) and int add(int a, int b, int c)",
                                "void run() and void run()",
                                "int f(int a) and int g(int a)"),
                        q("An array is created with new int[5]. What is the index of its last element?", 1,
                                "Indexes start at 0, so an array of length 5 has indexes 0 to 4.",
                                "5", "4", "6", "0"),
                        q("What values does new int[3] contain right after creation?", 1,
                                "Numeric array elements start with the default value 0.",
                                "null null null", "0 0 0", "Unpredictable values", "It does not compile"),
                        q("String s = \"java\"; s.toUpperCase(); What is s afterwards?", 1,
                                "Strings are immutable. toUpperCase returns a new String and leaves s as it was.",
                                "JAVA", "java", "null", "Compile error"),
                        q("Which class is best for assembling a long string piece by piece inside a loop?", 1,
                                "StringBuilder changes in place, while joining Strings with + creates a new object each time.",
                                "String", "StringBuilder", "Scanner", "Arrays")
                ));

        List<PracticeSeed> practiceSet = List.of(

                practice("Largest number in an array", "EASY",
                        "Given the array {4, 17, 9, 23, 8}, find the largest value without sorting and print it as Maximum: <value>.",
                        """
                        public class MaxValue {
                            public static void main(String[] args) {
                                int[] numbers = {4, 17, 9, 23, 8};
                                // TODO: find the largest number and print "Maximum: <value>"
                            }
                        }
                        """,
                        "Start with max equal to the first element, then replace it whenever you meet a bigger number.",
                        """
                        public class MaxValue {
                            public static void main(String[] args) {
                                int[] numbers = {4, 17, 9, 23, 8};
                                int max = numbers[0];
                                for (int n : numbers) {
                                    if (n > max) {
                                        max = n;
                                    }
                                }
                                System.out.println("Maximum: " + max);
                            }
                        }
                        """,
                        "Maximum: 23"),

                practice("Count the vowels", "MEDIUM",
                        "Count how many vowels (a, e, i, o, u, in either case) appear in the text \"Programming in Java\" and print Vowels: <count>.",
                        """
                        public class Vowels {
                            public static void main(String[] args) {
                                String text = "Programming in Java";
                                // TODO: count the vowels and print "Vowels: <count>"
                            }
                        }
                        """,
                        "Loop over the positions of the string, use charAt, and convert each character to lower case before comparing.",
                        """
                        public class Vowels {
                            public static void main(String[] args) {
                                String text = "Programming in Java";
                                int count = 0;
                                for (int i = 0; i < text.length(); i++) {
                                    char c = Character.toLowerCase(text.charAt(i));
                                    if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                                        count++;
                                    }
                                }
                                System.out.println("Vowels: " + count);
                            }
                        }
                        """,
                        "Vowels: 6"),

                practice("Reverse a string", "MEDIUM",
                        "Reverse the text \"CodeNest\" by looping over it (do not just call reverse()) and print the result.",
                        """
                        public class ReverseText {
                            public static void main(String[] args) {
                                String text = "CodeNest";
                                // TODO: build the reversed text and print it
                            }
                        }
                        """,
                        "Loop from the last index (text.length() - 1) down to 0 and append each character to a StringBuilder.",
                        """
                        public class ReverseText {
                            public static void main(String[] args) {
                                String text = "CodeNest";
                                StringBuilder reversed = new StringBuilder();
                                for (int i = text.length() - 1; i >= 0; i--) {
                                    reversed.append(text.charAt(i));
                                }
                                System.out.println(reversed);
                            }
                        }
                        """,
                        "tseNedoC")
        );

        return new ModuleSeed("Methods and Arrays",
                "Organise code into methods, store data in arrays, and work confidently with text using String and StringBuilder.",
                lessons, quiz, practiceSet);
    }

    // ====================================================================================
    //  MODULE 3: OBJECT-ORIENTED PROGRAMMING
    // ====================================================================================

    static ModuleSeed module3() {
        List<LessonSeed> lessons = List.of(

                lesson("Classes and objects", 12,
                        List.of("Explain the relationship between a class and an object",
                                "Create objects and use their fields and methods"),
                        """
                        A class is a blueprint: it describes what data something has (fields) and what it can do (methods). An object is one concrete thing built from that blueprint, created with the new keyword. You can make as many objects as you like from one class, and each has its own copy of the fields.

                        You reach an object's fields and methods with the dot operator, such as s1.name or s1.introduce(). Objects are reference types, so a variable holds a reference to the object. Assigning one object variable to another copies the reference, and both names then refer to the same object. In a source file, classes other than the public one can be written in the same file.
                        """,
                        List.of(ex("A Student class", """
                                class Student {
                                    String name;
                                    int marks;

                                    void introduce() {
                                        System.out.println("I am " + name + " and I scored " + marks);
                                    }
                                }

                                public class ClassesDemo {
                                    public static void main(String[] args) {
                                        Student s1 = new Student();
                                        s1.name = "Asha";
                                        s1.marks = 88;

                                        Student s2 = new Student();
                                        s2.name = "Ravi";
                                        s2.marks = 74;

                                        s1.introduce();
                                        s2.introduce();

                                        Student s3 = s1;
                                        s3.marks = 95;
                                        System.out.println(s1.marks);
                                    }
                                }
                                """, """
                                I am Asha and I scored 88
                                I am Ravi and I scored 74
                                95
                                """)),
                        List.of("The last line prints 95 because s3 and s1 are two names for the same object."),
                        List.of("Using a field or method of a class without first creating an object (for non-static members).",
                                "Thinking that Student s3 = s1; makes a copy of the object."),
                        List.of("Constructors", "Reference types")),

                lesson("Constructors", 12,
                        List.of("Write constructors to initialise objects",
                                "Explain the default constructor"),
                        """
                        A constructor is a special block of code that runs when an object is created with new. It has the same name as the class and no return type, not even void. Its job is to put the new object into a valid starting state.

                        If you write no constructor at all, Java supplies an invisible default constructor with no parameters. As soon as you write any constructor, that free one disappears, so add a no-argument version yourself if you still want one. A class can have several constructors with different parameter lists, which is constructor overloading.
                        """,
                        List.of(ex("Two constructors", """
                                class Book {
                                    String title;
                                    double price;

                                    Book() {
                                        title = "Untitled";
                                        price = 0;
                                    }

                                    Book(String t, double p) {
                                        title = t;
                                        price = p;
                                    }

                                    void show() {
                                        System.out.println(title + " - " + price);
                                    }
                                }

                                public class ConstructorDemo {
                                    public static void main(String[] args) {
                                        new Book().show();
                                        new Book("Java Basics", 499.0).show();
                                    }
                                }
                                """, """
                                Untitled - 0.0
                                Java Basics - 499.0
                                """)),
                        List.of("A constructor with required parameters guarantees that nobody can create a half-initialised object."),
                        List.of("Adding a return type such as void to a constructor. That turns it into an ordinary method.",
                                "Writing a parameterised constructor and then calling new ClassName() with no arguments, which no longer compiles."),
                        List.of("this keyword", "Classes and objects")),

                lesson("this keyword", 10,
                        List.of("Use this to refer to the current object",
                                "Call one constructor from another with this(...)"),
                        """
                        Inside a method or constructor, this is a reference to the object currently being worked on. Its most common use is to tell a field apart from a parameter with the same name: in this.x = x; the left side is the field and the right side is the parameter. Without this, the assignment would just copy the parameter onto itself.

                        this(...) on the first line of a constructor calls another constructor of the same class, so common setup lives in one place. A method can also return this, which allows chained calls such as p.moveRight(5).moveRight(1).
                        """,
                        List.of(ex("this in constructors and chaining", """
                                class Point {
                                    int x, y;

                                    Point(int x, int y) {
                                        this.x = x;
                                        this.y = y;
                                    }

                                    Point() {
                                        this(0, 0);
                                    }

                                    Point moveRight(int distance) {
                                        this.x += distance;
                                        return this;
                                    }

                                    void show() {
                                        System.out.println("(" + x + ", " + y + ")");
                                    }
                                }

                                public class ThisDemo {
                                    public static void main(String[] args) {
                                        Point origin = new Point();
                                        origin.show();

                                        new Point(2, 3).moveRight(5).moveRight(1).show();
                                    }
                                }
                                """, """
                                (0, 0)
                                (8, 3)
                                """)),
                        List.of("A call to this(...) or super(...) must be the very first statement in a constructor."),
                        List.of("Writing x = x; in a constructor, which assigns the parameter to itself and leaves the field unchanged."),
                        List.of("Constructors", "Encapsulation")),

                lesson("Encapsulation", 12,
                        List.of("Protect an object's data with private fields",
                                "Validate changes through methods"),
                        """
                        Encapsulation means bundling data and the code that works on it together, and hiding the data from outside code. You declare fields private and offer public methods (getters, setters or better-named operations like deposit) as the only way in. Because every change goes through a method, the class can check it and refuse invalid ones.

                        This protects the object's own rules. A bank balance, for instance, should never become negative through a careless assignment. It also gives you freedom: you can change how the data is stored inside the class without breaking the code that uses it.
                        """,
                        List.of(ex("A protected bank balance", """
                                class Account {
                                    private double balance;

                                    Account(double opening) {
                                        if (opening >= 0) {
                                            balance = opening;
                                        }
                                    }

                                    void deposit(double amount) {
                                        if (amount <= 0) {
                                            System.out.println("Deposit must be positive");
                                            return;
                                        }
                                        balance += amount;
                                    }

                                    boolean withdraw(double amount) {
                                        if (amount <= 0 || amount > balance) {
                                            return false;
                                        }
                                        balance -= amount;
                                        return true;
                                    }

                                    double getBalance() {
                                        return balance;
                                    }
                                }

                                public class EncapsulationDemo {
                                    public static void main(String[] args) {
                                        Account acc = new Account(500);
                                        acc.deposit(250);
                                        System.out.println(acc.getBalance());
                                        System.out.println(acc.withdraw(1000));
                                        System.out.println(acc.withdraw(200));
                                        System.out.println(acc.getBalance());
                                        // acc.balance = -1;   // would not compile: balance is private
                                    }
                                }
                                """, """
                                750.0
                                false
                                true
                                550.0
                                """)),
                        List.of("Do not generate a setter for every field by reflex. Offer only the operations that make sense for the object."),
                        List.of("Making fields public for convenience, which lets any code break the object's rules.",
                                "Writing getters and setters that simply expose the field with no validation at all."),
                        List.of("Access modifiers", "this keyword")),

                lesson("Inheritance", 12,
                        List.of("Create a subclass with extends",
                                "Use super to reach the parent class"),
                        """
                        Inheritance lets one class (the subclass or child) reuse and extend another class (the superclass or parent) with the keyword extends. The child automatically has the parent's accessible fields and methods and can add its own. It models an "is-a" relationship: a Dog is an Animal.

                        Java allows a class to extend only one other class. Every class ultimately inherits from java.lang.Object. When a child object is created, the parent's constructor runs first; use super(...) as the first line of the child's constructor to choose which parent constructor to call, and super.method() to call the parent's version of a method.
                        """,
                        List.of(ex("Dog extends Animal", """
                                class Animal {
                                    String name;

                                    Animal(String name) {
                                        this.name = name;
                                    }

                                    void eat() {
                                        System.out.println(name + " is eating");
                                    }
                                }

                                class Dog extends Animal {

                                    Dog(String name) {
                                        super(name);
                                    }

                                    void bark() {
                                        System.out.println(name + " says Woof");
                                    }
                                }

                                public class InheritanceDemo {
                                    public static void main(String[] args) {
                                        Dog d = new Dog("Bruno");
                                        d.eat();
                                        d.bark();
                                    }
                                }
                                """, """
                                Bruno is eating
                                Bruno says Woof
                                """)),
                        List.of("Use inheritance only for a real is-a relationship. If you just want to reuse some code, use a field of another class instead (composition)."),
                        List.of("Forgetting that the parent class has no no-argument constructor, so the child must call super(...) explicitly.",
                                "Trying to extend two classes at once."),
                        List.of("Method overriding", "Polymorphism")),

                lesson("Polymorphism", 12,
                        List.of("Treat objects of different subclasses through one parent type",
                                "Explain dynamic method dispatch"),
                        """
                        Polymorphism means "many forms". A variable of a parent type can refer to an object of any of its subclasses, and when you call an overridden method through it, Java runs the version belonging to the actual object, decided at run time. This is called dynamic dispatch.

                        The payoff is code that works with the general type and automatically does the right thing for each specific kind. A loop over an array of Shape objects can ask each one for its area without knowing whether it is looking at a Circle or a Rectangle, and you can add new shapes later without touching the loop.
                        """,
                        List.of(ex("One loop, different shapes", """
                                class Shape {
                                    double area() {
                                        return 0;
                                    }
                                }

                                class Circle extends Shape {
                                    double r;

                                    Circle(double r) {
                                        this.r = r;
                                    }

                                    @Override
                                    double area() {
                                        return Math.PI * r * r;
                                    }
                                }

                                class Rectangle extends Shape {
                                    double w, h;

                                    Rectangle(double w, double h) {
                                        this.w = w;
                                        this.h = h;
                                    }

                                    @Override
                                    double area() {
                                        return w * h;
                                    }
                                }

                                public class PolymorphismDemo {
                                    public static void main(String[] args) {
                                        Shape[] shapes = {new Circle(1), new Rectangle(2, 3)};
                                        for (Shape s : shapes) {
                                            System.out.printf("%.2f%n", s.area());
                                        }
                                    }
                                }
                                """, """
                                3.14
                                6.00
                                """)),
                        List.of("Which method runs depends on the object's real type; which methods you may call depends on the variable's declared type."),
                        List.of("Calling a method that exists only in the subclass through a parent-type variable, which does not compile unless you cast."),
                        List.of("Method overriding", "Abstraction")),

                lesson("Method overriding", 10,
                        List.of("Override a parent method in a child class",
                                "Use @Override and super.method() correctly"),
                        """
                        Overriding means a subclass provides its own version of a method it inherited, with the same name and parameters. When the method is called on a subclass object, the subclass version runs. Always add the @Override annotation: it makes the compiler check that you really are overriding something, so a typo in the name becomes an error instead of a silent new method.

                        Inside the override, super.method() calls the parent's version, which is useful when you want to extend the behaviour instead of replacing it. An overriding method cannot reduce visibility (public cannot become private), and private, static and final methods cannot be overridden.
                        """,
                        List.of(ex("A manager earns more", """
                                class Employee {
                                    double salary() {
                                        return 30000;
                                    }

                                    String describe() {
                                        return "Employee earning " + salary();
                                    }
                                }

                                class Manager extends Employee {
                                    @Override
                                    double salary() {
                                        return super.salary() + 20000;
                                    }
                                }

                                public class OverridingDemo {
                                    public static void main(String[] args) {
                                        Employee e = new Employee();
                                        Employee m = new Manager();
                                        System.out.println(e.describe());
                                        System.out.println(m.describe());
                                    }
                                }
                                """, """
                                Employee earning 30000.0
                                Employee earning 50000.0
                                """)),
                        List.of("Notice that describe() is written once in Employee, yet it uses the Manager's salary() for a Manager object."),
                        List.of("Misspelling the method name and omitting @Override, which creates an unrelated new method.",
                                "Confusing overriding (same signature, in a subclass) with overloading (different parameters)."),
                        List.of("Method overloading", "Polymorphism")),

                lesson("Abstraction", 12,
                        List.of("Declare abstract classes and abstract methods",
                                "Explain why an abstract class cannot be instantiated"),
                        """
                        Abstraction means exposing what something does while hiding how it does it. In Java, an abstract class is declared with the keyword abstract and may contain abstract methods: methods with a signature but no body. It is a partly finished blueprint that is meant to be extended.

                        You cannot create an object of an abstract class with new. A concrete subclass must implement every inherited abstract method, or be abstract itself. Abstract classes can still have fields, constructors and ordinary methods, so they are a good place to put the behaviour shared by a family of classes while forcing each member to supply its own specifics.
                        """,
                        List.of(ex("Vehicles share info(), define wheels()", """
                                abstract class Vehicle {
                                    String name;

                                    Vehicle(String name) {
                                        this.name = name;
                                    }

                                    abstract int wheels();

                                    void info() {
                                        System.out.println(name + " has " + wheels() + " wheels");
                                    }
                                }

                                class Car extends Vehicle {
                                    Car() {
                                        super("Car");
                                    }

                                    int wheels() {
                                        return 4;
                                    }
                                }

                                class Bike extends Vehicle {
                                    Bike() {
                                        super("Bike");
                                    }

                                    int wheels() {
                                        return 2;
                                    }
                                }

                                public class AbstractionDemo {
                                    public static void main(String[] args) {
                                        new Car().info();
                                        new Bike().info();
                                        // new Vehicle("x");   // would not compile: Vehicle is abstract
                                    }
                                }
                                """, """
                                Car has 4 wheels
                                Bike has 2 wheels
                                """)),
                        List.of("An abstract method has no body and ends with a semicolon, and it can exist only inside an abstract class or an interface."),
                        List.of("Trying to create an object of an abstract class.",
                                "Forgetting to implement one abstract method in a concrete subclass."),
                        List.of("Interfaces", "Inheritance")),

                lesson("Interfaces", 12,
                        List.of("Define and implement interfaces",
                                "Use default methods and implement several interfaces"),
                        """
                        An interface is a contract: it lists methods that a class promises to provide, without saying how. A class signs the contract with implements, and unlike with extends, a class can implement as many interfaces as it wants. That makes interfaces Java's tool for giving unrelated classes a common ability, such as being printable or comparable.

                        Methods in an interface are public and abstract by default, so the implementing class must declare them public. Since Java 8, an interface may contain default methods that have a body and are inherited by implementers, and static methods. An interface can also extend other interfaces, and a variable of an interface type can hold any object whose class implements it.
                        """,
                        List.of(ex("One class, two interfaces", """
                                interface Payable {
                                    double amount();

                                    default String label() {
                                        return "Pay " + amount();
                                    }
                                }

                                interface Printable {
                                    void print();
                                }

                                class Invoice implements Payable, Printable {

                                    public double amount() {
                                        return 1200.5;
                                    }

                                    public void print() {
                                        System.out.println("Invoice: " + label());
                                    }
                                }

                                public class InterfaceDemo {
                                    public static void main(String[] args) {
                                        Printable p = new Invoice();
                                        p.print();
                                    }
                                }
                                """, """
                                Invoice: Pay 1200.5
                                """)),
                        List.of("Choose an abstract class when subclasses share state and code, and an interface when you only need to share a capability."),
                        List.of("Implementing an interface method without the public keyword, which is a compile error.",
                                "Forgetting to implement one of the interface's methods."),
                        List.of("Abstraction", "Polymorphism")),

                lesson("Access modifiers", 10,
                        List.of("Describe private, default, protected and public access",
                                "Choose the narrowest access that works"),
                        """
                        Access modifiers decide who can see a class member. private means only code inside the same class. With no modifier (package-private, also called default), the member is visible to classes in the same package. protected adds access for subclasses, even in other packages. public means everyone.

                        A good habit is to start with the narrowest access that works, usually private for fields, and widen it only when needed. This keeps the public surface of a class small, which makes the class easier to understand and change without breaking other code. A top-level class can be only public or package-private.
                        """,
                        List.of(ex("Four levels of access", """
                                class Box {
                                    private int secret = 1;
                                    int packageLevel = 2;
                                    protected int family = 3;
                                    public int open = 4;

                                    int reveal() {
                                        return secret;
                                    }
                                }

                                public class AccessDemo {
                                    public static void main(String[] args) {
                                        Box b = new Box();
                                        System.out.println(b.packageLevel + b.family + b.open);
                                        System.out.println(b.reveal());
                                        // System.out.println(b.secret);   // would not compile: secret is private
                                    }
                                }
                                """, """
                                9
                                1
                                """)),
                        List.of("Both classes live in the same package, so packageLevel and family are reachable from AccessDemo; secret is not."),
                        List.of("Leaving everything public \"just in case\".",
                                "Assuming protected means the same as private-to-subclasses. It also allows access from the same package."),
                        List.of("Encapsulation")),

                lesson("Static members", 10,
                        List.of("Explain what static fields and methods belong to",
                                "Use static members for shared data and utilities"),
                        """
                        A static member belongs to the class itself, not to any one object. A static field has exactly one copy shared by all instances, which is handy for counters and constants. A static method can be called through the class name without creating an object, like Math.max(3, 7), and it can use only other static members directly because it has no this.

                        Instance members, in contrast, belong to each object separately. Utility methods that do not depend on object state are good candidates for static. Overusing static makes code harder to test and to extend, so reach for it deliberately.
                        """,
                        List.of(ex("Counting created objects", """
                                class Counter {
                                    static int created = 0;
                                    int id;

                                    Counter() {
                                        created++;
                                        id = created;
                                    }

                                    static void report() {
                                        System.out.println("Created: " + created);
                                    }
                                }

                                public class StaticDemo {
                                    public static void main(String[] args) {
                                        new Counter();
                                        new Counter();
                                        Counter c = new Counter();
                                        System.out.println(c.id);
                                        Counter.report();
                                    }
                                }
                                """, """
                                3
                                Created: 3
                                """)),
                        List.of("The main method is static, which is why it can start a program before any object exists."),
                        List.of("Trying to use an instance field from a static method without an object.",
                                "Making a field static just to avoid passing it around, which shares one value across all objects by accident."),
                        List.of("final keyword", "Classes and objects")),

                lesson("final keyword", 10,
                        List.of("Use final on variables, methods and classes",
                                "Understand that a final reference can still point to a changing object"),
                        """
                        The keyword final means "this cannot change", and it has a different effect in each place. A final variable can be assigned only once, which makes it a constant when combined with static (static final). A final method cannot be overridden by subclasses. A final class cannot be extended at all; String is a well-known example.

                        With a final reference variable, the reference cannot be pointed at another object, but the object it refers to can still be modified. A final array can have its elements changed, for instance. If you need a truly unchangeable object, design the class so that its fields are final and it offers no way to modify them.
                        """,
                        List.of(ex("Constants and final arrays", """
                                class Config {
                                    static final double TAX_RATE = 0.18;
                                }

                                public class FinalDemo {
                                    public static void main(String[] args) {
                                        final int limit = 10;
                                        final int[] nums = {1, 2, 3};
                                        nums[0] = 99;

                                        System.out.println(limit);
                                        System.out.println(nums[0]);
                                        System.out.println(Config.TAX_RATE);
                                        // limit = 11;          // would not compile
                                        // nums = new int[2];   // would not compile
                                    }
                                }
                                """, """
                                10
                                99
                                0.18
                                """)),
                        List.of("Name constants in UPPER_CASE with underscores, for example MAX_RETRY_COUNT."),
                        List.of("Believing a final array or list can never change. Only the reference is fixed, not the contents."),
                        List.of("Static members", "Method overriding"))
        );

        QuizSeed quiz = new QuizSeed("Object-Oriented Programming Quiz",
                "Check your grasp of classes, inheritance, polymorphism, abstraction and more.",
                List.of(
                        q("Animal a = new Dog(); a.sound(); Dog overrides sound(). Which version runs?", 1,
                                "The method is chosen by the object's actual type at run time (dynamic dispatch), so Dog's version runs.",
                                "Animal's version", "Dog's version", "Neither", "It does not compile"),
                        q("Which principle is about hiding fields behind private access and offering controlled methods?", 2,
                                "That is encapsulation: bundling data with the code that guards it.",
                                "Inheritance", "Polymorphism", "Encapsulation", "Overloading"),
                        q("Which keyword makes one class inherit from another?", 1,
                                "A subclass declares its parent with extends. implements is used for interfaces.",
                                "implements", "extends", "inherits", "super"),
                        q("Which statement about an abstract class is true?", 1,
                                "An abstract class cannot be instantiated directly, but it can have constructors, fields and normal methods.",
                                "It can be instantiated with new", "It cannot be instantiated directly",
                                "It cannot have constructors", "It must not contain any method with a body"),
                        q("What happens if a subclass tries to override a final method?", 1,
                                "A final method cannot be overridden, so the compiler reports an error.",
                                "It overrides silently", "A compile-time error", "A runtime exception", "The final keyword is ignored"),
                        q("A static field belongs to...", 1,
                                "A static field belongs to the class and is shared by all objects of that class.",
                                "Each object separately", "The class, shared by all objects", "Only the main method", "The JVM only")
                ));

        List<PracticeSeed> practiceSet = List.of(

                practice("Rectangle class", "EASY",
                        "Create a Rectangle class with width and height fields, a constructor, and methods area() and perimeter(). In main, create a 5 x 3 rectangle and print Area: and Perimeter: lines.",
                        """
                        public class Rectangle {
                            double width;
                            double height;

                            // TODO: add a constructor, area() and perimeter()

                            public static void main(String[] args) {
                                // TODO: create a 5 x 3 rectangle and print its area and perimeter
                            }
                        }
                        """,
                        "area is width * height and perimeter is 2 * (width + height). Use this.width = width in the constructor.",
                        """
                        public class Rectangle {
                            double width;
                            double height;

                            Rectangle(double width, double height) {
                                this.width = width;
                                this.height = height;
                            }

                            double area() {
                                return width * height;
                            }

                            double perimeter() {
                                return 2 * (width + height);
                            }

                            public static void main(String[] args) {
                                Rectangle r = new Rectangle(5, 3);
                                System.out.println("Area: " + r.area());
                                System.out.println("Perimeter: " + r.perimeter());
                            }
                        }
                        """,
                        """
                        Area: 15.0
                        Perimeter: 16.0
                        """),

                practice("Safe bank account", "MEDIUM",
                        "Write a BankAccount class with a private balance. Start with 1000, deposit 500, withdraw 200, then try to withdraw 5000. A withdrawal larger than the balance must print Insufficient funds and change nothing. Finish by printing Balance: <amount>.",
                        """
                        class BankAccount {
                            private double balance;

                            // TODO: constructor, deposit, withdraw and getBalance
                        }

                        public class BankDemo {
                            public static void main(String[] args) {
                                // TODO: run the scenario described above
                            }
                        }
                        """,
                        "Keep balance private and check amount > balance inside withdraw before subtracting.",
                        """
                        class BankAccount {
                            private double balance;

                            BankAccount(double opening) {
                                balance = opening;
                            }

                            void deposit(double amount) {
                                if (amount > 0) {
                                    balance += amount;
                                }
                            }

                            void withdraw(double amount) {
                                if (amount > balance) {
                                    System.out.println("Insufficient funds");
                                } else if (amount > 0) {
                                    balance -= amount;
                                }
                            }

                            double getBalance() {
                                return balance;
                            }
                        }

                        public class BankDemo {
                            public static void main(String[] args) {
                                BankAccount account = new BankAccount(1000);
                                account.deposit(500);
                                account.withdraw(200);
                                account.withdraw(5000);
                                System.out.println("Balance: " + account.getBalance());
                            }
                        }
                        """,
                        """
                        Insufficient funds
                        Balance: 1300.0
                        """),

                practice("Shapes with an abstract class", "MEDIUM",
                        "Make an abstract class Shape with an abstract method double area(). Create Square (side 4) and Rect (width 2, height 5) as subclasses. Put both in a Shape array and print each area on its own line using one loop.",
                        """
                        abstract class Shape {
                            abstract double area();
                        }

                        // TODO: write the Square and Rect classes

                        public class ShapesDemo {
                            public static void main(String[] args) {
                                // TODO: create the array and print each area in a loop
                            }
                        }
                        """,
                        "Each subclass stores its own dimensions and overrides area(). The loop only needs the Shape type.",
                        """
                        abstract class Shape {
                            abstract double area();
                        }

                        class Square extends Shape {
                            double side;

                            Square(double side) {
                                this.side = side;
                            }

                            double area() {
                                return side * side;
                            }
                        }

                        class Rect extends Shape {
                            double width, height;

                            Rect(double width, double height) {
                                this.width = width;
                                this.height = height;
                            }

                            double area() {
                                return width * height;
                            }
                        }

                        public class ShapesDemo {
                            public static void main(String[] args) {
                                Shape[] shapes = {new Square(4), new Rect(2, 5)};
                                for (Shape s : shapes) {
                                    System.out.println(s.area());
                                }
                            }
                        }
                        """,
                        """
                        16.0
                        10.0
                        """)
        );

        return new ModuleSeed("Object-Oriented Programming",
                "Model real things with classes and objects, and use encapsulation, inheritance, polymorphism, abstraction and interfaces.",
                lessons, quiz, practiceSet);
    }

    // ====================================================================================
    //  MODULE 4: EXCEPTION HANDLING
    // ====================================================================================

    static ModuleSeed module4() {
        List<LessonSeed> lessons = List.of(

                lesson("Introduction to exceptions", 10,
                        List.of("Explain what an exception is and why Java has them",
                                "Read a stack trace and describe the exception hierarchy"),
                        """
                        An exception is an event that disrupts the normal flow of a program, such as dividing by zero, reading past the end of an array or opening a file that does not exist. When this happens, Java creates an exception object that describes the problem and throws it. If nothing catches it, the program stops and prints a stack trace.

                        All exceptions descend from the class Throwable. It has two branches. Error represents serious problems in the JVM itself, such as OutOfMemoryError; programs normally do not try to recover from them. Exception represents problems your program can reasonably handle. Under Exception, RuntimeException and its children are the unchecked exceptions (usually programming mistakes), while all the others are checked exceptions that the compiler forces you to deal with.

                        A stack trace reads from the top: the first line names the exception type and message, and each following line is a method call, with the most recent call first. The first line that mentions your own class usually shows where to look.
                        """,
                        List.of(ex("An uncaught exception stops the program", """
                                public class ExceptionIntro {
                                    public static void main(String[] args) {
                                        System.out.println("Start");
                                        int[] data = {1, 2, 3};
                                        System.out.println(data[5]);
                                        System.out.println("Never printed");
                                    }
                                }
                                """, """
                                Start
                                Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 3
                                    at ExceptionIntro.main(ExceptionIntro.java:5)
                                """)),
                        List.of("The stack trace is written to the error stream, which IDEs usually show in red. The line number in the last line matches the line of data[5].",
                                "Exception handling separates the normal path of your code from the error-handling path."),
                        List.of("Ignoring the stack trace. It tells you the exact line and the chain of calls that led to the failure.",
                                "Catching Throwable or Error to hide problems instead of fixing the cause."),
                        List.of("try and catch", "Checked exceptions", "Unchecked exceptions")),

                lesson("try and catch", 12,
                        List.of("Handle an exception with try and catch",
                                "Use several catch blocks and multi-catch"),
                        """
                        Code that might fail goes in a try block. If an exception is thrown inside it, execution jumps immediately to the first catch block whose type matches the exception; the rest of the try block is skipped. If nothing is thrown, the catch blocks are skipped. After the try statement finishes, the program continues normally.

                        The catch parameter (usually e) is the exception object. e.getMessage() returns the description, and e.printStackTrace() prints the full trace. You can write several catch blocks for different types, and Java picks the first that matches, so list the more specific types before the more general ones. When two types need identical handling, a multi-catch such as catch (A | B e) avoids repeating code.
                        """,
                        List.of(ex("Catching specific exceptions", """
                                public class TryCatch {
                                    public static void main(String[] args) {
                                        try {
                                            int result = 10 / 0;
                                            System.out.println(result);
                                        } catch (ArithmeticException e) {
                                            System.out.println("Cannot divide by zero: " + e.getMessage());
                                        }

                                        String[] inputs = {"42", "abc", null};
                                        for (String s : inputs) {
                                            try {
                                                int n = Integer.parseInt(s);
                                                System.out.println("Parsed " + n);
                                            } catch (NumberFormatException e) {
                                                System.out.println("Not a number: " + s);
                                            }
                                        }
                                        System.out.println("Done");
                                    }
                                }
                                """, """
                                Cannot divide by zero: / by zero
                                Parsed 42
                                Not a number: abc
                                Not a number: null
                                Done
                                """),
                                ex("Multi-catch", """
                                public class MultiCatch {

                                    static void run(String text, int divisor) {
                                        try {
                                            int n = Integer.parseInt(text);
                                            System.out.println(n / divisor);
                                        } catch (NumberFormatException | ArithmeticException e) {
                                            System.out.println("Problem: " + e.getClass().getSimpleName());
                                        }
                                    }

                                    public static void main(String[] args) {
                                        run("20", 4);
                                        run("abc", 4);
                                        run("20", 0);
                                    }
                                }
                                """, """
                                5
                                Problem: NumberFormatException
                                Problem: ArithmeticException
                                """)),
                        List.of("Integer.parseInt(null) also throws NumberFormatException, which is why the null input above is caught.",
                                "Keep try blocks small: wrap only the statements that can actually fail."),
                        List.of("Writing catch (Exception e) with an empty body, which silently hides every problem.",
                                "Placing a general catch before a specific one, for example Exception before IOException. That is a compile error because the later block could never run."),
                        List.of("finally", "throw")),

                lesson("finally", 12,
                        List.of("Run clean-up code with finally",
                                "Close resources automatically with try-with-resources"),
                        """
                        A finally block runs after the try and catch blocks whether or not an exception happened, and even when the try block exits through return. It is the place for clean-up such as closing files or releasing connections. The only things that stop it are a JVM shutdown or an endless loop.

                        Writing finally blocks by hand for every resource is repetitive and easy to get wrong. Since Java 7, try-with-resources does it for you: declare resources that implement AutoCloseable in parentheses after try, and Java closes them automatically when the block ends, in reverse order of creation, even if an exception occurs. Prefer it whenever you work with files, database connections or streams.
                        """,
                        List.of(ex("finally always runs", """
                                public class FinallyDemo {

                                    static String test(int n) {
                                        try {
                                            System.out.println("try " + n);
                                            if (n == 0) {
                                                throw new IllegalArgumentException("zero");
                                            }
                                            return "ok";
                                        } catch (IllegalArgumentException e) {
                                            System.out.println("catch");
                                            return "failed";
                                        } finally {
                                            System.out.println("finally " + n);
                                        }
                                    }

                                    public static void main(String[] args) {
                                        System.out.println(test(1));
                                        System.out.println(test(0));
                                    }
                                }
                                """, """
                                try 1
                                finally 1
                                ok
                                try 0
                                catch
                                finally 0
                                failed
                                """),
                                ex("try-with-resources", """
                                class Resource implements AutoCloseable {
                                    private final String name;

                                    Resource(String name) {
                                        this.name = name;
                                        System.out.println("open " + name);
                                    }

                                    void use() {
                                        System.out.println("using " + name);
                                    }

                                    @Override
                                    public void close() {
                                        System.out.println("close " + name);
                                    }
                                }

                                public class TryWithResources {
                                    public static void main(String[] args) {
                                        try (Resource r1 = new Resource("A"); Resource r2 = new Resource("B")) {
                                            r1.use();
                                            r2.use();
                                        }
                                        System.out.println("after");
                                    }
                                }
                                """, """
                                open A
                                open B
                                using A
                                using B
                                close B
                                close A
                                after
                                """)),
                        List.of("In the first example the finally line prints before the returned value reaches main, because finally runs as the method exits."),
                        List.of("Putting a return statement inside finally, which can silently discard an exception.",
                                "Forgetting to close resources, which leaks files and connections."),
                        List.of("try and catch", "File handling")),

                lesson("throw", 10,
                        List.of("Create and throw an exception yourself",
                                "Choose an appropriate exception type and message"),
                        """
                        The throw statement raises an exception on purpose. You build an exception object with new and throw it: throw new IllegalArgumentException("message"). Execution of the current method stops right there, and the exception travels up the call stack until some catch block handles it.

                        Use throw to reject invalid input or an impossible state as early as possible, rather than letting wrong data spread through the program. Pick the most fitting built-in type: IllegalArgumentException for a bad argument, IllegalStateException when an operation is not allowed in the current state, and so on. Write a message that says what was wrong and, where helpful, what the bad value was.
                        """,
                        List.of(ex("Validating an argument", """
                                public class ThrowDemo {

                                    static void setAge(int age) {
                                        if (age < 0 || age > 150) {
                                            throw new IllegalArgumentException("Age must be between 0 and 150, got " + age);
                                        }
                                        System.out.println("Age set to " + age);
                                    }

                                    public static void main(String[] args) {
                                        setAge(25);
                                        try {
                                            setAge(-5);
                                        } catch (IllegalArgumentException e) {
                                            System.out.println("Rejected: " + e.getMessage());
                                        }
                                    }
                                }
                                """, """
                                Age set to 25
                                Rejected: Age must be between 0 and 150, got -5
                                """)),
                        List.of("throw (a statement that raises one exception) is different from throws (a clause that declares what a method may raise)."),
                        List.of("Throwing a generic Exception or RuntimeException with no message.",
                                "Writing code after a throw statement in the same block. It can never run, so the compiler reports an error."),
                        List.of("throws", "Custom exceptions")),

                lesson("throws", 10,
                        List.of("Declare checked exceptions with throws",
                                "Decide between handling an exception and passing it on"),
                        """
                        If a method can throw a checked exception and does not handle it, it must declare that fact in its signature with the throws keyword: void readConfig() throws IOException. The compiler then forces every caller to either catch the exception or declare it too, so the possibility of failure cannot be ignored.

                        Deciding where to handle an exception is a design question. Handle it where you can do something useful about it, such as asking for a new file name or showing a message. If the current method has no sensible way to recover, declare it with throws and let a higher level decide. Unchecked exceptions do not need to be declared.
                        """,
                        List.of(ex("Declaring and catching a checked exception", """
                                import java.io.IOException;

                                public class ThrowsDemo {

                                    static String readConfig(String name) throws IOException {
                                        if (name.isEmpty()) {
                                            throw new IOException("Config name is empty");
                                        }
                                        return "config:" + name;
                                    }

                                    public static void main(String[] args) {
                                        try {
                                            System.out.println(readConfig("app"));
                                            System.out.println(readConfig(""));
                                        } catch (IOException e) {
                                            System.out.println("Could not read: " + e.getMessage());
                                        }
                                    }
                                }
                                """, """
                                config:app
                                Could not read: Config name is empty
                                """)),
                        List.of("A method may declare several exceptions, separated by commas: throws IOException, SQLException."),
                        List.of("Declaring throws Exception on every method to silence the compiler, which hides what can actually go wrong.",
                                "Calling a method that declares a checked exception without catching or declaring it."),
                        List.of("Checked exceptions", "throw")),

                lesson("Custom exceptions", 12,
                        List.of("Create your own exception class",
                                "Carry extra information inside an exception"),
                        """
                        When the built-in exceptions do not describe a problem in your domain well, create your own by extending Exception (a checked exception) or RuntimeException (an unchecked one). Give the class constructors that pass a message to super(...), and add fields if callers need more detail.

                        Custom exceptions make code easier to read and let callers catch exactly the failure they care about, such as InsufficientFundsException, instead of a vague Exception. Name them with the suffix Exception, and extend RuntimeException unless you genuinely want to force every caller to handle the problem.
                        """,
                        List.of(ex("InsufficientFundsException", """
                                class InsufficientFundsException extends Exception {
                                    private final double shortfall;

                                    InsufficientFundsException(String message, double shortfall) {
                                        super(message);
                                        this.shortfall = shortfall;
                                    }

                                    double getShortfall() {
                                        return shortfall;
                                    }
                                }

                                class Wallet {
                                    private double balance;

                                    Wallet(double balance) {
                                        this.balance = balance;
                                    }

                                    void pay(double amount) throws InsufficientFundsException {
                                        if (amount > balance) {
                                            throw new InsufficientFundsException("Not enough money", amount - balance);
                                        }
                                        balance -= amount;
                                    }
                                }

                                public class CustomExceptionDemo {
                                    public static void main(String[] args) {
                                        Wallet w = new Wallet(100);
                                        try {
                                            w.pay(60);
                                            w.pay(80);
                                        } catch (InsufficientFundsException e) {
                                            System.out.println(e.getMessage() + ", short by " + e.getShortfall());
                                        }
                                    }
                                }
                                """, """
                                Not enough money, short by 40.0
                                """)),
                        List.of("The first payment leaves 40 in the wallet, so the second payment of 80 is short by 40."),
                        List.of("Creating a custom exception for every tiny case. Reuse built-in types when they already fit.",
                                "Forgetting to call super(message), which loses the message."),
                        List.of("Checked exceptions", "Unchecked exceptions")),

                lesson("Checked exceptions", 10,
                        List.of("Identify checked exceptions",
                                "Handle them with catch or declare them with throws"),
                        """
                        Checked exceptions are the ones the compiler checks. They are subclasses of Exception that are not RuntimeException, such as IOException, FileNotFoundException and SQLException. They describe problems outside your program's control, like missing files or network failures, which a careful program should expect.

                        If code can throw a checked exception, the compiler insists that you either catch it or declare it with throws; otherwise the program does not compile. When catching several related types, put the subclass first: FileNotFoundException is a kind of IOException, so it must come before IOException.
                        """,
                        List.of(ex("A file that does not exist", """
                                import java.io.FileNotFoundException;
                                import java.io.FileReader;
                                import java.io.IOException;

                                public class CheckedDemo {
                                    public static void main(String[] args) {
                                        try (FileReader reader = new FileReader("no-such-file.txt")) {
                                            System.out.println(reader.read());
                                        } catch (FileNotFoundException e) {
                                            System.out.println("File not found");
                                        } catch (IOException e) {
                                            System.out.println("I/O problem");
                                        }
                                    }
                                }
                                """, """
                                File not found
                                """)),
                        List.of("This output assumes that no-such-file.txt does not exist in the folder where you run the program."),
                        List.of("Catching IOException before FileNotFoundException, which is a compile error.",
                                "Catching a checked exception and doing nothing, which hides real failures."),
                        List.of("Unchecked exceptions", "throws", "File handling")),

                lesson("Unchecked exceptions", 10,
                        List.of("Recognise common unchecked exceptions",
                                "Decide when to catch them and when to fix the code")
                        ,
                        """
                        Unchecked exceptions are RuntimeException and its subclasses. The compiler does not force you to catch or declare them, because they usually signal a bug that should be fixed rather than handled: NullPointerException (using a null reference), ArrayIndexOutOfBoundsException and IndexOutOfBoundsException (bad index), ClassCastException (a wrong cast), NumberFormatException (text that is not a number) and IllegalArgumentException (a bad argument).

                        The best defence is to prevent them: check for null, check bounds, and validate input before using it. Catch an unchecked exception only when you can recover, for example when parsing user input that might not be a number, or at the outer edge of an application to log the failure and show a friendly message.
                        """,
                        List.of(ex("Common unchecked exceptions", """
                                import java.util.ArrayList;
                                import java.util.List;

                                public class UncheckedDemo {
                                    public static void main(String[] args) {
                                        String text = null;
                                        try {
                                            System.out.println(text.length());
                                        } catch (NullPointerException e) {
                                            System.out.println("NullPointerException caught");
                                        }

                                        Object o = "hello";
                                        try {
                                            Integer i = (Integer) o;
                                        } catch (ClassCastException e) {
                                            System.out.println("ClassCastException caught");
                                        }

                                        List<Integer> list = new ArrayList<>();
                                        try {
                                            list.get(0);
                                        } catch (IndexOutOfBoundsException e) {
                                            System.out.println("IndexOutOfBoundsException caught");
                                        }

                                        try {
                                            Integer.parseInt("12x");
                                        } catch (RuntimeException e) {
                                            System.out.println("Caught: " + e.getClass().getSimpleName());
                                        }
                                    }
                                }
                                """, """
                                NullPointerException caught
                                ClassCastException caught
                                IndexOutOfBoundsException caught
                                Caught: NumberFormatException
                                """)),
                        List.of("Catching the parent type RuntimeException catches every unchecked exception below it, including NumberFormatException."),
                        List.of("Using try and catch to patch over a bug (such as a null you should have checked) instead of fixing the cause.",
                                "Assuming unchecked exceptions are less serious. They crash the program just the same when uncaught."),
                        List.of("Checked exceptions", "Introduction to exceptions"))
        );

        QuizSeed quiz = new QuizSeed("Exception Handling Quiz",
                "Test your knowledge of try, catch, finally, throw, throws and custom exceptions.",
                List.of(
                        q("Which block runs whether or not an exception occurred?", 2,
                                "A finally block runs after try and catch in both cases, and is used for clean-up.",
                                "try", "catch", "finally", "throws"),
                        q("Which of these is a checked exception?", 1,
                                "IOException is checked, so the compiler forces you to catch or declare it. The others are unchecked.",
                                "NullPointerException", "IOException", "ArithmeticException", "IllegalArgumentException"),
                        q("What is the difference between throw and throws?", 1,
                                "throw is a statement that raises an exception object; throws is a clause in a method signature that declares which exceptions the method may pass on.",
                                "They are identical", "throw raises an exception; throws declares possible exceptions",
                                "throws raises an exception; throw declares possible exceptions", "Both are only used in catch blocks"),
                        q("Which catch order causes a compile error?", 3,
                                "A general catch (Exception) before a specific one (IOException) makes the specific block unreachable, so it does not compile.",
                                "catch (IOException) then catch (Exception)", "catch (ArithmeticException) then catch (RuntimeException)",
                                "catch (FileNotFoundException) then catch (IOException)", "catch (Exception) then catch (IOException)"),
                        q("To create your own checked exception, which class should it extend?", 1,
                                "Extending Exception gives a checked exception. Extending RuntimeException gives an unchecked one.",
                                "RuntimeException", "Exception", "Error", "Throwable only"),
                        q("What must a class implement to be used in a try-with-resources statement?", 2,
                                "Resources must implement AutoCloseable (Closeable extends it), so Java can call close() automatically.",
                                "Comparable", "Serializable", "AutoCloseable", "Runnable")
                ));

        List<PracticeSeed> practiceSet = List.of(

                practice("Safe division", "EASY",
                        "Write a method divide(int a, int b) that returns a / b. In main, print Result: and the answer for divide(10, 2). Then call divide(7, 0) inside a try block and print Error: cannot divide by zero when the exception is caught.",
                        """
                        public class SafeDivision {

                            static int divide(int a, int b) {
                                return a / b;
                            }

                            public static void main(String[] args) {
                                // TODO: call divide(10, 2), then divide(7, 0) inside try and catch
                            }
                        }
                        """,
                        "Integer division by zero throws ArithmeticException. Put both calls in one try block.",
                        """
                        public class SafeDivision {

                            static int divide(int a, int b) {
                                return a / b;
                            }

                            public static void main(String[] args) {
                                try {
                                    System.out.println("Result: " + divide(10, 2));
                                    System.out.println("Result: " + divide(7, 0));
                                } catch (ArithmeticException e) {
                                    System.out.println("Error: cannot divide by zero");
                                }
                            }
                        }
                        """,
                        """
                        Result: 5
                        Error: cannot divide by zero
                        """),

                practice("Validate an age with a custom exception", "MEDIUM",
                        "Create a checked exception InvalidAgeException. Write a method check(int age) that throws it with the message Age <age> is not allowed when age is below 0 or above 120, and otherwise prints Valid age: <age>. Test it with 20 and 200, catching the exception and printing Invalid: followed by its message.",
                        """
                        // TODO: write the InvalidAgeException class

                        public class AgeValidator {

                            // TODO: write check(int age)

                            public static void main(String[] args) {
                                // TODO: test with 20 and 200
                            }
                        }
                        """,
                        "Extend Exception, pass the message to super(message), and declare throws InvalidAgeException on check.",
                        """
                        class InvalidAgeException extends Exception {
                            InvalidAgeException(String message) {
                                super(message);
                            }
                        }

                        public class AgeValidator {

                            static void check(int age) throws InvalidAgeException {
                                if (age < 0 || age > 120) {
                                    throw new InvalidAgeException("Age " + age + " is not allowed");
                                }
                                System.out.println("Valid age: " + age);
                            }

                            public static void main(String[] args) {
                                int[] ages = {20, 200};
                                for (int age : ages) {
                                    try {
                                        check(age);
                                    } catch (InvalidAgeException e) {
                                        System.out.println("Invalid: " + e.getMessage());
                                    }
                                }
                            }
                        }
                        """,
                        """
                        Valid age: 20
                        Invalid: Age 200 is not allowed
                        """),

                practice("Sum the valid numbers", "MEDIUM",
                        "Given the strings {\"10\", \"x\", \"30\"}, add up the ones that are valid integers and count the invalid ones. Use try, catch and finally inside the loop, where the finally block counts how many items were processed. Print Sum:, Invalid: and Processed: lines.",
                        """
                        public class SumValid {
                            public static void main(String[] args) {
                                String[] values = {"10", "x", "30"};
                                int sum = 0;
                                int invalid = 0;
                                int processed = 0;
                                // TODO: loop over values with try, catch and finally

                                System.out.println("Sum: " + sum);
                                System.out.println("Invalid: " + invalid);
                                System.out.println("Processed: " + processed);
                            }
                        }
                        """,
                        "Integer.parseInt throws NumberFormatException for text like x. Increase processed in the finally block so it runs for every item.",
                        """
                        public class SumValid {
                            public static void main(String[] args) {
                                String[] values = {"10", "x", "30"};
                                int sum = 0;
                                int invalid = 0;
                                int processed = 0;

                                for (String value : values) {
                                    try {
                                        sum += Integer.parseInt(value);
                                    } catch (NumberFormatException e) {
                                        invalid++;
                                    } finally {
                                        processed++;
                                    }
                                }

                                System.out.println("Sum: " + sum);
                                System.out.println("Invalid: " + invalid);
                                System.out.println("Processed: " + processed);
                            }
                        }
                        """,
                        """
                        Sum: 40
                        Invalid: 1
                        Processed: 3
                        """)
        );

        return new ModuleSeed("Exception Handling",
                "Handle errors gracefully with try, catch and finally, and design your own exceptions.",
                lessons, quiz, practiceSet);
    }

    // ====================================================================================
    //  MODULE 5: COLLECTIONS FRAMEWORK
    // ====================================================================================

    static ModuleSeed module5() {
        List<LessonSeed> lessons = List.of(

                lesson("Introduction to collections", 12,
                        List.of("Explain what the Collections Framework provides",
                                "Name the main interfaces and when to use each"),
                        """
                        Arrays have a fixed size, which makes them awkward when you do not know in advance how many items you will store. The Java Collections Framework, in the package java.util, provides ready-made, resizable containers with a consistent set of operations.

                        The main interfaces are List (an ordered sequence that allows duplicates and access by index), Set (no duplicates), Queue (items waiting to be processed, usually first-in-first-out) and Map (pairs of a key and a value). Map is not a subtype of Collection but is considered part of the framework. Each interface has several implementations, such as ArrayList, HashSet and HashMap, which differ in speed and ordering.

                        Collections use generics: List<String> says that the list holds only Strings, and the compiler rejects anything else. Collections hold objects, not primitives, so you write List<Integer> rather than List<int>; Java converts between int and Integer automatically (autoboxing).
                        """,
                        List.of(ex("Same three items, three different containers", """
                                import java.util.ArrayList;
                                import java.util.HashMap;
                                import java.util.HashSet;
                                import java.util.List;
                                import java.util.Map;
                                import java.util.Set;

                                public class CollectionsIntro {
                                    public static void main(String[] args) {
                                        List<String> list = new ArrayList<>();
                                        list.add("Java");
                                        list.add("Java");
                                        list.add("Spring");

                                        Set<String> set = new HashSet<>();
                                        set.add("Java");
                                        set.add("Java");
                                        set.add("Spring");

                                        Map<String, Integer> map = new HashMap<>();
                                        map.put("Java", 1);
                                        map.put("Spring", 2);

                                        System.out.println(list.size());
                                        System.out.println(set.size());
                                        System.out.println(map.get("Spring"));
                                    }
                                }
                                """, """
                                3
                                2
                                2
                                """)),
                        List.of("Program to the interface: declare variables as List, Set or Map, and choose the implementation only where you create the object.",
                                "The diamond <> lets the compiler infer the type arguments on the right-hand side."),
                        List.of("Trying to store primitives directly, as in List<int>.",
                                "Choosing a collection by habit instead of by what the data needs (order, duplicates, lookup by key)."),
                        List.of("List", "Set", "Map", "Generics")),

                lesson("List", 12,
                        List.of("Use the List interface for ordered data",
                                "Add, replace, remove and search elements"),
                        """
                        A List keeps its elements in the order you put them in, allows duplicates and lets you reach any element by its index starting at 0. Important methods are add(element), add(index, element), get(index), set(index, element), remove(index or element), indexOf, contains, size, isEmpty and clear.

                        List.of(...) creates a small unmodifiable list: reading is fine, but any attempt to change it throws an UnsupportedOperationException. To get an editable copy, pass it to a constructor: new ArrayList<>(List.of(...)).
                        """,
                        List.of(ex("Changing a list", """
                                import java.util.ArrayList;
                                import java.util.List;

                                public class ListDemo {
                                    public static void main(String[] args) {
                                        List<String> names = new ArrayList<>(List.of("Asha", "Ravi", "Meena"));
                                        names.add(1, "Kiran");
                                        names.set(0, "Anita");
                                        names.remove("Ravi");

                                        System.out.println(names);
                                        System.out.println(names.get(2));
                                        System.out.println(names.indexOf("Kiran"));
                                        System.out.println(names.contains("Ravi"));

                                        List<Integer> fixed = List.of(1, 2, 3);
                                        try {
                                            fixed.add(4);
                                        } catch (UnsupportedOperationException e) {
                                            System.out.println("List.of is unmodifiable");
                                        }
                                    }
                                }
                                """, """
                                [Anita, Kiran, Meena]
                                Meena
                                1
                                false
                                List.of is unmodifiable
                                """)),
                        List.of("On a List<Integer>, remove(2) removes the element at index 2, while remove(Integer.valueOf(2)) removes the value 2."),
                        List.of("Confusing remove(int index) with remove(Object value) on a List<Integer>.",
                                "Calling get with an index equal to size(), which throws IndexOutOfBoundsException."),
                        List.of("ArrayList", "LinkedList")),

                lesson("ArrayList", 12,
                        List.of("Describe how ArrayList stores its data",
                                "Sort, search and filter an ArrayList"),
                        """
                        ArrayList is the most used List. Internally it keeps its elements in an array and replaces that array with a bigger one when it fills up. Reading or replacing an element by index is very fast. Adding at the end is fast on average. Inserting or removing in the middle is slower, because the elements after that point have to be shifted.

                        The helper class Collections adds useful operations such as sort, max, min, reverse and shuffle. removeIf deletes every element that matches a condition. The condition in the example is written as s -> s < 65, a lambda expression, which you will study in Module 6; read it as "for each s, test whether s is less than 65".
                        """,
                        List.of(ex("Sorting and filtering scores", """
                                import java.util.ArrayList;
                                import java.util.Collections;

                                public class ArrayListDemo {
                                    public static void main(String[] args) {
                                        ArrayList<Integer> scores = new ArrayList<>();
                                        scores.add(70);
                                        scores.add(95);
                                        scores.add(60);
                                        scores.add(82);

                                        Collections.sort(scores);
                                        System.out.println(scores);
                                        System.out.println(Collections.max(scores));

                                        scores.removeIf(s -> s < 65);

                                        int total = 0;
                                        for (int s : scores) {
                                            total += s;
                                        }
                                        System.out.println(scores + " total=" + total);
                                    }
                                }
                                """, """
                                [60, 70, 82, 95]
                                95
                                [70, 82, 95] total=247
                                """)),
                        List.of("If you know roughly how many elements you will add, new ArrayList<>(1000) avoids repeated resizing."),
                        List.of("Removing elements from an ArrayList inside a for-each loop, which throws ConcurrentModificationException.",
                                "Using ArrayList for a queue where you constantly remove from the front."),
                        List.of("LinkedList", "Iterators")),

                lesson("LinkedList", 10,
                        List.of("Describe how LinkedList differs from ArrayList",
                                "Use the deque methods addFirst, addLast and removeFirst"),
                        """
                        A LinkedList stores each element in a small node that points to the previous and next node. Adding or removing at either end is quick because no shifting is needed. Reaching an element in the middle is slow, because Java has to walk node by node from one end.

                        LinkedList implements both List and Deque (a double-ended queue), so besides get and add it offers addFirst, addLast, getFirst, getLast, removeFirst and removeLast. In practice ArrayList is the better default for lists, and LinkedList is worth considering mainly when you work at the ends of the sequence.
                        """,
                        List.of(ex("Working at both ends", """
                                import java.util.LinkedList;

                                public class LinkedListDemo {
                                    public static void main(String[] args) {
                                        LinkedList<String> tasks = new LinkedList<>();
                                        tasks.add("write");
                                        tasks.addFirst("plan");
                                        tasks.addLast("review");

                                        System.out.println(tasks);
                                        System.out.println(tasks.getFirst());

                                        tasks.removeFirst();
                                        System.out.println(tasks);
                                        System.out.println(tasks.get(1));
                                    }
                                }
                                """, """
                                [plan, write, review]
                                plan
                                [write, review]
                                review
                                """)),
                        List.of("Both ArrayList and LinkedList keep insertion order and allow duplicates; they differ in performance, not in behaviour."),
                        List.of("Choosing LinkedList because it sounds faster, then calling get(i) in a loop, which becomes very slow on big lists."),
                        List.of("ArrayList", "Queue")),

                lesson("Set", 10,
                        List.of("Explain that a Set stores unique elements",
                                "Compare HashSet, LinkedHashSet and TreeSet"),
                        """
                        A Set is a collection with no duplicates. Adding an element that is already present does nothing, and the add method tells you the outcome by returning false. A Set has no index, so you cannot ask for the element at position 3.

                        There are three common implementations. HashSet is the fastest and keeps no particular order. LinkedHashSet remembers the order in which you inserted elements. TreeSet keeps elements sorted, either in their natural order or by a Comparator. Choose by the ordering you need.
                        """,
                        List.of(ex("Insertion order versus sorted order", """
                                import java.util.LinkedHashSet;
                                import java.util.Set;
                                import java.util.TreeSet;

                                public class SetDemo {
                                    public static void main(String[] args) {
                                        Set<String> linked = new LinkedHashSet<>();
                                        System.out.println(linked.add("pear"));
                                        linked.add("apple");
                                        linked.add("banana");
                                        System.out.println(linked.add("apple"));
                                        System.out.println(linked);

                                        Set<String> sorted = new TreeSet<>(linked);
                                        System.out.println(sorted);
                                    }
                                }
                                """, """
                                true
                                false
                                [pear, apple, banana]
                                [apple, banana, pear]
                                """)),
                        List.of("Never rely on the printing order of a plain HashSet: it is unspecified and can differ between Java versions."),
                        List.of("Expecting a Set to keep the order you added things in, unless you chose LinkedHashSet.",
                                "Trying to read a Set element by index."),
                        List.of("HashSet", "Comparable")),

                lesson("HashSet", 12,
                        List.of("Use HashSet to remove duplicates and test membership quickly",
                                "Explain why equals and hashCode matter for your own classes"),
                        """
                        HashSet is built on a hash table, so add, remove and contains take roughly the same short time no matter how many elements it holds. That makes it the tool of choice for removing duplicates from a list, for fast membership tests, and for set operations: addAll gives the union, retainAll the intersection and removeAll the difference.

                        For this to work with your own classes, they must override equals and hashCode consistently: objects that are equal must return the same hash code. If you skip this, two objects representing the same thing are treated as different and duplicates slip in. String, Integer and the other standard classes already do this correctly.
                        """,
                        List.of(ex("Duplicates, union and intersection", """
                                import java.util.HashSet;
                                import java.util.List;
                                import java.util.Set;
                                import java.util.TreeSet;

                                public class HashSetDemo {
                                    public static void main(String[] args) {
                                        List<Integer> numbers = List.of(4, 2, 4, 7, 2, 9);
                                        Set<Integer> unique = new HashSet<>(numbers);
                                        System.out.println(unique.size());
                                        System.out.println(unique.contains(7));

                                        Set<Integer> a = new HashSet<>(List.of(1, 2, 3, 4));
                                        Set<Integer> b = new HashSet<>(List.of(3, 4, 5));

                                        Set<Integer> common = new HashSet<>(a);
                                        common.retainAll(b);

                                        Set<Integer> all = new HashSet<>(a);
                                        all.addAll(b);

                                        System.out.println(new TreeSet<>(common));
                                        System.out.println(all.size());
                                    }
                                }
                                """, """
                                4
                                true
                                [3, 4]
                                5
                                """),
                                ex("equals and hashCode for your own class", """
                                import java.util.HashSet;
                                import java.util.Objects;
                                import java.util.Set;

                                class Student {
                                    final int id;
                                    final String name;

                                    Student(int id, String name) {
                                        this.id = id;
                                        this.name = name;
                                    }

                                    @Override
                                    public boolean equals(Object o) {
                                        if (this == o) {
                                            return true;
                                        }
                                        if (!(o instanceof Student)) {
                                            return false;
                                        }
                                        Student other = (Student) o;
                                        return id == other.id;
                                    }

                                    @Override
                                    public int hashCode() {
                                        return Objects.hash(id);
                                    }
                                }

                                public class EqualsHashCodeDemo {
                                    public static void main(String[] args) {
                                        Set<Student> students = new HashSet<>();
                                        students.add(new Student(1, "Asha"));
                                        students.add(new Student(1, "Asha Again"));
                                        students.add(new Student(2, "Ravi"));
                                        System.out.println(students.size());
                                    }
                                }
                                """, """
                                2
                                """)),
                        List.of("In the second example two Student objects with id 1 count as the same student, because equals compares only the id."),
                        List.of("Overriding equals without hashCode. HashSet and HashMap may then fail to find objects that are equal.",
                                "Changing a field used in hashCode after the object is in a HashSet, which makes it impossible to find again."),
                        List.of("Set", "HashMap")),

                lesson("Map", 12,
                        List.of("Store and look up values by key with a Map",
                                "Loop over keys, values and entries"),
                        """
                        A Map stores key-value pairs. Each key appears at most once, and you use the key to find its value quickly. Putting a value for an existing key replaces the old value. Main methods are put, get (returns null when the key is absent), getOrDefault, containsKey, remove, size, keySet, values and entrySet.

                        The main implementations are HashMap (fastest, no guaranteed order), LinkedHashMap (keeps insertion order) and TreeMap (keeps keys sorted). To loop over everything, use entrySet(), which gives each key and its value together as a Map.Entry.
                        """,
                        List.of(ex("A small stock table", """
                                import java.util.Map;
                                import java.util.TreeMap;

                                public class MapDemo {
                                    public static void main(String[] args) {
                                        Map<String, Integer> stock = new TreeMap<>();
                                        stock.put("pen", 10);
                                        stock.put("book", 4);
                                        stock.put("bag", 2);
                                        stock.put("pen", 12);

                                        System.out.println(stock);
                                        System.out.println(stock.get("book"));
                                        System.out.println(stock.getOrDefault("ink", 0));
                                        System.out.println(stock.containsKey("bag"));

                                        stock.remove("bag");
                                        for (Map.Entry<String, Integer> e : stock.entrySet()) {
                                            System.out.println(e.getKey() + " -> " + e.getValue());
                                        }
                                    }
                                }
                                """, """
                                {bag=2, book=4, pen=12}
                                4
                                0
                                true
                                book -> 4
                                pen -> 12
                                """)),
                        List.of("The second put for pen replaced its value 10 with 12, so the map has three entries, not four."),
                        List.of("Calling a method on the result of get without checking for null when the key might be missing.",
                                "Using a mutable object as a key and then changing it."),
                        List.of("HashMap", "Set")),

                lesson("HashMap", 12,
                        List.of("Use HashMap to count and group data",
                                "Know its behaviour with null and with ordering"),
                        """
                        HashMap is the standard Map: lookups, inserts and removals take roughly constant time, and the iteration order is not guaranteed. It permits one null key and any number of null values. It is not thread-safe, so concurrent code needs a different approach, such as ConcurrentHashMap.

                        A classic use is counting how often things occur. For each item, read the current count with getOrDefault, add one, and put it back. Because the order of a HashMap is unspecified, wrap it in a TreeMap when you want sorted output.
                        """,
                        List.of(ex("Counting words", """
                                import java.util.HashMap;
                                import java.util.Map;
                                import java.util.TreeMap;

                                public class WordCount {
                                    public static void main(String[] args) {
                                        String text = "to be or not to be";
                                        Map<String, Integer> counts = new HashMap<>();

                                        for (String word : text.split(" ")) {
                                            counts.put(word, counts.getOrDefault(word, 0) + 1);
                                        }

                                        System.out.println(counts.get("to"));
                                        System.out.println(counts.get("or"));
                                        System.out.println(counts.size());
                                        System.out.println(counts.get("missing"));
                                        System.out.println(new TreeMap<>(counts));
                                    }
                                }
                                """, """
                                2
                                1
                                4
                                null
                                {be=2, not=1, or=1, to=2}
                                """)),
                        List.of("The text has six words but only four different ones, so the map has four entries."),
                        List.of("Assuming a HashMap iterates in insertion order. Use LinkedHashMap if that is what you need.",
                                "Unboxing a null result into an int, as in int n = counts.get(\"x\");, which throws NullPointerException."),
                        List.of("Map", "HashSet")),

                lesson("Queue", 12,
                        List.of("Use a Queue for first-in-first-out processing",
                                "Use PriorityQueue and a Deque as a stack"),
                        """
                        A Queue holds elements waiting to be processed, normally in first-in-first-out (FIFO) order, like people standing in a line. Each operation has two forms: offer, poll and peek return a special value (false or null) when they cannot do their job, while add, remove and element throw an exception instead.

                        LinkedList and ArrayDeque are common queue implementations. A PriorityQueue always hands out the smallest element first (by natural order or a Comparator), regardless of insertion order. A Deque (double-ended queue) can also work as a stack, which is last-in-first-out: push adds to the front, pop removes from the front and peek looks at it. ArrayDeque is the recommended class for stacks.
                        """,
                        List.of(ex("Queue, PriorityQueue and stack", """
                                import java.util.ArrayDeque;
                                import java.util.Deque;
                                import java.util.LinkedList;
                                import java.util.PriorityQueue;
                                import java.util.Queue;

                                public class QueueDemo {
                                    public static void main(String[] args) {
                                        Queue<String> line = new LinkedList<>();
                                        line.offer("Asha");
                                        line.offer("Ravi");
                                        line.offer("Meena");
                                        System.out.println(line.peek());
                                        System.out.println(line.poll());
                                        System.out.println(line.size());
                                        System.out.println(line);

                                        Queue<Integer> pq = new PriorityQueue<>();
                                        pq.offer(30);
                                        pq.offer(10);
                                        pq.offer(20);
                                        System.out.println(pq.poll());
                                        System.out.println(pq.poll());

                                        Deque<Integer> stack = new ArrayDeque<>();
                                        stack.push(1);
                                        stack.push(2);
                                        stack.push(3);
                                        System.out.println(stack.pop());
                                        System.out.println(stack.peek());
                                    }
                                }
                                """, """
                                Asha
                                Asha
                                2
                                [Ravi, Meena]
                                10
                                20
                                3
                                2
                                """)),
                        List.of("Printing a PriorityQueue directly does not show sorted order. Only poll() is guaranteed to return the smallest element."),
                        List.of("Using remove or element on an empty queue, which throws NoSuchElementException. poll and peek return null instead.",
                                "Using the old Stack class instead of ArrayDeque."),
                        List.of("LinkedList", "Comparator")),

                lesson("Iterators", 12,
                        List.of("Walk through a collection with an Iterator",
                                "Remove elements safely during iteration"),
                        """
                        An Iterator gives sequential access to a collection's elements. hasNext() asks whether another element exists, next() returns it and moves forward, and remove() deletes the element that was just returned. The for-each loop is built on top of Iterator.

                        Changing a collection while you loop over it with for-each (adding or removing elements) makes the iterator detect the modification and throw a ConcurrentModificationException. When you need to remove elements during a loop, use the iterator's own remove() method, or call removeIf on the collection.
                        """,
                        List.of(ex("Safe removal and a ConcurrentModificationException", """
                                import java.util.ArrayList;
                                import java.util.ConcurrentModificationException;
                                import java.util.Iterator;
                                import java.util.List;

                                public class IteratorDemo {
                                    public static void main(String[] args) {
                                        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));

                                        Iterator<Integer> it = numbers.iterator();
                                        while (it.hasNext()) {
                                            int n = it.next();
                                            if (n % 2 == 0) {
                                                it.remove();
                                            }
                                        }
                                        System.out.println(numbers);

                                        try {
                                            for (Integer n : numbers) {
                                                if (n == 1) {
                                                    numbers.remove(n);
                                                }
                                            }
                                        } catch (ConcurrentModificationException e) {
                                            System.out.println("ConcurrentModificationException");
                                        }
                                    }
                                }
                                """, """
                                [1, 3, 5]
                                ConcurrentModificationException
                                """)),
                        List.of("Detection of concurrent modification is on a best-effort basis. Do not write code that depends on whether the exception appears; simply do not modify a collection inside a for-each loop."),
                        List.of("Calling next() without checking hasNext(), which throws NoSuchElementException at the end.",
                                "Calling the iterator's remove() twice in a row without a next() in between."),
                        List.of("ArrayList", "For loops")),

                lesson("Comparable", 12,
                        List.of("Give a class a natural ordering with Comparable",
                                "Sort and find the maximum of your own objects"),
                        """
                        Numbers and Strings know how to sort themselves, but your own classes do not know which object comes first. The Comparable interface fixes that. A class implements Comparable<ItsOwnType> and defines one method, compareTo(other). It must return a negative number if this object comes before the other, zero if they are equal in order, and a positive number if it comes after.

                        This is the natural ordering of the class. Once it exists, Collections.sort, Arrays.sort, Collections.max, TreeSet and TreeMap all work with your objects without further help. Use the static compare methods, such as Integer.compare, instead of subtracting numbers, because subtraction can overflow.
                        """,
                        List.of(ex("Players ordered by score", """
                                import java.util.ArrayList;
                                import java.util.Collections;
                                import java.util.List;

                                class Player implements Comparable<Player> {
                                    String name;
                                    int score;

                                    Player(String name, int score) {
                                        this.name = name;
                                        this.score = score;
                                    }

                                    @Override
                                    public int compareTo(Player other) {
                                        return Integer.compare(this.score, other.score);
                                    }

                                    @Override
                                    public String toString() {
                                        return name + "(" + score + ")";
                                    }
                                }

                                public class ComparableDemo {
                                    public static void main(String[] args) {
                                        List<Player> players = new ArrayList<>(List.of(
                                                new Player("Asha", 70),
                                                new Player("Ravi", 95),
                                                new Player("Meena", 82)));

                                        Collections.sort(players);
                                        System.out.println(players);
                                        System.out.println(Collections.max(players));
                                    }
                                }
                                """, """
                                [Asha(70), Meena(82), Ravi(95)]
                                Ravi(95)
                                """)),
                        List.of("A class has only one natural ordering. If you need other orderings, use a Comparator."),
                        List.of("Returning a wrong sign, which sorts in reverse.",
                                "Writing compareTo so that it disagrees with equals, which can make TreeSet treat different objects as duplicates."),
                        List.of("Comparator", "Set")),

                lesson("Comparator", 12,
                        List.of("Define custom and multiple sort orders with Comparator",
                                "Chain comparators with thenComparing and reversed"),
                        """
                        A Comparator is an object outside the class that defines an ordering for two objects: compare(a, b) follows the same negative, zero and positive convention as compareTo. This lets you sort the same objects in many different ways, and you can sort classes you cannot change.

                        The Comparator interface has handy static helpers. Comparator.comparing(keyExtractor) sorts by a value taken from each object, comparingDouble and comparingInt do the same for numbers, reversed() flips the order, and thenComparing(...) adds a tie-breaker. The :: syntax, as in Employee::getDept, is a method reference: a short way to say "call getDept on each employee". Lists have a sort(comparator) method. Sorting is stable, so objects that compare as equal keep their original relative order.
                        """,
                        List.of(ex("Three ways to sort employees", """
                                import java.util.ArrayList;
                                import java.util.Comparator;
                                import java.util.List;

                                class Employee {
                                    private final String name;
                                    private final String dept;
                                    private final double salary;

                                    Employee(String name, String dept, double salary) {
                                        this.name = name;
                                        this.dept = dept;
                                        this.salary = salary;
                                    }

                                    String getName() {
                                        return name;
                                    }

                                    String getDept() {
                                        return dept;
                                    }

                                    double getSalary() {
                                        return salary;
                                    }

                                    @Override
                                    public String toString() {
                                        return name;
                                    }
                                }

                                class BySalaryDescending implements Comparator<Employee> {
                                    @Override
                                    public int compare(Employee a, Employee b) {
                                        return Double.compare(b.getSalary(), a.getSalary());
                                    }
                                }

                                public class ComparatorDemo {
                                    public static void main(String[] args) {
                                        List<Employee> staff = new ArrayList<>(List.of(
                                                new Employee("Asha", "IT", 50000),
                                                new Employee("Ravi", "HR", 40000),
                                                new Employee("Meena", "IT", 65000),
                                                new Employee("Kiran", "HR", 40000)));

                                        staff.sort(new BySalaryDescending());
                                        System.out.println(staff);

                                        staff.sort(Comparator.comparing(Employee::getDept).thenComparing(Employee::getName));
                                        System.out.println(staff);

                                        staff.sort(Comparator.comparingDouble(Employee::getSalary).reversed()
                                                .thenComparing(Employee::getName));
                                        System.out.println(staff);
                                    }
                                }
                                """, """
                                [Meena, Asha, Ravi, Kiran]
                                [Kiran, Ravi, Asha, Meena]
                                [Meena, Asha, Kiran, Ravi]
                                """)),
                        List.of("In the first line Ravi stays ahead of Kiran because they have equal salaries and the sort is stable.",
                                "Use Comparable for the one obvious natural order of a class, and Comparator for every other order."),
                        List.of("Subtracting values inside compare, which can overflow for large numbers. Use Integer.compare or Double.compare.",
                                "Forgetting the tie-breaker, so equal items appear in an unpredictable-looking order."),
                        List.of("Comparable", "Lambda expressions"))
        );

        QuizSeed quiz = new QuizSeed("Collections Framework Quiz",
                "Check that you can choose the right collection and use it correctly.",
                List.of(
                        q("Which collection does NOT allow duplicate elements?", 2,
                                "A Set stores each element at most once. Lists and Queues allow duplicates.",
                                "List", "Queue", "Set", "ArrayList"),
                        q("Which Map implementation keeps its keys sorted?", 3,
                                "TreeMap keeps its keys in sorted order. HashMap has no guaranteed order and LinkedHashMap keeps insertion order.",
                                "HashMap", "LinkedHashMap", "Hashtable", "TreeMap"),
                        q("Which List implementation gives the fastest access to an element by its index?", 0,
                                "ArrayList is backed by an array, so get(index) is very fast. LinkedList must walk through nodes.",
                                "ArrayList", "LinkedList", "Both are equally fast", "Neither supports index access"),
                        q("What does Queue.poll() return when the queue is empty?", 1,
                                "poll returns null on an empty queue. remove() would throw a NoSuchElementException instead.",
                                "It throws an exception", "null", "0", "An empty string"),
                        q("Your class will be stored in a HashSet. Which methods should it override?", 2,
                                "HashSet relies on hashCode and equals, and they must be consistent with each other.",
                                "toString only", "compareTo only", "equals and hashCode", "clone and finalize"),
                        q("When would you use a Comparator instead of Comparable?", 3,
                                "A Comparator defines an ordering outside the class, so you can have several orderings or sort classes you cannot modify. Comparable gives a class its single natural order.",
                                "When the class already has a natural order", "Only for sorting Strings",
                                "Comparator is just a faster Comparable", "When you need an ordering other than the class's natural one")
                ));

        List<PracticeSeed> practiceSet = List.of(

                practice("Remove duplicates, keep the order", "EASY",
                        "The list {5, 3, 5, 8, 3, 1} contains duplicates. Create a new collection with each number only once, keeping the order of first appearance, and print it.",
                        """
                        import java.util.List;

                        public class Deduplicate {
                            public static void main(String[] args) {
                                List<Integer> numbers = List.of(5, 3, 5, 8, 3, 1);
                                // TODO: remove the duplicates, keep the first-seen order, and print the result
                            }
                        }
                        """,
                        "A LinkedHashSet removes duplicates and remembers insertion order. You can create one directly from the list.",
                        """
                        import java.util.LinkedHashSet;
                        import java.util.List;
                        import java.util.Set;

                        public class Deduplicate {
                            public static void main(String[] args) {
                                List<Integer> numbers = List.of(5, 3, 5, 8, 3, 1);
                                Set<Integer> unique = new LinkedHashSet<>(numbers);
                                System.out.println(unique);
                            }
                        }
                        """,
                        "[5, 3, 8, 1]"),

                practice("Word frequency", "MEDIUM",
                        "Count how many times each word appears in \"the cat and the hat and the bat\" and print the counts with the words in alphabetical order.",
                        """
                        import java.util.Map;

                        public class WordFrequency {
                            public static void main(String[] args) {
                                String text = "the cat and the hat and the bat";
                                // TODO: count each word and print the map with the words in alphabetical order
                            }
                        }
                        """,
                        "Count with a HashMap and getOrDefault, then print new TreeMap<>(counts) so the keys come out sorted.",
                        """
                        import java.util.HashMap;
                        import java.util.Map;
                        import java.util.TreeMap;

                        public class WordFrequency {
                            public static void main(String[] args) {
                                String text = "the cat and the hat and the bat";
                                Map<String, Integer> counts = new HashMap<>();
                                for (String word : text.split(" ")) {
                                    counts.put(word, counts.getOrDefault(word, 0) + 1);
                                }
                                System.out.println(new TreeMap<>(counts));
                            }
                        }
                        """,
                        "{and=2, bat=1, cat=1, hat=1, the=3}"),

                practice("Sort words by length, then alphabetically", "MEDIUM",
                        "Sort the list {banana, kiwi, apple, fig, cherry} so shorter words come first. Words with the same length must be in alphabetical order. Print the sorted list.",
                        """
                        import java.util.ArrayList;
                        import java.util.List;

                        public class SortWords {
                            public static void main(String[] args) {
                                List<String> words = new ArrayList<>(List.of("banana", "kiwi", "apple", "fig", "cherry"));
                                // TODO: sort by length, then alphabetically, and print the list
                            }
                        }
                        """,
                        "Use Comparator.comparing(String::length) and add a tie-breaker with thenComparing(Comparator.naturalOrder()).",
                        """
                        import java.util.ArrayList;
                        import java.util.Comparator;
                        import java.util.List;

                        public class SortWords {
                            public static void main(String[] args) {
                                List<String> words = new ArrayList<>(List.of("banana", "kiwi", "apple", "fig", "cherry"));
                                words.sort(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()));
                                System.out.println(words);
                            }
                        }
                        """,
                        "[fig, kiwi, apple, banana, cherry]")
        );

        return new ModuleSeed("Collections Framework",
                "Store and organise data with lists, sets, maps and queues, and control how objects are ordered.",
                lessons, quiz, practiceSet);
    }

    // ====================================================================================
    //  MODULE 6: ADVANCED JAVA
    // ====================================================================================

    static ModuleSeed module6() {
        List<LessonSeed> lessons = List.of(

                lesson("Generics", 14,
                        List.of("Write classes and methods that work with any type",
                                "Use bounded type parameters and wildcards"),
                        """
                        Generics let you write one class or method that works with many types while the compiler still checks every use. Instead of storing Object and casting later, you declare a type parameter such as T in angle brackets. A Box<String> can hold only Strings, and a Box<Integer> only Integers; mistakes become compile errors rather than ClassCastExceptions at run time.

                        A generic method declares its own type parameter before the return type, as in <T> T pick(T a, T b). A bound narrows what T may be: <T extends Comparable<T>> means T must be comparable with itself, so the method can call compareTo. Wildcards describe unknown types in parameters: List<? extends Number> accepts a list of any Number subtype and can be read from, but you cannot add elements to it.

                        Generics exist only at compile time. The compiler removes the type arguments afterwards (type erasure), which is why you cannot write new T() or use primitives such as int as type arguments; use the wrapper classes like Integer instead.
                        """,
                        List.of(ex("A generic class, a bounded method and a wildcard", """
                                import java.util.List;

                                class Box<T> {
                                    private T value;

                                    Box(T value) {
                                        this.value = value;
                                    }

                                    T get() {
                                        return value;
                                    }

                                    void set(T value) {
                                        this.value = value;
                                    }
                                }

                                public class GenericsDemo {

                                    static <T extends Comparable<T>> T larger(T a, T b) {
                                        return a.compareTo(b) >= 0 ? a : b;
                                    }

                                    static double sum(List<? extends Number> numbers) {
                                        double total = 0;
                                        for (Number n : numbers) {
                                            total += n.doubleValue();
                                        }
                                        return total;
                                    }

                                    public static void main(String[] args) {
                                        Box<String> name = new Box<>("Asha");
                                        Box<Integer> age = new Box<>(21);
                                        String s = name.get();
                                        int a = age.get();
                                        System.out.println(s + " " + a);

                                        System.out.println(larger(3, 9));
                                        System.out.println(larger("pear", "apple"));
                                        System.out.println(sum(List.of(1, 2, 3)));
                                        System.out.println(sum(List.of(1.5, 2.5)));
                                    }
                                }
                                """, """
                                Asha 21
                                9
                                pear
                                6.0
                                4.0
                                """)),
                        List.of("Read the diamond <> as \"infer the type arguments here\".",
                                "A common naming convention: T for a type, E for an element, K and V for key and value."),
                        List.of("Trying to use a primitive as a type argument, as in Box<int>.",
                                "Mixing raw types (Box with no <>) into new code, which switches off the compiler's checks.",
                                "Expecting List<Integer> to be a subtype of List<Number>. It is not; use List<? extends Number>."),
                        List.of("Introduction to collections", "Lambda expressions")),

                lesson("Lambda expressions", 14,
                        List.of("Write lambda expressions in their different forms",
                                "Use method references and explain effectively final variables"),
                        """
                        A lambda expression is a short way to write a small piece of behaviour as a value, so you can pass it to a method. Its shape is (parameters) -> body. With one expression as the body you need no braces and no return; with several statements you use braces and return explicitly. The parameter types can usually be left out, and the parentheses can be dropped when there is exactly one parameter.

                        A lambda can be used wherever a functional interface is expected, meaning an interface with exactly one abstract method, such as Runnable or Comparator. A lambda can read local variables from the surrounding method, but only if they are effectively final: never reassigned after they get their value.

                        A method reference is an even shorter form when the lambda only calls an existing method: System.out::println means w -> System.out.println(w), and String::length means s -> s.length().
                        """,
                        List.of(ex("Lambdas and method references", """
                                import java.util.ArrayList;
                                import java.util.Comparator;
                                import java.util.List;

                                public class LambdaDemo {
                                    public static void main(String[] args) {
                                        Runnable hello = () -> System.out.println("Running");
                                        hello.run();

                                        Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());
                                        List<String> words = new ArrayList<>(List.of("kiwi", "fig", "banana"));
                                        words.sort(byLength);
                                        System.out.println(words);

                                        words.forEach(w -> System.out.println(w.toUpperCase()));

                                        int factor = 3;
                                        List.of(1, 2, 3).forEach(n -> System.out.println(n * factor));

                                        words.forEach(System.out::println);
                                    }
                                }
                                """, """
                                Running
                                [fig, kiwi, banana]
                                FIG
                                KIWI
                                BANANA
                                3
                                6
                                9
                                fig
                                kiwi
                                banana
                                """)),
                        List.of("Lambdas replaced most uses of anonymous inner classes, with much less noise.",
                                "Keep lambdas short. If one needs many lines, move the code into a named method and use a method reference."),
                        List.of("Changing a local variable that a lambda uses. The compiler requires it to be effectively final.",
                                "Writing a lambda where the target type is not a functional interface."),
                        List.of("Functional interfaces", "Stream API", "Comparator")),

                lesson("Functional interfaces", 14,
                        List.of("Recognise the main interfaces of java.util.function",
                                "Create your own functional interface and combine functions"),
                        """
                        A functional interface has exactly one abstract method. It may also have default and static methods. The annotation @FunctionalInterface is optional but useful: the compiler then reports an error if someone adds a second abstract method.

                        The package java.util.function provides the common shapes so you rarely need your own. Predicate<T> takes a value and returns a boolean (test). Function<T, R> turns a T into an R (apply). Consumer<T> takes a value and returns nothing (accept). Supplier<T> takes nothing and produces a value (get). BiFunction takes two arguments. UnaryOperator<T> and BinaryOperator<T> are functions whose input and output types are the same.

                        These interfaces come with default methods for combining behaviour: Predicate has negate, and and or; Function has andThen (run this, then the other) and compose (run the other first, then this).
                        """,
                        List.of(ex("The standard functional interfaces", """
                                import java.util.function.BiFunction;
                                import java.util.function.Consumer;
                                import java.util.function.Function;
                                import java.util.function.Predicate;
                                import java.util.function.Supplier;

                                public class FunctionalDemo {

                                    @FunctionalInterface
                                    interface Greeter {
                                        String greet(String name);
                                    }

                                    public static void main(String[] args) {
                                        Predicate<Integer> isEven = n -> n % 2 == 0;
                                        Function<String, Integer> length = String::length;
                                        Consumer<String> printer = s -> System.out.println("Hello " + s);
                                        Supplier<String> supplier = () -> "generated";
                                        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
                                        Greeter greeter = name -> "Hi " + name;

                                        System.out.println(isEven.test(4));
                                        System.out.println(isEven.negate().test(4));
                                        System.out.println(length.apply("CodeNest"));
                                        printer.accept("Asha");
                                        System.out.println(supplier.get());
                                        System.out.println(add.apply(2, 3));
                                        System.out.println(greeter.greet("Ravi"));

                                        Function<Integer, Integer> doubleIt = x -> x * 2;
                                        Function<Integer, Integer> plusOne = x -> x + 1;
                                        System.out.println(doubleIt.andThen(plusOne).apply(5));
                                        System.out.println(doubleIt.compose(plusOne).apply(5));
                                    }
                                }
                                """, """
                                true
                                false
                                8
                                Hello Asha
                                generated
                                5
                                Hi Ravi
                                11
                                12
                                """)),
                        List.of("andThen applies doubleIt first (5 becomes 10) and then plusOne (11). compose applies plusOne first (6) and then doubleIt (12).",
                                "Runnable, Callable and Comparator are older functional interfaces from before Java 8."),
                        List.of("Confusing andThen and compose, which run the two functions in opposite orders.",
                                "Creating a custom interface when Predicate, Function or Consumer already fits."),
                        List.of("Lambda expressions", "Stream API")),

                lesson("Stream API", 16,
                        List.of("Build stream pipelines with filter, map and collect",
                                "Use common terminal operations such as count, sum and groupingBy"),
                        """
                        A stream is a pipeline for processing a sequence of elements in a declarative style: you describe what to do, not how to loop. A pipeline has three parts. A source creates the stream (list.stream()). Intermediate operations such as filter, map and sorted each return a new stream and are lazy, meaning nothing happens yet. A terminal operation such as collect, count, sum, forEach or anyMatch starts the work and produces the result.

                        A stream never changes its source: the original list stays as it was. A stream can be consumed only once; using it again throws an IllegalStateException. For numbers, mapToInt gives an IntStream with sum, max and average, avoiding the cost of boxing. Collectors.groupingBy splits elements into groups, and Collectors.joining glues strings together.

                        Keep stream operations free of side effects, such as changing an outside variable, so the code stays correct and could be parallelised.
                        """,
                        List.of(ex("A stream pipeline", """
                                import java.util.List;
                                import java.util.Map;
                                import java.util.TreeMap;
                                import java.util.stream.Collectors;

                                public class StreamDemo {
                                    public static void main(String[] args) {
                                        List<Integer> numbers = List.of(5, 12, 8, 3, 20, 7);

                                        List<Integer> evenSquares = numbers.stream()
                                                .filter(n -> n % 2 == 0)
                                                .map(n -> n * n)
                                                .toList();
                                        System.out.println(evenSquares);

                                        int total = numbers.stream().mapToInt(Integer::intValue).sum();
                                        System.out.println(total);

                                        System.out.println(numbers.stream().filter(n -> n > 6).count());

                                        double average = numbers.stream().mapToInt(Integer::intValue).average().orElse(0);
                                        System.out.println(Math.round(average * 100) / 100.0);

                                        List<String> names = List.of("Asha", "Ravi", "Meena", "Kiran");
                                        System.out.println(names.stream().sorted().collect(Collectors.joining(", ")));
                                        System.out.println(names.stream().anyMatch(n -> n.startsWith("M")));

                                        Map<Integer, List<String>> byLength = names.stream()
                                                .collect(Collectors.groupingBy(String::length));
                                        System.out.println(new TreeMap<>(byLength));
                                    }
                                }
                                """, """
                                [144, 64, 400]
                                55
                                4
                                9.17
                                Asha, Kiran, Meena, Ravi
                                true
                                {4=[Asha, Ravi], 5=[Meena, Kiran]}
                                """)),
                        List.of("Stream.toList() (Java 16+) returns an unmodifiable list. Use Collectors.toList() or new ArrayList<>(...) if you need to change the result.",
                                "The TreeMap in the last line only makes the printed order predictable."),
                        List.of("Writing a stream pipeline with no terminal operation and wondering why nothing happens.",
                                "Reusing a stream after a terminal operation.",
                                "Using streams for everything. A plain loop is sometimes clearer."),
                        List.of("Lambda expressions", "Optional")),

                lesson("Optional", 12,
                        List.of("Use Optional to represent a value that may be absent",
                                "Transform and unwrap an Optional safely"),
                        """
                        Optional<T> is a small container that either holds a value or is empty. It was added to give methods an honest way to say "there might be no result", instead of returning null and hoping callers remember to check. A method that returns Optional<User> makes the possibility of absence part of its signature.

                        Create one with Optional.of(value) (the value must not be null), Optional.empty(), or Optional.ofNullable(value) (null becomes empty). Then avoid calling get() blindly, because on an empty Optional it throws NoSuchElementException. Prefer orElse(default), orElseGet(supplier), orElseThrow, ifPresent, ifPresentOrElse, and transformations with map and filter.

                        Use Optional mainly as a return type. Do not use it for fields, method parameters or collection elements, and never return null from a method whose return type is Optional.
                        """,
                        List.of(ex("Finding a user that may not exist", """
                                import java.util.Optional;

                                public class OptionalDemo {

                                    static Optional<String> findUser(String id) {
                                        if (id.equals("u1")) {
                                            return Optional.of("Asha");
                                        }
                                        return Optional.empty();
                                    }

                                    public static void main(String[] args) {
                                        Optional<String> found = findUser("u1");
                                        Optional<String> missing = findUser("u9");

                                        System.out.println(found.isPresent());
                                        System.out.println(found.get());
                                        System.out.println(missing.orElse("guest"));
                                        System.out.println(found.map(String::toUpperCase).orElse("none"));

                                        missing.ifPresentOrElse(
                                                n -> System.out.println("Hello " + n),
                                                () -> System.out.println("No user"));
                                        found.ifPresent(n -> System.out.println("Hello " + n));

                                        String text = null;
                                        System.out.println(Optional.ofNullable(text).isPresent());
                                    }
                                }
                                """, """
                                true
                                Asha
                                guest
                                ASHA
                                No user
                                Hello Asha
                                false
                                """)),
                        List.of("found.get() is safe here only because isPresent() was checked just before. In real code prefer orElse, map or ifPresent."),
                        List.of("Calling get() without checking, which just moves the NullPointerException to a NoSuchElementException.",
                                "Passing Optional as a method parameter, which forces callers to wrap values for no benefit."),
                        List.of("Stream API", "Unchecked exceptions")),

                lesson("Date and Time API", 14,
                        List.of("Create and combine dates and times with java.time",
                                "Calculate differences and format dates"),
                        """
                        The java.time package, added in Java 8, replaces the old Date and Calendar classes. Its types are immutable and thread-safe: every operation such as plusDays returns a new object and leaves the original unchanged. LocalDate holds a date, LocalTime a time of day, LocalDateTime both, and ZonedDateTime adds a time zone. Instant is a point on the timeline, and Duration and Period describe amounts of time.

                        You create values with of(...), now() or parse(...). To measure differences, use ChronoUnit.DAYS.between(a, b) for a single unit, Period.between(a, b) for years, months and days, and Duration.between(...) for hours and minutes. A DateTimeFormatter converts to and from text using a pattern such as dd/MM/yyyy.

                        The examples use fixed dates, so they give the same output every time you run them.
                        """,
                        List.of(ex("Working with dates", """
                                import java.time.Duration;
                                import java.time.LocalDate;
                                import java.time.LocalDateTime;
                                import java.time.LocalTime;
                                import java.time.Period;
                                import java.time.format.DateTimeFormatter;
                                import java.time.temporal.ChronoUnit;

                                public class DateTimeDemo {
                                    public static void main(String[] args) {
                                        LocalDate date = LocalDate.of(2026, 10, 16);
                                        LocalTime time = LocalTime.of(14, 30);
                                        LocalDateTime meeting = LocalDateTime.of(date, time);

                                        System.out.println(date);
                                        System.out.println(meeting);
                                        System.out.println(date.plusDays(20));
                                        System.out.println(date.getDayOfWeek());

                                        LocalDate exam = LocalDate.of(2026, 12, 1);
                                        System.out.println(ChronoUnit.DAYS.between(date, exam));
                                        System.out.println(Period.between(date, exam));

                                        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                        System.out.println(date.format(format));
                                        System.out.println(LocalDate.parse("2027-01-26"));
                                        System.out.println(date.isLeapYear());

                                        System.out.println(Duration.between(LocalTime.of(9, 0), LocalTime.of(11, 30)));
                                    }
                                }
                                """, """
                                2026-10-16
                                2026-10-16T14:30
                                2026-11-05
                                FRIDAY
                                46
                                P1M15D
                                16/10/2026
                                2027-01-26
                                false
                                PT2H30M
                                """)),
                        List.of("Months are numbered 1 to 12 in java.time, unlike the old Calendar class, where January was 0.",
                                "Use LocalDate.now() or LocalDateTime.now() when you need the current moment. It returns a different value each run."),
                        List.of("Calling date.plusDays(5) and ignoring the result. Dates are immutable, so you must keep the returned object.",
                                "Using the legacy java.util.Date in new code.",
                                "Formatting with mm (minutes) when you meant MM (month)."),
                        List.of("File handling")),

                lesson("File handling", 14,
                        List.of("Read and write text files with java.nio.file",
                                "Close files reliably with try-with-resources"),
                        """
                        The java.nio.file package is the modern way to work with files. A Path represents a location, and the helper class Files offers one-line operations: writeString, readString, readAllLines, exists, delete, copy, createTempFile and more. These methods use UTF-8 by default and throw IOException (a checked exception) when something goes wrong, so you must catch it or declare it.

                        For large files, do not load everything into memory. Use Files.newBufferedReader or Files.newBufferedWriter inside try-with-resources, and process one line at a time. Relative paths are resolved from the directory where the program is started, which is the project folder when you run from an IDE.

                        These examples use temporary files and delete them at the end, so you can run them without leaving anything behind.
                        """,
                        List.of(ex("Writing, appending and reading", """
                                import java.io.IOException;
                                import java.nio.file.Files;
                                import java.nio.file.Path;
                                import java.nio.file.StandardOpenOption;
                                import java.util.List;

                                public class FileDemo {
                                    public static void main(String[] args) {
                                        try {
                                            Path file = Files.createTempFile("codenest", ".txt");
                                            Files.writeString(file, "line one\\nline two\\n");
                                            Files.writeString(file, "line three\\n", StandardOpenOption.APPEND);

                                            List<String> lines = Files.readAllLines(file);
                                            System.out.println(lines.size());
                                            for (String line : lines) {
                                                System.out.println(line);
                                            }

                                            System.out.println(Files.exists(file));
                                            Files.delete(file);
                                            System.out.println(Files.exists(file));
                                        } catch (IOException e) {
                                            System.out.println("I/O error: " + e.getMessage());
                                        }
                                    }
                                }
                                """, """
                                3
                                line one
                                line two
                                line three
                                true
                                false
                                """),
                                ex("Buffered reading line by line", """
                                import java.io.BufferedReader;
                                import java.io.BufferedWriter;
                                import java.io.IOException;
                                import java.nio.file.Files;
                                import java.nio.file.Path;

                                public class BufferedDemo {
                                    public static void main(String[] args) throws IOException {
                                        Path file = Files.createTempFile("notes", ".txt");

                                        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                                            writer.write("alpha");
                                            writer.newLine();
                                            writer.write("beta");
                                            writer.newLine();
                                        }

                                        try (BufferedReader reader = Files.newBufferedReader(file)) {
                                            String line;
                                            int count = 0;
                                            while ((line = reader.readLine()) != null) {
                                                count++;
                                                System.out.println(count + ": " + line);
                                            }
                                        }

                                        Files.delete(file);
                                    }
                                }
                                """, """
                                1: alpha
                                2: beta
                                """)),
                        List.of("The older java.io classes (File, FileReader) still work, but java.nio.file is the recommended API for new code.",
                                "Files.lines(path) gives a Stream of lines; close it with try-with-resources as well."),
                        List.of("Forgetting to close a reader or writer, which can lose buffered data and leak file handles.",
                                "Assuming a relative path points where you expect. Print path.toAbsolutePath() when in doubt."),
                        List.of("Checked exceptions", "finally", "Serialization fundamentals")),

                lesson("Serialization fundamentals", 12,
                        List.of("Convert an object to bytes and back",
                                "Use transient and serialVersionUID, and understand the security risk"),
                        """
                        Serialization turns an object into a stream of bytes so it can be saved or sent, and deserialization rebuilds the object from those bytes. In Java, a class opts in by implementing the marker interface java.io.Serializable. ObjectOutputStream.writeObject writes an object, and ObjectInputStream.readObject reads it back, together with all the objects it refers to, so they must be serializable too.

                        Fields marked transient are skipped and come back with their default value (null, 0 or false), which suits passwords or temporary caches. Static fields belong to the class, not the object, so they are not serialized. The constant serialVersionUID identifies the version of the class; if it differs when reading, an InvalidClassException is thrown.

                        Java's built-in deserialization is risky: never deserialize bytes from an untrusted source, because crafted data can trigger harmful behaviour. For exchanging data between systems, prefer a text format such as JSON.
                        """,
                        List.of(ex("A round trip in memory", """
                                import java.io.ByteArrayInputStream;
                                import java.io.ByteArrayOutputStream;
                                import java.io.IOException;
                                import java.io.ObjectInputStream;
                                import java.io.ObjectOutputStream;
                                import java.io.Serializable;

                                class Student implements Serializable {
                                    private static final long serialVersionUID = 1L;

                                    String name;
                                    int marks;
                                    transient String password;

                                    Student(String name, int marks, String password) {
                                        this.name = name;
                                        this.marks = marks;
                                        this.password = password;
                                    }
                                }

                                public class SerializationDemo {

                                    static Student roundTrip(Student student) throws IOException, ClassNotFoundException {
                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
                                            out.writeObject(student);
                                        }
                                        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                                            return (Student) in.readObject();
                                        }
                                    }

                                    public static void main(String[] args) throws Exception {
                                        Student original = new Student("Asha", 88, "secret");
                                        Student copy = roundTrip(original);

                                        System.out.println(copy.name);
                                        System.out.println(copy.marks);
                                        System.out.println(copy.password);
                                        System.out.println(copy == original);
                                    }
                                }
                                """, """
                                Asha
                                88
                                null
                                false
                                """)),
                        List.of("The copy is a separate object with the same data, which is why == is false.",
                                "Writing to a ByteArrayOutputStream keeps the demo in memory. Use a FileOutputStream to save to disk."),
                        List.of("Forgetting that every object reachable from a serializable object must also be serializable. Otherwise a NotSerializableException is thrown.",
                                "Deserializing data from a user or from the network."),
                        List.of("File handling", "Multithreading fundamentals")),

                lesson("Multithreading fundamentals", 16,
                        List.of("Start threads and wait for them with join",
                                "Run tasks with an ExecutorService and read results through a Future"),
                        """
                        A thread is an independent path of execution inside a program. Multithreading lets one program do several things at once, such as keeping an interface responsive while a download runs, or using several processor cores. All threads of a program share the same memory, which is what makes them powerful and also what makes them tricky.

                        You create a thread by giving a Runnable (often a lambda) to the Thread constructor and calling start(). Do not call run() directly: that just runs the code in the current thread. join() makes one thread wait until another has finished. The order in which threads run is decided by the scheduler, so the output of unsynchronised threads can differ from run to run.

                        In real applications you rarely manage threads by hand. An ExecutorService keeps a pool of threads, accepts tasks with submit, and returns a Future whose get() waits for the result. Always call shutdown() when you are finished with a pool. Java 21 also adds lightweight virtual threads (for example Executors.newVirtualThreadPerTaskExecutor()), which make it cheap to run many blocking tasks.
                        """,
                        List.of(ex("Threads, join and a thread pool", """
                                import java.util.concurrent.ExecutorService;
                                import java.util.concurrent.Executors;
                                import java.util.concurrent.Future;

                                public class ThreadDemo {
                                    public static void main(String[] args) throws Exception {
                                        Thread worker = new Thread(
                                                () -> System.out.println("Hello from " + Thread.currentThread().getName()),
                                                "worker-1");
                                        worker.start();
                                        worker.join();
                                        System.out.println("Main continues after worker");

                                        ExecutorService pool = Executors.newFixedThreadPool(2);
                                        Future<Integer> future = pool.submit(() -> {
                                            int sum = 0;
                                            for (int i = 1; i <= 100; i++) {
                                                sum += i;
                                            }
                                            return sum;
                                        });
                                        System.out.println(future.get());
                                        pool.shutdown();
                                    }
                                }
                                """, """
                                Hello from worker-1
                                Main continues after worker
                                5050
                                """)),
                        List.of("Because of worker.join(), the two printed lines always appear in this order. Without join, the order would not be guaranteed.",
                                "A program ends when all non-daemon threads have finished, even after main returns."),
                        List.of("Calling run() instead of start(), so no new thread is created.",
                                "Forgetting executor.shutdown(), which can keep the program running.",
                                "Assuming two threads will print in a particular order."),
                        List.of("Synchronization fundamentals", "Lambda expressions")),

                lesson("Synchronization fundamentals", 16,
                        List.of("Explain race conditions and why count++ is not safe",
                                "Protect shared data with synchronized or atomic classes"),
                        """
                        When several threads read and change the same data, the result can depend on the exact timing, which is called a race condition. A statement such as count++ looks like one step, but it is really three: read the value, add one, write it back. Two threads can read the same old value, and one update is then lost.

                        The synchronized keyword makes a method or block run by only one thread at a time for a given object (its lock), and it also makes the changes visible to the next thread that takes the lock. For simple counters, the classes in java.util.concurrent.atomic, such as AtomicInteger, use special processor instructions and need no explicit lock. The volatile keyword guarantees visibility of a variable but does not make count++ atomic.

                        Locks bring their own danger: a deadlock happens when two threads each hold a lock the other one needs, and both wait forever. Keep synchronised sections short, always acquire several locks in the same order, and prefer ready-made concurrent classes such as ConcurrentHashMap.
                        """,
                        List.of(ex("A safe counter, two ways", """
                                import java.util.concurrent.atomic.AtomicInteger;

                                class Counter {
                                    private int count = 0;

                                    synchronized void increment() {
                                        count++;
                                    }

                                    int get() {
                                        return count;
                                    }
                                }

                                public class SyncDemo {
                                    public static void main(String[] args) throws InterruptedException {
                                        Counter counter = new Counter();
                                        AtomicInteger atomic = new AtomicInteger();

                                        Runnable work = () -> {
                                            for (int i = 0; i < 1000; i++) {
                                                counter.increment();
                                                atomic.incrementAndGet();
                                            }
                                        };

                                        Thread a = new Thread(work);
                                        Thread b = new Thread(work);
                                        a.start();
                                        b.start();
                                        a.join();
                                        b.join();

                                        System.out.println(counter.get());
                                        System.out.println(atomic.get());
                                    }
                                }
                                """, """
                                2000
                                2000
                                """)),
                        List.of("If increment() were not synchronized, the first number would often be less than 2000, and a different number on each run. That is the race condition in action.",
                                "join() also guarantees that everything the finished thread wrote is visible to the thread that waited."),
                        List.of("Synchronizing only the writing method and reading the shared value without any protection.",
                                "Using different lock objects for the same data.",
                                "Holding a lock while doing slow work such as network calls."),
                        List.of("Multithreading fundamentals"))
        );

        QuizSeed quiz = new QuizSeed("Advanced Java Quiz",
                "Check your understanding of generics, lambdas, streams, Optional, dates, files and threads.",
                List.of(
                        q("Which functional interface takes one argument and returns a boolean?", 1,
                                "Predicate<T> has a single method test(T) that returns a boolean.",
                                "Function", "Predicate", "Supplier", "Consumer"),
                        q("What does list.stream().filter(...).map(...) do to the original list?", 3,
                                "Stream operations never change their source. They produce new streams (and results) from it.",
                                "Removes the filtered elements from it", "Replaces its elements with the mapped values",
                                "Sorts it", "Leaves it unchanged"),
                        q("What can you do with a List<? extends Number> parameter?", 1,
                                "You can read its elements as Number, but you cannot add elements because the exact element type is unknown.",
                                "Add any Number to it", "Read its elements as Number", "Add Integers only", "Nothing at all"),
                        q("What is Optional mainly meant for?", 2,
                                "Optional is a return type that says a result may be absent, instead of returning null.",
                                "Replacing every field in a class", "Making objects immutable",
                                "Representing a return value that may be absent", "Speeding up loops"),
                        q("Why can counter++ from two threads without synchronization give a wrong total?", 1,
                                "count++ is read, add and write. Two threads can interleave these steps and one update gets lost.",
                                "Java forbids two threads from using one variable", "The increment is not atomic, so updates can be lost",
                                "The JVM runs only one thread at a time", "int cannot be changed by threads"),
                        q("Which keyword excludes a field from Java serialization?", 3,
                                "A transient field is skipped when the object is serialized and gets its default value when it is read back.",
                                "static", "final", "volatile", "transient")
                ));

        List<PracticeSeed> practiceSet = List.of(

                practice("Squares of even numbers", "EASY",
                        "Use a stream to take the numbers 1 to 10, keep only the even ones, square them, and print the resulting list.",
                        """
                        import java.util.List;

                        public class EvenSquares {
                            public static void main(String[] args) {
                                List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
                                // TODO: filter the even numbers, square them, and print the list
                            }
                        }
                        """,
                        "Chain filter(n -> n % 2 == 0), then map(n -> n * n), and finish with toList().",
                        """
                        import java.util.List;

                        public class EvenSquares {
                            public static void main(String[] args) {
                                List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
                                List<Integer> result = numbers.stream()
                                        .filter(n -> n % 2 == 0)
                                        .map(n -> n * n)
                                        .toList();
                                System.out.println(result);
                            }
                        }
                        """,
                        "[4, 16, 36, 64, 100]"),

                practice("Safe lookup with Optional", "MEDIUM",
                        "Write a method findCity(String code) that returns an Optional<String>. It should find the city in the given map (DEL is Delhi, BOM is Mumbai) and return an empty Optional for an unknown code. Print the city for DEL, and the text Unknown for XXX.",
                        """
                        import java.util.Map;
                        import java.util.Optional;

                        public class CityLookup {
                            static final Map<String, String> CITIES = Map.of("DEL", "Delhi", "BOM", "Mumbai");

                            // TODO: write findCity(String code) returning Optional<String>

                            public static void main(String[] args) {
                                // TODO: print the city for DEL, and "Unknown" for XXX
                            }
                        }
                        """,
                        "Optional.ofNullable(CITIES.get(code)) turns a missing key into an empty Optional. Then use orElse(\"Unknown\").",
                        """
                        import java.util.Map;
                        import java.util.Optional;

                        public class CityLookup {
                            static final Map<String, String> CITIES = Map.of("DEL", "Delhi", "BOM", "Mumbai");

                            static Optional<String> findCity(String code) {
                                return Optional.ofNullable(CITIES.get(code));
                            }

                            public static void main(String[] args) {
                                System.out.println(findCity("DEL").orElse("Unknown"));
                                System.out.println(findCity("XXX").orElse("Unknown"));
                            }
                        }
                        """,
                        """
                        Delhi
                        Unknown
                        """),

                practice("Thread-safe total", "MEDIUM",
                        "Start two threads that each add 1 to a shared counter 1000 times. Wait for both to finish and print Total: followed by the counter. The result must always be 2000.",
                        """
                        public class SafeTotal {
                            public static void main(String[] args) throws InterruptedException {
                                // TODO: create a thread-safe counter, run two threads, join them,
                                // and print "Total: <value>"
                            }
                        }
                        """,
                        "An AtomicInteger with incrementAndGet() is thread-safe. Call join() on both threads before printing.",
                        """
                        import java.util.concurrent.atomic.AtomicInteger;

                        public class SafeTotal {
                            public static void main(String[] args) throws InterruptedException {
                                AtomicInteger counter = new AtomicInteger();

                                Runnable work = () -> {
                                    for (int i = 0; i < 1000; i++) {
                                        counter.incrementAndGet();
                                    }
                                };

                                Thread a = new Thread(work);
                                Thread b = new Thread(work);
                                a.start();
                                b.start();
                                a.join();
                                b.join();

                                System.out.println("Total: " + counter.get());
                            }
                        }
                        """,
                        "Total: 2000")
        );

        return new ModuleSeed("Advanced Java",
                "Write expressive modern Java with generics, lambdas and streams, and handle dates, files and concurrency.",
                lessons, quiz, practiceSet);
    }

    // ====================================================================================
    //  MODULE 7: DATABASE AND BACKEND FOUNDATIONS
    // ====================================================================================

    static ModuleSeed module7() {
        List<LessonSeed> lessons = List.of(

                lesson("JDBC fundamentals", 16,
                        List.of("Explain what JDBC is and its main interfaces",
                                "Run an INSERT and a SELECT safely with PreparedStatement"),
                        """
                        JDBC (Java Database Connectivity) is the standard Java API for talking to relational databases. Your code uses the interfaces in java.sql, and a database-specific driver (a library you add to the project) does the actual work. The key types are: Connection, one open session with the database; Statement and PreparedStatement, which carry SQL; and ResultSet, which gives access to the rows a query returns.

                        Always use PreparedStatement when a query includes values from variables or from users. You write the SQL with ? placeholders and set the values with setInt, setString and so on. The driver sends the data separately from the SQL, so a value can never change the meaning of the command. Building SQL by gluing strings together invites SQL injection. Connections, statements and result sets all need closing, so use try-with-resources.

                        The example uses H2, a small database that can run inside your program in memory. To run it, add the H2 dependency (com.h2database:h2) to a practice Maven project. The in-memory database disappears when the program ends. This dependency is not part of CodeNest itself.
                        """,
                        List.of(ex("Insert and query with PreparedStatement (H2 in memory)", """
                                import java.sql.Connection;
                                import java.sql.DriverManager;
                                import java.sql.PreparedStatement;
                                import java.sql.ResultSet;
                                import java.sql.SQLException;
                                import java.sql.Statement;

                                public class JdbcDemo {
                                    public static void main(String[] args) {
                                        String url = "jdbc:h2:mem:codenest";

                                        try (Connection conn = DriverManager.getConnection(url, "sa", "")) {

                                            try (Statement st = conn.createStatement()) {
                                                st.execute("CREATE TABLE student (id INT PRIMARY KEY, name VARCHAR(50), marks INT)");
                                            }

                                            String insert = "INSERT INTO student (id, name, marks) VALUES (?, ?, ?)";
                                            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                                                ps.setInt(1, 1);
                                                ps.setString(2, "Asha");
                                                ps.setInt(3, 88);
                                                ps.executeUpdate();

                                                ps.setInt(1, 2);
                                                ps.setString(2, "Ravi");
                                                ps.setInt(3, 74);
                                                ps.executeUpdate();
                                            }

                                            String query = "SELECT id, name, marks FROM student WHERE marks > ? ORDER BY id";
                                            try (PreparedStatement ps = conn.prepareStatement(query)) {
                                                ps.setInt(1, 70);
                                                try (ResultSet rs = ps.executeQuery()) {
                                                    while (rs.next()) {
                                                        System.out.println(rs.getInt("id") + " " + rs.getString("name") + " " + rs.getInt("marks"));
                                                    }
                                                }
                                            }
                                        } catch (SQLException e) {
                                            System.out.println("Database error: " + e.getMessage());
                                        }
                                    }
                                }
                                """, """
                                1 Asha 88
                                2 Ravi 74
                                """)),
                        List.of("JDBC column and parameter numbers start at 1, not 0.",
                                "Modern JDBC drivers register themselves automatically; you no longer call Class.forName."),
                        List.of("Building SQL with string concatenation and user input, which allows SQL injection.",
                                "Forgetting to close connections, which eventually exhausts the database.",
                                "Counting parameters from 0 instead of 1."),
                        List.of("SQL fundamentals", "Database connectivity")),

                lesson("SQL fundamentals", 16,
                        List.of("Create tables and insert, select, update and delete rows",
                                "Filter, sort, group and join data"),
                        """
                        SQL (Structured Query Language) is the language of relational databases, where data lives in tables of rows and columns. CREATE TABLE defines a table and its column types, and a PRIMARY KEY column uniquely identifies each row. INSERT adds rows, SELECT reads them, UPDATE changes them and DELETE removes them.

                        SELECT is the workhorse. WHERE filters rows before grouping, ORDER BY sorts the result (DESC for descending), and GROUP BY groups rows so that aggregate functions such as COUNT, SUM, MIN, MAX and AVG can summarise each group. A JOIN combines rows from two tables that are related by matching columns, usually a foreign key that points to another table's primary key.

                        Be careful with UPDATE and DELETE: without a WHERE clause they affect every row in the table. Write the WHERE first, and test it with a SELECT.
                        """,
                        List.of(ex("Filtering, sorting and grouping", """
                                CREATE TABLE student (id INT PRIMARY KEY, name VARCHAR(50), dept VARCHAR(20), marks INT);

                                INSERT INTO student VALUES (1, 'Asha', 'CS', 88);
                                INSERT INTO student VALUES (2, 'Ravi', 'CS', 74);
                                INSERT INTO student VALUES (3, 'Meena', 'IT', 91);
                                INSERT INTO student VALUES (4, 'Kiran', 'IT', 65);

                                SELECT name, marks FROM student WHERE marks >= 75 ORDER BY marks DESC;

                                SELECT dept, COUNT(*) AS students, MAX(marks) AS best
                                FROM student GROUP BY dept ORDER BY dept;
                                """, """
                                name  | marks
                                Meena | 91
                                Asha  | 88

                                dept | students | best
                                CS   | 2        | 88
                                IT   | 2        | 91
                                """),
                                ex("A join, an update and a delete", """
                                CREATE TABLE department (code VARCHAR(20) PRIMARY KEY, title VARCHAR(50));
                                INSERT INTO department VALUES ('CS', 'Computer Science');
                                INSERT INTO department VALUES ('IT', 'Information Technology');

                                SELECT s.name, d.title
                                FROM student s JOIN department d ON s.dept = d.code
                                WHERE s.marks > 80
                                ORDER BY s.name;

                                UPDATE student SET marks = 70 WHERE name = 'Kiran';
                                DELETE FROM student WHERE marks < 72;

                                SELECT COUNT(*) AS remaining FROM student;
                                """, """
                                name  | title
                                Asha  | Computer Science
                                Meena | Information Technology

                                remaining
                                3
                                """)),
                        List.of("The tables above are drawn as plain text. Your database tool will format results its own way.",
                                "In the second example Kiran's marks become 70, so the DELETE removes only Ravi (74 stays, 70 is below 72, so Kiran is removed). Ravi is kept because 74 is not below 72. Count: Asha, Ravi and Meena remain."),
                        List.of("Running UPDATE or DELETE without a WHERE clause.",
                                "Using = NULL instead of IS NULL.",
                                "Putting an aggregate function such as COUNT in a WHERE clause. Conditions on groups belong in HAVING."),
                        List.of("JDBC fundamentals", "Database connectivity")),

                lesson("Database connectivity", 14,
                        List.of("Build a JDBC connection URL and keep credentials out of source code",
                                "Group several statements into one transaction"),
                        """
                        A JDBC URL tells the driver where the database is, for example jdbc:postgresql://localhost:5432/shop or jdbc:h2:mem:test. The host, port and database name belong in configuration, and so do the user name and password. Read them from environment variables or a configuration file instead of writing them in the source code, where they would end up in version control.

                        Opening a connection is slow, so real applications use a connection pool (HikariCP is the default in Spring Boot) that keeps a set of ready connections and lends them out. Not all data is relational: CodeNest itself stores its data in MongoDB, which is a document database reached through its own driver with a connection string such as mongodb://localhost:27017/codenest.

                        A transaction groups several statements so that either all of them take effect or none do. By default JDBC commits each statement on its own (auto-commit). Call setAutoCommit(false), run your statements, then commit(); if something fails, call rollback(). The classic example is moving money between two accounts, where both updates must succeed together.
                        """,
                        List.of(ex("Connecting with settings from the environment", """
                                import java.sql.Connection;
                                import java.sql.DatabaseMetaData;
                                import java.sql.DriverManager;
                                import java.sql.SQLException;

                                public class ConnectDemo {
                                    public static void main(String[] args) {
                                        String url = System.getenv().getOrDefault("DB_URL", "jdbc:h2:mem:test");
                                        String user = System.getenv().getOrDefault("DB_USER", "sa");
                                        String password = System.getenv().getOrDefault("DB_PASSWORD", "");

                                        try (Connection conn = DriverManager.getConnection(url, user, password)) {
                                            System.out.println("Connected: " + conn.isValid(2));
                                            DatabaseMetaData meta = conn.getMetaData();
                                            System.out.println("Product: " + meta.getDatabaseProductName());
                                        } catch (SQLException e) {
                                            System.out.println("Could not connect: " + e.getMessage());
                                        }
                                    }
                                }
                                """, """
                                Connected: true
                                Product: H2
                                """),
                                ex("A transaction that moves money", """
                                import java.sql.Connection;
                                import java.sql.DriverManager;
                                import java.sql.ResultSet;
                                import java.sql.SQLException;
                                import java.sql.Statement;

                                public class TransactionDemo {
                                    public static void main(String[] args) throws SQLException {
                                        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:bank", "sa", "")) {

                                            try (Statement st = conn.createStatement()) {
                                                st.execute("CREATE TABLE account (id INT PRIMARY KEY, balance INT)");
                                                st.execute("INSERT INTO account VALUES (1, 500)");
                                                st.execute("INSERT INTO account VALUES (2, 100)");
                                            }

                                            conn.setAutoCommit(false);
                                            try (Statement st = conn.createStatement()) {
                                                st.executeUpdate("UPDATE account SET balance = balance - 200 WHERE id = 1");
                                                st.executeUpdate("UPDATE account SET balance = balance + 200 WHERE id = 2");
                                                conn.commit();
                                            } catch (SQLException e) {
                                                conn.rollback();
                                                throw e;
                                            }

                                            try (Statement st = conn.createStatement();
                                                 ResultSet rs = st.executeQuery("SELECT id, balance FROM account ORDER BY id")) {
                                                while (rs.next()) {
                                                    System.out.println(rs.getInt("id") + ": " + rs.getInt("balance"));
                                                }
                                            }
                                        }
                                    }
                                }
                                """, """
                                1: 300
                                2: 300
                                """)),
                        List.of("Both examples need the H2 driver on the classpath. For a real server database, change the URL and add that database's driver instead.",
                                "Set the variables DB_URL, DB_USER and DB_PASSWORD in your IDE's run configuration to connect somewhere else."),
                        List.of("Hard-coding passwords in source files.",
                                "Leaving auto-commit on for operations that must succeed or fail together.",
                                "Opening a new connection for every small query instead of using a pool."),
                        List.of("JDBC fundamentals", "Introduction to Spring Boot")),

                lesson("Maven fundamentals", 14,
                        List.of("Explain what Maven does and the structure of a pom.xml",
                                "Use the main Maven commands and add a dependency"),
                        """
                        Maven is a build tool. It describes your project in a file called pom.xml, downloads the libraries your project needs, compiles the code, runs tests and packages the result into a JAR. Every project and every library is identified by its coordinates: a groupId (usually a reversed domain name such as com.example), an artifactId (the project name) and a version. To use a library, you add a dependency with those three values, and Maven downloads it, and the libraries that it needs, from a repository on the internet into a local cache.

                        Maven expects a standard folder layout: source code in src/main/java, resources such as application.properties in src/main/resources, and test code in src/test/java. Compiled output goes into the target folder. Work is organised in a lifecycle of phases: validate, compile, test, package, verify, install and deploy. Running a phase also runs all phases before it, so mvn package compiles, tests and then packages.
                        """,
                        List.of(ex("A minimal pom.xml with one dependency (result of mvn package)", """
                                <project xmlns="http://maven.apache.org/POM/4.0.0">
                                    <modelVersion>4.0.0</modelVersion>

                                    <groupId>com.example</groupId>
                                    <artifactId>demo</artifactId>
                                    <version>1.0.0</version>
                                    <packaging>jar</packaging>

                                    <properties>
                                        <maven.compiler.release>21</maven.compiler.release>
                                    </properties>

                                    <dependencies>
                                        <dependency>
                                            <groupId>com.google.code.gson</groupId>
                                            <artifactId>gson</artifactId>
                                            <version>2.11.0</version>
                                        </dependency>
                                    </dependencies>
                                </project>
                                """, """
                                mvn package creates the file: target/demo-1.0.0.jar
                                """),
                                ex("The commands you will use most", """
                                mvn -v
                                mvn clean package
                                mvn dependency:tree
                                """, """
                                Apache Maven 3.9.x ... (your version may differ)
                                ...
                                [INFO] BUILD SUCCESS
                                """)),
                        List.of("In IntelliJ IDEA, right-click pom.xml and choose Maven, then Reload project after you edit the dependencies.",
                                "Spring Boot projects usually inherit from spring-boot-starter-parent, which manages compatible versions of hundreds of libraries for you."),
                        List.of("Putting source files outside src/main/java, so Maven does not compile them.",
                                "Forgetting to reload the Maven project after editing pom.xml.",
                                "Mixing incompatible versions of related libraries."),
                        List.of("Introduction to Spring", "Introduction to Spring Boot")),

                lesson("Introduction to Spring", 16,
                        List.of("Explain inversion of control and dependency injection",
                                "Describe beans and the Spring container"),
                        """
                        Spring is a framework that helps you build applications from loosely connected parts. Its core idea is dependency injection (DI): instead of a class creating the objects it needs with new, those objects (its dependencies) are given to it from outside, usually through the constructor. The class then depends only on an interface, which makes it easy to swap implementations and to test.

                        Spring takes this one step further with inversion of control (IoC). You describe your classes, and the Spring container creates the objects (called beans), works out what each needs, and wires them together. A class becomes a bean when you annotate it, for example with @Component, @Service or @Repository; @Configuration marks a class that defines beans, and @ComponentScan tells Spring which packages to search.

                        The first example does the wiring by hand, so you can see the idea in plain Java. The second lets Spring do it. It needs the spring-context library added to a practice Maven project.
                        """,
                        List.of(ex("Dependency injection by hand", """
                                interface MessageService {
                                    String message();
                                }

                                class EmailService implements MessageService {
                                    public String message() {
                                        return "Email sent";
                                    }
                                }

                                class Notifier {
                                    private final MessageService service;

                                    Notifier(MessageService service) {
                                        this.service = service;
                                    }

                                    void notifyUser() {
                                        System.out.println(service.message());
                                    }
                                }

                                public class ManualInjection {
                                    public static void main(String[] args) {
                                        MessageService service = new EmailService();
                                        Notifier notifier = new Notifier(service);
                                        notifier.notifyUser();
                                    }
                                }
                                """, """
                                Email sent
                                """),
                                ex("The same wiring done by the Spring container", """
                                package demo;

                                import org.springframework.context.annotation.AnnotationConfigApplicationContext;
                                import org.springframework.context.annotation.ComponentScan;
                                import org.springframework.context.annotation.Configuration;
                                import org.springframework.stereotype.Component;

                                interface MessageService {
                                    String message();
                                }

                                @Component
                                class EmailService implements MessageService {
                                    public String message() {
                                        return "Email sent";
                                    }
                                }

                                @Component
                                class Notifier {
                                    private final MessageService service;

                                    Notifier(MessageService service) {
                                        this.service = service;
                                    }

                                    void notifyUser() {
                                        System.out.println(service.message());
                                    }
                                }

                                @Configuration
                                @ComponentScan
                                public class SpringDemo {
                                    public static void main(String[] args) {
                                        try (AnnotationConfigApplicationContext context =
                                                     new AnnotationConfigApplicationContext(SpringDemo.class)) {
                                            context.getBean(Notifier.class).notifyUser();
                                        }
                                    }
                                }
                                """, """
                                Email sent
                                """)),
                        List.of("Notice that Notifier never says new EmailService(). Spring found the only MessageService bean and passed it to the constructor.",
                                "Constructor injection is the recommended style because the dependencies are explicit and can be final."),
                        List.of("Creating dependencies with new inside the class, which defeats the purpose.",
                                "Placing Spring classes in the default package. Use a named package so component scanning works."),
                        List.of("Interfaces", "Introduction to Spring Boot")),

                lesson("Introduction to Spring Boot", 16,
                        List.of("Explain what Spring Boot adds on top of Spring",
                                "Create a minimal web application and call it"),
                        """
                        Spring Boot makes Spring applications quick to start by removing most of the configuration. It provides starter dependencies: one entry such as spring-boot-starter-web brings in a compatible set of libraries, here the web framework and an embedded Tomcat server. It applies auto-configuration: based on what is on the classpath, Boot configures sensible defaults for you, such as a web server on port 8080 or a database connection. And it packages your application as a single JAR that you start with java -jar, with no external server to install.

                        An application has a main class annotated with @SpringBootApplication, which combines @Configuration, @EnableAutoConfiguration and @ComponentScan. Settings live in src/main/resources/application.properties (or application.yml), for example server.port=9090. Spring Boot 3 requires Java 17 or newer, and CodeNest, the application you are using right now, is built exactly this way.

                        The example needs the spring-boot-starter-web dependency in a Spring Boot Maven project.
                        """,
                        List.of(ex("A complete Spring Boot application (response of GET /hello)", """
                                package com.example.demo;

                                import org.springframework.boot.SpringApplication;
                                import org.springframework.boot.autoconfigure.SpringBootApplication;
                                import org.springframework.web.bind.annotation.GetMapping;
                                import org.springframework.web.bind.annotation.RestController;

                                @SpringBootApplication
                                @RestController
                                public class DemoApplication {

                                    @GetMapping("/hello")
                                    String hello() {
                                        return "Hello from Spring Boot";
                                    }

                                    public static void main(String[] args) {
                                        SpringApplication.run(DemoApplication.class, args);
                                    }
                                }
                                """, """
                                $ curl http://localhost:8080/hello
                                Hello from Spring Boot
                                """)),
                        List.of("Run the main method, wait for the log line saying the web server started, then open http://localhost:8080/hello in a browser.",
                                "start.spring.io can generate a ready-made project with the dependencies you choose."),
                        List.of("Placing the main class in a package that does not contain your other classes, so component scanning misses them.",
                                "Starting two applications on the same port, which fails with a port-already-in-use error."),
                        List.of("Introduction to Spring", "REST API fundamentals")),

                lesson("REST API fundamentals", 16,
                        List.of("Describe REST principles, HTTP methods and status codes",
                                "Design a small REST API and implement it in Spring Boot"),
                        """
                        A REST API exposes resources over HTTP. A resource is a thing such as a task or a user, identified by a URL like /api/tasks/7. You act on resources with the standard HTTP methods: GET reads, POST creates, PUT replaces or updates, and DELETE removes. Names in URLs should be plural nouns, not verbs: POST /api/tasks, not /api/createTask. Requests and responses usually carry JSON, and the server is stateless: each request contains everything needed to handle it.

                        The status code of the response tells the client what happened: 200 OK, 201 Created (a new resource was made), 204 No Content (success with nothing to return), 400 Bad Request (invalid input), 401 Unauthorized (not signed in), 403 Forbidden (signed in but not allowed), 404 Not Found and 500 Internal Server Error. GET, PUT and DELETE are meant to be idempotent: repeating the same request leaves the server in the same state, which makes retries safe.

                        In Spring Boot, a @RestController class maps methods to URLs with @GetMapping, @PostMapping and @DeleteMapping. @PathVariable reads a value from the URL, @RequestBody converts the JSON body into an object, and ResponseEntity lets you choose the status code. CodeNest's own API follows these ideas.
                        """,
                        List.of(ex("A small tasks API (an example HTTP session)", """
                                package com.example.demo;

                                import org.springframework.http.HttpStatus;
                                import org.springframework.http.ResponseEntity;
                                import org.springframework.web.bind.annotation.DeleteMapping;
                                import org.springframework.web.bind.annotation.GetMapping;
                                import org.springframework.web.bind.annotation.PathVariable;
                                import org.springframework.web.bind.annotation.PostMapping;
                                import org.springframework.web.bind.annotation.RequestBody;
                                import org.springframework.web.bind.annotation.RequestMapping;
                                import org.springframework.web.bind.annotation.RestController;

                                import java.util.ArrayList;
                                import java.util.List;
                                import java.util.Map;
                                import java.util.concurrent.ConcurrentHashMap;
                                import java.util.concurrent.atomic.AtomicInteger;

                                @RestController
                                @RequestMapping("/api/tasks")
                                public class TaskController {

                                    public record Task(int id, String title) {
                                    }

                                    private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
                                    private final AtomicInteger nextId = new AtomicInteger(1);

                                    @GetMapping
                                    List<Task> all() {
                                        return new ArrayList<>(tasks.values());
                                    }

                                    @GetMapping("/{id}")
                                    ResponseEntity<Task> one(@PathVariable int id) {
                                        Task task = tasks.get(id);
                                        if (task == null) {
                                            return ResponseEntity.notFound().build();
                                        }
                                        return ResponseEntity.ok(task);
                                    }

                                    @PostMapping
                                    ResponseEntity<Task> create(@RequestBody Map<String, String> body) {
                                        Task task = new Task(nextId.getAndIncrement(), body.getOrDefault("title", "untitled"));
                                        tasks.put(task.id(), task);
                                        return ResponseEntity.status(HttpStatus.CREATED).body(task);
                                    }

                                    @DeleteMapping("/{id}")
                                    ResponseEntity<Void> delete(@PathVariable int id) {
                                        if (tasks.remove(id) == null) {
                                            return ResponseEntity.notFound().build();
                                        }
                                        return ResponseEntity.noContent().build();
                                    }
                                }
                                """, """
                                POST   /api/tasks    {"title":"Learn REST"}   ->  201 Created     {"id":1,"title":"Learn REST"}
                                GET    /api/tasks/1                           ->  200 OK          {"id":1,"title":"Learn REST"}
                                GET    /api/tasks/9                           ->  404 Not Found
                                DELETE /api/tasks/1                           ->  204 No Content
                                """)),
                        List.of("This example keeps tasks in memory, so they disappear when the application stops. A real API would store them in a database.",
                                "A real API also validates the input and returns 400 for a missing title instead of defaulting it."),
                        List.of("Using verbs in URLs, such as /getAllTasks.",
                                "Returning 200 for everything, including errors, which hides failures from clients.",
                                "Changing data with a GET request, which breaks the safety that caches and browsers assume."),
                        List.of("Introduction to Spring Boot", "Database connectivity"))
        );

        QuizSeed quiz = new QuizSeed("Database and Backend Foundations Quiz",
                "Test what you know about JDBC, SQL, Maven, Spring and REST.",
                List.of(
                        q("Why should you use PreparedStatement with placeholders instead of joining strings into SQL?", 1,
                                "Values are sent separately from the SQL text, so input can never change the meaning of the command. That prevents SQL injection.",
                                "It makes the database faster in every case", "It prevents SQL injection by keeping data separate from SQL",
                                "It avoids needing a driver", "It lets you skip closing connections"),
                        q("Which SQL clause filters individual rows before they are grouped?", 0,
                                "WHERE filters rows before GROUP BY. HAVING filters the groups afterwards.",
                                "WHERE", "HAVING", "ORDER BY", "JOIN"),
                        q("Which three values identify a library in Maven?", 2,
                                "A dependency is identified by its groupId, artifactId and version.",
                                "name, path, size", "class, method, package", "groupId, artifactId, version", "author, license, date"),
                        q("What does dependency injection mean?", 3,
                                "A class receives the objects it needs from outside, usually through its constructor, instead of creating them itself.",
                                "Copying all dependencies into one file", "Downloading libraries at run time",
                                "Making every field public", "A class receives the objects it needs from outside instead of creating them"),
                        q("Which HTTP status code should a server return after successfully creating a resource with POST?", 1,
                                "201 Created tells the client that a new resource was made. 200 means a generic success and 204 means success with no content.",
                                "200", "201", "204", "404"),
                        q("Which Maven command compiles the project and packages it into a JAR?", 2,
                                "mvn package runs the lifecycle up to the packaging phase, so it compiles (and tests) before creating the JAR.",
                                "mvn start", "mvn run", "mvn package", "mvn jar")
                ));

        List<PracticeSeed> practiceSet = List.of(

                practice("Write the query", "EASY",
                        "A table student(id, name, dept, marks) holds these rows: (1, Asha, CS, 88), (2, Ravi, CS, 74), (3, Meena, IT, 91), (4, Kiran, IT, 65). Write one SQL query that returns the names of students with more than 80 marks, in alphabetical order.",
                        """
                        -- Table: student(id, name, dept, marks)
                        -- Write your query below:

                        """,
                        "Use SELECT name FROM student, filter with WHERE marks > 80, and add ORDER BY name.",
                        """
                        SELECT name
                        FROM student
                        WHERE marks > 80
                        ORDER BY name;
                        """,
                        """
                        name
                        Asha
                        Meena
                        """),

                practice("Insert and count with JDBC", "MEDIUM",
                        "Using the in-memory H2 database (add the H2 dependency to a practice project), create a table book(id INT PRIMARY KEY, title VARCHAR(100)), insert two books with a PreparedStatement, and print Books: followed by the number of rows.",
                        """
                        import java.sql.Connection;
                        import java.sql.DriverManager;
                        import java.sql.SQLException;

                        public class BookCount {
                            public static void main(String[] args) {
                                String url = "jdbc:h2:mem:library";
                                try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
                                    // TODO: create the table, insert two books, count the rows
                                    // and print "Books: <count>"
                                } catch (SQLException e) {
                                    System.out.println("Database error: " + e.getMessage());
                                }
                            }
                        }
                        """,
                        "Run CREATE TABLE with a Statement, reuse one PreparedStatement for both INSERTs, then run SELECT COUNT(*) and read it with rs.getInt(1).",
                        """
                        import java.sql.Connection;
                        import java.sql.DriverManager;
                        import java.sql.PreparedStatement;
                        import java.sql.ResultSet;
                        import java.sql.SQLException;
                        import java.sql.Statement;

                        public class BookCount {
                            public static void main(String[] args) {
                                String url = "jdbc:h2:mem:library";
                                try (Connection conn = DriverManager.getConnection(url, "sa", "")) {

                                    try (Statement st = conn.createStatement()) {
                                        st.execute("CREATE TABLE book (id INT PRIMARY KEY, title VARCHAR(100))");
                                    }

                                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO book (id, title) VALUES (?, ?)")) {
                                        ps.setInt(1, 1);
                                        ps.setString(2, "Effective Java");
                                        ps.executeUpdate();

                                        ps.setInt(1, 2);
                                        ps.setString(2, "Clean Code");
                                        ps.executeUpdate();
                                    }

                                    try (Statement st = conn.createStatement();
                                         ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM book")) {
                                        rs.next();
                                        System.out.println("Books: " + rs.getInt(1));
                                    }
                                } catch (SQLException e) {
                                    System.out.println("Database error: " + e.getMessage());
                                }
                            }
                        }
                        """,
                        "Books: 2"),

                practice("Design a REST API", "MEDIUM",
                        "A library app manages books. List the REST endpoints (HTTP method, URL and success status code) for: listing all books, reading one book, adding a book, replacing a book, and deleting a book. Use /api/books as the base path.",
                        """
                        # Fill in one line per operation:
                        # METHOD  URL  ->  success status

                        """,
                        "Use plural nouns in the URL, put the id in the path for single-book operations, and remember which operations return 201 and 204.",
                        """
                        GET     /api/books        ->  200 OK
                        GET     /api/books/{id}   ->  200 OK
                        POST    /api/books        ->  201 Created
                        PUT     /api/books/{id}   ->  200 OK
                        DELETE  /api/books/{id}   ->  204 No Content
                        """,
                        """
                        GET     /api/books        ->  200 OK
                        GET     /api/books/{id}   ->  200 OK
                        POST    /api/books        ->  201 Created
                        PUT     /api/books/{id}   ->  200 OK
                        DELETE  /api/books/{id}   ->  204 No Content
                        """)
        );

        return new ModuleSeed("Database and Backend Foundations",
                "Connect Java to databases with JDBC and SQL, manage projects with Maven, and meet Spring, Spring Boot and REST APIs.",
                lessons, quiz, practiceSet);
    }

}