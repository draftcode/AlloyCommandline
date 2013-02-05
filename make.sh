env LC_ALL= javac -cp alloy4.2.jar AlloyCommandline.java
env LC_ALL= jar -cvfm AlloyCommandline.jar manifest.mf AlloyCommandline\$1.class AlloyCommandline.class AlloyCommandline.java
