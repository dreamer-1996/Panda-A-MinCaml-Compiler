
all: compile

compile:
	mvn compile -q

clean:
	mvn clean -q

test: compile test_frontend test_backend

test_frontend: compile
	./scripts/mincaml-test-frontend.sh

test_backend: compile
	./scripts/mincaml-test-backend.sh

doc: clean
	mvn site -q
