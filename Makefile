FILE ?=
BIN = bin

all: compile run

compile:
	javac -d $(BIN) $$(find src -name "*.java")

run:
	@if [ -z "$(FILE)" ]; then java -cp "$(BIN)" com.JLox.Main; else java -cp "$(BIN)" com.JLox.Main "$(FILE)"; fi

clean:
	rm -rf $(BIN)
