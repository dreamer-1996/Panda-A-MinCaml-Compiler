let rec f x = x +1 in
let y = 2 in
let z = f y in
if not(z = 4) then print_int z else print_int (f 5)
