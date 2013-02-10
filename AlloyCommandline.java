/*
Copyright (c) 2013, Masaya SUZUKI <draftcode@gmail.com>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

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
        A4Solution ans = TranslateAlloyToKodkod.execute_command(
            rep, world.getAllReachableSigs(), command, options);
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
