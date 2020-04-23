BUILD_DIR := build
SRC_DIR := src

# where to find the files, so we can use shorter paths below
vpath %.class $(BUILD_DIR)
vpath %.java $(SRC_DIR)

# compilation strategy for all source files
JFLAGS = -g -Xlint -Xlint:-serial -d $(BUILD_DIR) -sourcepath $(SRC_DIR)
%.class: %.java | $(BUILD_DIR)
	javac $(JFLAGS) $<

# helper to run compiled artifacts
run//%: %.class
	java -cp $(BUILD_DIR) $(subst /,.,$*) $(ARGS)

# this is the main (and default) target
SRCS := $(wildcard $(SRC_DIR)/lox/*.java)
GENERATED := lox/Expr.java lox/Stmt.java
lox/Lox.class: $(GENERATED) $(SRCS) | tool/GenerateAst.class

# generated code requires building and running tool
$(GENERATED): tool/GenerateAst.class
	$(MAKE) run//tool/GenerateAst ARGS=$(SRC_DIR)/lox

# works without this on some platforms, fails on others
$(BUILD_DIR):
	mkdir $@
