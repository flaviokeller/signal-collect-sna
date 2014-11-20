Social Network Analysis with Signal/Collect
========================================================================================================================

Signal/Collect is a framework for computations on large graphs. The model allows to concisely express many iterated and data-flow algorithms, while the framework parallelizes and distributes the computation.

This repository contains an implementation of standard social network analysis metrics such as PageRank or Betweenness Centrality and calculations of graph properties like density, reciprocity or diameter using the pre-release snapshots of the distributed 2.1 version of Signal/Collect.

How to Compile the Project and develop in Eclipse
--------------------------
Download the [signal-collect](https://github.com/uzh/signal-collect) project and this project to your computer and put them both in the same folder.

Ensure Java 8 is available on the system, verify with "java -version" on the command line.
Additionally, [Download and Install SBT] (http://www.scala-sbt.org/download.html)

Navigate to the signal-collect project folder and start SBT on the command line. The output should end with: "[info] Set current project to signal-collect (in build file:XYZ/signal-collect/)".

Compile the signal-collect project by using the "compile" command on the SBT prompt.

Navigate to the signal-collect-sna project folder and start SBT on the command line. The output should end with: "[info] Set current project to signal-collect (in build file:XYZ/signal-collect-sna/)".

Compile the signal-collect-sna project by using the "compile" command on the SBT prompt. If a "Cannot run program "javac"" exception occurs, try to set your JAVA_HOME environment variable to a jdk, since the sna project also contains Java code which has to be compiled.

To generate a .jar file with dependencies, use the "assembly" command on the SBT prompt.

To generate an Eclipse project, use the "eclipse" command on the SBT prompt.

Please refer as well to the readme of the [Signal/Collect repository](https://github.com/uzh/signal-collect)

Thanks a lot to
---------------
* [University of Zurich](http://www.ifi.uzh.ch/ddis.html) and the [Hasler Foundation](http://www.haslerstiftung.ch/en/home) are generously funding our research on graph processing and the development of Signal/Collect.
* GitHub helps us by hosting our [code repositories](https://github.com/uzh/signal-collect).