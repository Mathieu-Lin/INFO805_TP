DATA SEGMENT
	x DD
	y DD
	pgcd DD
	z DD
DATA ENDS
CODE SEGMENT
	in eax
	mov x, eax
	in eax
	mov y, eax
	lea eax, lambda_0
	mov pgcd, eax
	mov eax, pgcd
	push eax
	mov eax, x
	push eax
	mov eax, y
	push eax
	mov eax, 8[esp]
	call eax
	add esp, 8
	pop eax
	out eax
	mov z, eax
	mov eax, z
	out eax
	jmp end_pg_4
lambda_0:
	enter 0
	mov eax, 0
	push eax
	mov eax, 8[ebp]
	pop ebx
	sub ebx, eax
	jl vrai_jl_2
	mov eax, 0
	jmp fin_jl_2
vrai_jl_2:
	mov eax, 1
fin_jl_2:
	jz else_1
	mov eax, pgcd
	push eax
	mov eax, 8[ebp]
	push eax
	mov eax, 12[ebp]
	push eax
	mov eax, 8[ebp]
	pop ebx
	mov ecx, eax
	mov eax, ebx
	div ebx, ecx
	mul ebx, ecx
	sub eax, ebx
	push eax
	mov eax, 8[esp]
	call eax
	add esp, 8
	pop eax
	jmp fin_if_1
else_1:
	mov eax, 12[ebp]
fin_if_1:
	mov 16[ebp], eax
	leave
	ret
end_pg_4:
CODE ENDS
