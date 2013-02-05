import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

public final class AlloyCommandline {
  public static void main(String[] args) throws Err {
    A4Reporter rep = new A4Reporter() {
      @Override public void warning(ErrorWarning msg) {
        System.out.print("Relevance Warning:\n"+(msg.toString().trim())+"\n\n");
        System.out.flush();
      }
    };
    A4Options options = new A4Options();
    options.solver = A4Options.SatSolver.SAT4J;

    for (String filename : args) {
      Module world;
      try {
        world = CompUtil.parseEverything_fromFile(rep, null, filename);
      } catch (Exception e) {
        System.out.println(e);
        continue;
      }

      for (Command command: world.getAllCommands()) {
        if (command.check) {
          A4Solution ans = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), command, options);
          if (ans.satisfiable()) {
            System.out.println("Counter-example found: " + command);
          } else {
            System.out.println("Maybe valid: " + command);
          }
        }
      }
    }
  }
}
