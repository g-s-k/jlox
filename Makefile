BUILD_DIR := ./build
SRC_DIR := ./src

SRCS = $(wildcard $(SRC_DIR)/lox/*.java)

JFLAGS = -g -d $(BUILD_DIR)
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $<

all: $(SRCS:.java=.class)

.PHONY: clean

clean:
	$(RM) -r $(BUILD_DIR)
