	 .text
	 .global _start

_start:
	mov 	 fp, sp
	mov 	 r0, #0
	str 	 r0, [fp, #-4]
	mov 	 r0, #9
	str 	 r0, [fp, #-8]
	mov 	 r0, #26
	str 	 r0, [fp, #-12]
	ldr 	 r0, [fp, #-12]
	rsb 	 r0, r0, #0,
	str 	 r0, [fp, #-16]
	ldr 	 r1, [fp, #-8]
	add 	 r0, r1, #900
	str 	 r0, [fp, #-20]
	ldr 	 r1, [fp, #-4]
	ldr 	 r2, [fp, #-8]
	add 	 r0, r1, r2
	str 	 r0, [fp, #-24]
	ldr 	 r1, [fp, #-8]
	ldr 	 r2, [fp, #-16]
	add 	 r0, r1, r2
	str 	 r0, [fp, #-28]
	ldr 	 r1, [fp, #-16]
	ldr 	 r2, [fp, #-20]
	add 	 r0, r1, r2
	str 	 r0, [fp, #-32]
	ldr 	 r1, [fp, #-4]
	ldr 	 r2, [fp, #-8]
	sub 	 r0, r1, r2
	str 	 r0, [fp, #-36]
	ldr 	 r1, [fp, #-8]
	ldr 	 r2, [fp, #-16]
	sub 	 r0, r1, r2
	str 	 r0, [fp, #-40]
	ldr 	 r1, [fp, #-16]
	ldr 	 r2, [fp, #-20]
	sub 	 r0, r1, r2
	str 	 r0, [fp, #-44]
	ldr 	 r0, [fp, #-4]
	rsb 	 r0, r0, #0,
	str 	 r0, [fp, #-48]
	ldr 	 r0, [fp, #-8]
	rsb 	 r0, r0, #0,
	str 	 r0, [fp, #-52]
	ldr 	 r0, [fp, #-16]
	rsb 	 r0, r0, #0,
	str 	 r0, [fp, #-56]
	ldr 	 r1, [fp, #-52]
	ldr 	 r2, [fp, #-56]
	add 	 r0, r1, r2
	str 	 r0, [fp, #-60]
	ldr 	 r0, [fp, #-24]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-64]
	ldr 	 r0, [fp, #-28]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-68]
	ldr 	 r0, [fp, #-32]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-72]
	ldr 	 r0, [fp, #-36]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-76]
	ldr 	 r0, [fp, #-40]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-80]
	ldr 	 r0, [fp, #-44]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-84]
	ldr 	 r0, [fp, #-48]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-88]
	ldr 	 r0, [fp, #-52]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-92]
	ldr 	 r0, [fp, #-56]
	bl 	 min_caml_print_int
	str 	 r0, [fp, #-96]
	ldr 	 r0, [fp, #-60]
	bl 	 min_caml_print_int
	bl 	 min_caml_exit
