BUILD_DIR := ./build

vpath %.java src

JFLAGS = -g -d $(BUILD_DIR) -Xlint
JC = javac

GENERATED = src/lox/Expr.java
SRCS := $(wildcard src/lox/*.java)
ifeq (,$(findstring $(GENERATED),$(SRCS)))
	SRCS += $(GENERATED)
endif

$(patsubst src/%.java, $(BUILD_DIR)/%.class, $(SRCS)): $(SRCS)
	$(JC) $(JFLAGS) $^

$(GENERATED): tool/GenerateAst.java
	$(JC) $(JFLAGS) $^
	java -cp $(BUILD_DIR) tool.GenerateAst src/lox
