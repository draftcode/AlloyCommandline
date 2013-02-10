Use Alloy Analyzer from Commandline
===================================

Run ``./make.sh`` and you get ``AlloyCommandline.jar``. Place it and ``alloy4.2.jar`` in the same directory.

To run your Alloy program, ``java -jar AlloyCommandline.jar your_program.als``, and it runs all of checks and runs in the file.

You might want to set ``errorformat`` in your vimrc like this: ``%E%m in %f at line %l column %c:``

