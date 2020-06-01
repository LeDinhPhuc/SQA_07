all:
	java openjava.ojc.Main it/itc/etoc/BranchInstrumentor.oj
	java openjava.ojc.Main it/itc/etoc/DataFlowInstrumentor.oj
	javac it/itc/etoc/*.java

jar:
	jar cvf etoc.jar it/itc/etoc/*.class

distrib:
	tar czvf etoc.tgz it/itc/etoc/*.java it/itc/etoc/*.oj it/itc/etoc/*.class Makefile README.TXT examples/BinaryTree/ etoc.jar


