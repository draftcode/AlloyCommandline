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

    StringBuilder success_out = new StringBuilder();
    StringBuilder error_out = new StringBuilder();
    for (String filename : args) {
      Module world;
      try {
        world = CompUtil.parseEverything_fromFile(rep, null, filename);
      } catch (Exception e) {
        System.out.println(e);
        continue;
      }

      for (Command command: world.getAllCommands()) {
        A4Solution ans = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), command, options);
        if (command.check) {
          if (ans.satisfiable()) {
            error_out.append("Assertion error in " + command.pos.filename +
                " at line " + command.pos.y + " column " + command.pos.x + ":\n");
            error_out.append("Counter-example of " + command + " found.\n");
          } else {
            success_out.append("Maybe valid: " + command + "\n");
          }
        } else {
          if (ans.satisfiable()) {
            success_out.append("Consistent: " + command + "\n");
          } else {
            error_out.append("Inconsistent error in " + command.pos.filename +
                " at line " + command.pos.y + " column " + command.pos.x + ":\n");
            error_out.append("No instance of " + command + " found.\n");
          }
        }
      }
    }

    if (error_out.length() != 0) {
      System.err.print(error_out.toString());
    } else {
      System.out.print(success_out.toString());
    }
  }
}
