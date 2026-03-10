DATA SEGMENT
	n DD
	a DD
	b DD
	i DD
	aux DD
DATA ENDS
CODE SEGMENT
	in eax
	mov n, eax
	mov eax, 0
	mov a, eax
	mov eax, 1
	mov b, eax
	mov eax, 0
	mov i, eax
debut_while_0:
	mov eax, i
	push eax
	mov eax, n
	pop ebx
	sub ebx, eax
	jl vrai_jl_1
	mov eax, 0
	jmp fin_jl_1
vrai_jl_1:
	mov eax, 1
fin_jl_1:
	jz fin_while_0
	mov eax, a
	push eax
	mov eax, b
	pop ebx
	add eax, ebx
	mov aux, eax
	mov eax, b
	mov a, eax
	mov eax, aux
	mov b, eax
	mov eax, i
	push eax
	mov eax, 1
	pop ebx
	add eax, ebx
	mov i, eax
	jmp debut_while_0
fin_while_0:
	mov eax, a
	out eax
CODE ENDS
