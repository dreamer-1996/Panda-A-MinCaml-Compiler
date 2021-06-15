let x = Array.create 5 2 in let y = (x.(0) <- 3) in
print_int (x.(3))
