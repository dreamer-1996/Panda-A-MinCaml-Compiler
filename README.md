# Panda: A MinCaml Compiler written in Java

## Build Dependencies 
- Java (>= 1.8)
- Maven (Install [here](https://maven.apache.org/install.html))

## How to compile
In order to compile the Java classes and generate the lexers and parsers, simply enter:
```
make
```

## How to execute 

### From a `.ml` file, run:
```
./mincamlc awesome.ml
```

### From a `.asml` file, run:
```
./mincamlc -fromAsml awesome.ml
```

## How to test
### To run all tests
```
make test
```

### To run only frontend tests
```
make test_frontend
```

### To run only backend tests
```
make test_backend
```

## To generate Javadoc
```
make doc
```
The `.html` files are placed in `target/site/apidocs/org/panda`.

## Command line options

| Option    	| Value                 	| Description                                                                    	|
|-----------	|-----------------------	|--------------------------------------------------------------------------------	|
| -h        	| N/A                   	| Displays the help text                                                         	|
| -v        	| N/A                   	| Displays the current version                                                   	|
| -p        	| a `.ml` file          	| Parses the provided file and exits                                             	|
| -t        	| a `.ml` file          	| Does typechecking on provided file and exits                                   	|
| -d        	| a `.ml` file          	| Debug option that prints intermediate results from every pass of the compiler  	|
| -asml     	| a `.ml` file          	| Generates the .asml file (saved as `output.asml`) and exits                    	|
| -fromAsml 	| a `.asml` file        	| Executes the backend on the provided asml file                                 	|
| -arm      	| `.ml` or `.asml` file 	| Displays the result of ARM generation (use with `-o` to write to file)         	|
| -o        	| file                  	| Writes the result of ARM or asml generation to the specified file                             	|


## Current Features

### Frontend
- [x] arithmetic expressions
- [x] call to external functions
- [x] functions (`let rec`)
- [x] if-then-else
- [x] arrays
- [x] closures
- [x] tuples 
- [ ] floats

### Backend
- [x] arithmetic expressions
- [x] call to external functions
- [x] functions (`let rec`)
- [x] if-then-else
- [x] arrays
- [x] tuples
- [ ] closures
- [x] floats

