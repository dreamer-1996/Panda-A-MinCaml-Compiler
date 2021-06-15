let rec quad x = 
    let rec dbl y = y + y in dbl (dbl x)
in quad 123