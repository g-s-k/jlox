BUILD_DIR := ./build
SRC_DIR := ./src

SRCS = $(wildcard $(SRC_DIR)/lox/*.java)

JFLAGS = -g -d $(BUILD_DIR)
JC = javac

all: $(SRCS)
	$(JC) $(JFLAGS) $(SRCS)

.PHONY: clean

clean:
	$(RM) -r $(BUILD_DIR)
