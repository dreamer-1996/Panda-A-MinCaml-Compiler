	 .text
	 .global main
f:
@args: [y, x]
	push 	 {fp}
	mov 	 fp, sp
	sub 	 sp, #16
	@^^ start of function ^^
	str 	 r1, [fp, #-4]
	str 	 r0, [fp, #-8]
	ldr 	 r1, [fp, #-8]
	ldr 	 r2, [fp, #-4]
	add 	 r0, r1, r2
	str 	 r0, [fp, #-12]
	mov 	 r0, #2
	str 	 r0, [fp, #-16]
	ldr 	 r1, [fp, #-16]
	ldr 	 r2, [fp, #-12]
	sub 	 r0, r1, r2
	@vv end of function vv
	mov 	 sp, fp
	pop 	 {fp}
	bx 	 lr

main:
	bl 	 _mincaml_init
	sub 	 sp, #12
	mov 	 r0, #0
	str 	 r0, [fp, #-4]
	mov 	 r0, #1
	str 	 r0, [fp, #-8]
	ldr 	 r1, [fp, #-8]
	ldr 	 r0, [fp, #-4]
	bl 	 f
	str 	 r0, [fp, #-12]
	ldr 	 r0, [fp, #-12]
	bl 	 min_caml_print_int
	bl 	 min_caml_exit
