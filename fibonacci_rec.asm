DATA SEGMENT
	n DD
	fib DD
DATA ENDS
CODE SEGMENT
	in eax
	mov n, eax
	lea eax, lambda_0
	mov fib, eax
	mov eax, fib
	push eax
	mov eax, n
	push eax
	mov eax, 4[esp]
	call eax
	add esp, 4
	pop eax
	out eax
	jmp end_pg_4
lambda_0:
	enter 0
	mov eax, 8[ebp]
	push eax
	mov eax, 2
	pop ebx
	sub ebx, eax
	jl vrai_jl_2
	mov eax, 0
	jmp fin_jl_2
vrai_jl_2:
	mov eax, 1
fin_jl_2:
	jz else_1
	mov eax, 8[ebp]
	jmp fin_if_1
else_1:
	mov eax, fib
	push eax
	mov eax, 8[ebp]
	push eax
	mov eax, 1
	pop ebx
	sub ebx, eax
	mov eax, ebx
	push eax
	mov eax, 4[esp]
	call eax
	add esp, 4
	pop eax
	push eax
	mov eax, fib
	push eax
	mov eax, 8[ebp]
	push eax
	mov eax, 2
	pop ebx
	sub ebx, eax
	mov eax, ebx
	push eax
	mov eax, 4[esp]
	call eax
	add esp, 4
	pop eax
	pop ebx
	add eax, ebx
fin_if_1:
	mov 12[ebp], eax
	leave
	ret
end_pg_4:
CODE ENDS
