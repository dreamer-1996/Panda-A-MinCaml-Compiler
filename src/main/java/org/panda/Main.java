package org.panda;

import org.panda.ast.*;
import org.panda.backend.BackendLexer;
import org.panda.backend.BackendParser;
import org.panda.backend.SpillAllocation;
import org.panda.frontend.*;

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Set;

public class Main {
    static String version = "1.0.0";
    static String inputFile;
    static String outputFile = null;

    static boolean outputAsml = false;
    static boolean outputArm = false;
    static boolean fromAsml = false;
    static boolean typeCheckOnly = false;
    static boolean parseOnly = false;
    static boolean debug = false;

    public static void main(String[] argv) throws Exception {
        if (argv.length < 1) {
            help();
            System.exit(0);
        }

        int argCount = -1;
        while (argv.length - 1 > argCount) {
            argCount++;
            switch (argv[argCount]) {
                case "-h":
                    help();
                    System.exit(0);
                case "-o":
                    argCount++;
                    outputFile = argv[argCount];
                    break;
                case "-v":
                    System.out.printf("Version %s%n", version);
                    System.exit(0);
                case "-t":
                    typeCheckOnly = true;
                    break;
                case "-p":
                    parseOnly = true;
                    break;
                case "-d":
                    debug = true;
                    break;
                case "-asml":
                    outputAsml = true;
                    break;
                case "-arm":
                    outputArm = true;
                    break;
                case "-fromAsml":
                    fromAsml = true;
                    break;
                default:
                    if (argCount != argv.length - 1) {
                        System.err.printf("Unrecognized option %s%n", argv[argCount]);
                        help();
                        System.exit(1);
                    }
                    break;
            }
        }

        inputFile = argv[argCount];

        Program program;
        if (fromAsml) {
            program = parseAsml(inputFile);
        } else {
            Exp mlTree = parseMl(inputFile);
            program = processMl(mlTree);
        }

        if (debug) {
            System.out.println("------ Backend------");
            System.out.println();
        }

        SpillAllocation sa = new SpillAllocation();
        String code = sa.genARM(program);
        if (outputArm) {
            if (outputFile != null) {
                try {
                    PrintWriter out = new PrintWriter(outputFile);
                    out.println(code);
                    out.close();
                } catch (Exception e) {
                    System.out.printf("FileNotFound %s%n", outputFile);
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                if (debug) {
                    System.out.println(code);
                }
            }
        }

        System.exit(0);
    }

    static public Exp parseMl(String mlFile) {
        Exp expression = null;
        try {
            FrontendParser p = new FrontendParser(new FrontendLexer(new FileReader(inputFile)));
            expression = (Exp) p.parse().value;
            assert (expression != null);
        } catch (Exception e) {
            System.out.printf("Unable to parse ml file %s%n", mlFile);
            e.printStackTrace();
            System.exit(1);
        }

        if (parseOnly) {
            System.out.printf("Parsed file '%s' successfully%n", inputFile);
            System.exit(0);
        }

        return expression;
    }

    static public Program processMl(Exp mlTree) throws Exception {

        if (debug) {
            System.out.println("------ AST ------");
            mlTree.accept(new PrintVisitor());

            System.out.println();
            System.out.println();

            System.out.println("------ Set of variables ----");
            ObjVisitor<Set<String>> s = new VariableVisitor();
            Set<String> vars = mlTree.accept(s);
            System.out.println(vars);

            System.out.println();
            System.out.println();
        }

        doTypeChecking(mlTree);


        if (debug) {
            System.out.println();
            System.out.println();
            System.out.println("------ KNormalization -----");
        }

        ObjVisitor<Exp> k = new KNormalizationVisitor();
        Exp knormalizedExpression = mlTree.accept(k);

        if (debug) {
            knormalizedExpression.accept(new PrintVisitor());
            System.out.println();
            System.out.println();

            System.out.println("------ Alpha Conversion ----");
        }

        ObjVisitor<Exp> a = new AlphaConversionVisitor();
        Exp alphaExpression = knormalizedExpression.accept(a);

        if (debug) {
            alphaExpression.accept(new PrintVisitor());

            System.out.println();
            System.out.println();

            System.out.println("------ Let Reduction ------");
        }

        ObjVisitor<Exp> l = new LetReductionVisitor();
        Exp letReducedExpression = alphaExpression.accept(l);

        if (debug) {
            letReducedExpression.accept(new PrintVisitor());

            System.out.println();
            System.out.println();

            System.out.println("------ Closures (ASML) ------");
            System.out.println();
        }

        ClosuresVisitor c = new ClosuresVisitor();
        Exp closureExpression = letReducedExpression.accept(c);
        org.panda.ast.Program program = c.getProgram(closureExpression);

        if (outputAsml) {
            if (outputFile != null) {
                c.generateAsmlFile(outputFile);
            }else {
                c.printProgram();
            }
            System.exit(0);
        } else {
            if (debug) {
                c.printProgram();
            }
        }


        if (debug) {
            System.out.println();
            System.out.println();
        }

        return program;
    }

    private static void doTypeChecking(Exp mlTree) throws Exception {
        try {
            if (debug) {
                System.out.println("------ TypeChecking ------");
            }
            ObjVisitor<Type> x = new TypeCheckVisitor();
            mlTree.accept(x);
            if (debug) {
                System.out.println("Program is properly typed!");
            }

            if (typeCheckOnly) {
                System.exit(0);
            }
        } catch (TypeException e) {
            if (debug) {
                System.out.println("Program is not properly typed %n");
            }
        } catch (NullPointerException e) {
            if (debug) {
                System.out.println("Cannot typecheck this program: Unimplemented types");
            }
        }
    }

    static public Program parseAsml(String asmlFile) {
        Program prog = null;
        try {
            BackendParser bp = new BackendParser(new BackendLexer(new FileReader(asmlFile)));
            prog = (Program) bp.parse().value;
            assert (prog != null);
        } catch (Exception e) {
            System.out.printf("Unable to parse asml file %s%n", asmlFile);
            e.printStackTrace();
            System.exit(1);
        }
        return prog;
    }

    static public void help() {
        System.out.println(
                "Usage:\n" +
                        "  ./mincamlc [Options] [File_Name]\n" +
                        "Options:\n" +
                        "  -o [OutputFile] \"Outputs to file\"\n" +
                        "  -h \"Display help\"\n" +
                        "  -v \"Display the current version\"\n" +
                        "  -t \"Type check only\"\n" +
                        "  -p \"Parse only\"\n" +
                        "  -d \"Print intermediate results for each pass \"\n" +
                        "  -fromAsml \"input file is in asml\"\n" +
                        "  -asml \"output .asml file\"\n" +
                        "  -arm \"output .s file\""
        );
    }

}

